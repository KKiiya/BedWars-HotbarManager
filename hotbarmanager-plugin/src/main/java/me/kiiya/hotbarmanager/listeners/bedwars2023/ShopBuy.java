package me.kiiya.hotbarmanager.listeners.bedwars2023;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.shop.ICategoryContent;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.arena.team.ITeam;
import com.tomkeuper.bedwars.api.arena.team.TeamEnchant;
import com.tomkeuper.bedwars.api.events.shop.ShopBuyEvent;
import com.tomkeuper.bedwars.api.server.VersionSupport;
import com.tomkeuper.bedwars.configuration.Sounds;
import com.tomkeuper.bedwars.shop.ShopCache;
import com.tomkeuper.bedwars.shop.main.CategoryContent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class ShopBuy implements Listener {

    // Debounce cache to limit rapid purchases
    private static final HashMap<Player, Long> debounceCache = new HashMap<>();
    private static final long DEBOUNCE_THRESHOLD = 300; // 300 ms

    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        Player player = e.getBuyer();
        PlayerInventory inventory = player.getInventory();
        IHotbarPlayer hotbarPlayer = HotbarManager.getAPI().getHotbarPlayer(player);
        Category category = HotbarUtils.getCategoryFromString(e.getCategoryContent().getIdentifier());
        List<Category> hotbar = hotbarPlayer.getHotbarAsList();

        // Check for debounce
        if (!isDebounceAllowed(player)) {
            player.sendMessage(Utility.getMsg(player, "shop-too-fast"));
            return;
        }

        // Validate category
        if (category == null || category == Category.NONE) return;

        // Cache commonly used data
        VersionSupport versionSupport = HotbarManager.getBW2023Api().getVersionSupport();
        ITeam team = e.getArena().getTeam(player);
        ICategoryContent content = e.getCategoryContent();
        String identifier = content.getIdentifier();
        ShopCache shopCache = ShopCache.getInstance().getShopCache(player.getUniqueId());
        ShopCache.CachedItem cachedItem = (ShopCache.CachedItem) shopCache.getCachedItem(content);
        IContentTier contentTier = content.getContentTiers().get(cachedItem.getTier() - 1);

        // Handle purchase inside a synchronized block
        synchronized (player) {
            Material currency = contentTier.getCurrency();
            int price = contentTier.getPrice();
            ItemStack item = Utility.formatItemStack(contentTier.getBuyItemsList().get(0).getItemStack(), e.getArena().getTeam(player));

            if (inventory.firstEmpty() == -1) {
                player.sendMessage(Utility.getMsg(player, "upgrades-lore-insuff-space")
                        .replace("%bw_lang_prefix%", Utility.getMsg(player, "prefix")));
                return;
            }

            // Handle sword replacement
            if (BedWars.nms.isSword(item)) {
                inventory.remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
            }

            // Process unbreakable and upgrade identifier
            if (BedWars.nms.isTool(item) || item.getType() == Material.SHEARS) {
                makeUnbreakable(item);
                versionSupport.setShopUpgradeIdentifier(item, identifier);
            }

            // Attempt to place in the hotbar
            boolean placed = placeInHotbar(player, inventory, hotbar, category, item, content, versionSupport, currency, price, team);
            if (!placed) {
                inventory.addItem(item);
            }

            // Deduct currency and play sound
            CategoryContent.takeMoney(player, currency, price);
            Sounds.playSound("shop-bought", player);
        }

        // Notify player asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(HotbarManager.getInstance(), () -> {
            player.sendMessage(Utility.getMsg(player, "shop-new-purchase")
                    .replace("%bw_prefix%", Utility.getMsg(player, "prefix"))
                    .replace("%bw_lang_prefix%", Utility.getMsg(player, "prefix"))
                    .replace("%bw_item%", Utility.getMsg(player, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                    .replace("%bw_color%", "")
                    .replace("%bw_tier%", BedWars.nms.isTool(item) ? CategoryContent.getRomanNumber(cachedItem.getTier()) : ""));
        });

        e.setCancelled(true);
    }

    private boolean isDebounceAllowed(Player player) {
        long currentTime = System.currentTimeMillis();
        if (debounceCache.containsKey(player)) {
            long lastPurchaseTime = debounceCache.get(player);
            if (currentTime - lastPurchaseTime < DEBOUNCE_THRESHOLD) {
                return false;
            }
        }
        debounceCache.put(player, currentTime);
        return true;
    }

    private void makeUnbreakable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        BedWars.nms.setUnbreakable(meta);
        itemStack.setItemMeta(meta);
    }

    private boolean placeInHotbar(Player player, PlayerInventory inventory, List<Category> hotbar, Category category, ItemStack item, ICategoryContent content, VersionSupport versionSupport, Material currency, int price, ITeam team) {
        for (int i = 0; i < 9; i++) {
            ItemStack slotItem = inventory.getItem(i);
            if (hotbar.get(i) != category) continue;

            if (slotItem == null) {
                inventory.setItem(i, prepareItemForHotbar(item, content, versionSupport, team));
                return true;
            }

            if (BedWars.nms.isSword(item) && hotbar.get(i) == Category.MELEE) {
                if (Utility.isItemHigherTier(item, slotItem)) {
                    inventory.setItem(i, prepareItemForHotbar(item, content, versionSupport, team));
                    return true;
                }
            } else if (slotItem.getType() == item.getType() && slotItem.getAmount() + item.getAmount() <= slotItem.getMaxStackSize()) {
                slotItem.setAmount(slotItem.getAmount() + item.getAmount());
                return true;
            }
        }
        return false;
    }

    private ItemStack prepareItemForHotbar(ItemStack item, ICategoryContent content, VersionSupport versionSupport, ITeam team) {
        makeUnbreakable(item);
        if (BedWars.nms.isSword(item)) {
            for (TeamEnchant enchant : team.getSwordsEnchantments()) {
                item.addEnchantment(enchant.getEnchantment(), enchant.getAmplifier());
            }
        }
        return versionSupport.setShopUpgradeIdentifier(item, content.getIdentifier());
    }
}

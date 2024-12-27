package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.TeamEnchant;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.api.server.VersionSupport;
import com.andrei1058.bedwars.configuration.Sounds;
import com.andrei1058.bedwars.shop.ShopCache;
import com.andrei1058.bedwars.shop.main.CategoryContent;
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

        // Debounce check
        if (!isDebounceAllowed(player)) {
            player.sendMessage(Utility.getMsg(player, "shop-too-fast"));
            return;
        }

        // Validate category
        if (category == null || category == Category.NONE) return;

        // Cache commonly used data
        VersionSupport versionSupport = HotbarManager.getBW1058Api().getVersionSupport();
        ITeam team = e.getArena().getTeam(player);
        CategoryContent content = (CategoryContent) e.getCategoryContent();
        String identifier = content.getIdentifier();
        ShopCache shopCache = ShopCache.getShopCache(player.getUniqueId());
        ShopCache.CachedItem cachedItem = shopCache.getCachedItem(content);
        IContentTier contentTier = content.getContentTiers().get(cachedItem.getTier() - 1);

        synchronized (player) {
            Material currency = contentTier.getCurrency();
            int price = contentTier.getPrice();
            ItemStack item = Utility.formatItemStack(contentTier.getBuyItemsList().get(0).getItemStack(), team);

            if (inventory.firstEmpty() == -1) {
                player.sendMessage(Utility.getMsg(player, "upgrades-lore-insuff-space")
                        .replace("{prefix}", Utility.getMsg(player, "prefix")));
                return;
            }

            if (BedWars.nms.isSword(item)) {
                inventory.remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
            }

            if (BedWars.nms.isTool(item) || item.getType() == Material.SHEARS) {
                makeUnbreakable(item);
                versionSupport.setShopUpgradeIdentifier(item, identifier);
            }

            // Try placing item in hotbar
            boolean placed = placeInHotbar(player, inventory, hotbar, category, item, content, versionSupport, currency, price, team);
            if (!placed) {
                inventory.addItem(item);
            }

            CategoryContent.takeMoney(player, currency, price);
            Sounds.playSound("shop-bought", player);
        }

        Bukkit.getScheduler().runTaskAsynchronously(HotbarManager.getInstance(), () -> {
            player.sendMessage(Utility.getMsg(player, "shop-new-purchase")
                    .replace("{prefix}", Utility.getMsg(player, "prefix"))
                    .replace("{item}", Utility.getMsg(player, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                    .replace("{color}", "")
                    .replace("{tier}", BedWars.nms.isTool(item) ? CategoryContent.getRomanNumber(cachedItem.getTier()) : ""));
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

    private boolean placeInHotbar(Player player, PlayerInventory inventory, List<Category> hotbar, Category category, ItemStack item, CategoryContent content, VersionSupport versionSupport, Material currency, int price, ITeam team) {
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

    private ItemStack prepareItemForHotbar(ItemStack item, CategoryContent content, VersionSupport versionSupport, ITeam team) {
        makeUnbreakable(item);
        if (BedWars.nms.isSword(item)) {
            for (TeamEnchant enchant : team.getSwordsEnchantments()) {
                item.addEnchantment(enchant.getEnchantment(), enchant.getAmplifier());
            }
        }
        return versionSupport.setShopUpgradeIdentifier(item, content.getIdentifier());
    }
}

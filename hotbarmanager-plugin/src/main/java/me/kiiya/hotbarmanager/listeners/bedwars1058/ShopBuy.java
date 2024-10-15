package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.team.TeamEnchant;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.configuration.Sounds;
import com.andrei1058.bedwars.shop.main.CategoryContent;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.server.VersionSupport;
import com.andrei1058.bedwars.shop.ShopCache;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShopBuy implements Listener {
    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        // MAIN VARIABLES
        Player p = e.getBuyer();
        PlayerInventory inv = p.getInventory();
        IHotbarPlayer hp = HotbarManager.getAPI().getHotbarPlayer(e.getBuyer());
        Category cat = HotbarUtils.getCategoryFromString(e.getCategoryContent().getIdentifier());
        List<Category> hotbar = hp.getHotbarAsList();

        // CHECKS
        if (cat == null || cat == Category.NONE) return;

        // ITEM BOUGHT VARIABLES
        VersionSupport vs = HotbarManager.getBW1058Api().getVersionSupport();
        ITeam t = e.getArena().getTeam(p);
        CategoryContent cc = (CategoryContent) e.getCategoryContent();
        String identifier = cc.getIdentifier();
        ShopCache cache = ShopCache.getShopCache(p.getUniqueId());
        cache.upgradeCachedItem(cc, cc.getSlot());
        ShopCache.CachedItem cachedItem = cache.getCachedItem(cc);
        IContentTier upgradableContent = cc.getContentTiers().get(cachedItem.getTier()-1);

        Material currency = upgradableContent.getCurrency();
        int price = upgradableContent.getPrice();

        ItemStack item = BedWars.nms.addCustomData(Utility.formatItemStack(upgradableContent.getBuyItemsList().get(0).getItemStack(), e.getArena().getTeam(p)), "");

        if (BedWars.nms.isSword(item)) {
            inv.remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
        }

        if (BedWars.nms.isTool(item) || item.getType() == Material.SHEARS) {
            unbreakable(item);
            vs.setShopUpgradeIdentifier(item, identifier);
        }

        // SOUNDS
        String buySound = "shop-bought";

        if (!hotbar.contains(cat)) return;

        if (inv.firstEmpty() == -1) {
            p.sendMessage(Utility.getMsg(p, "upgrades-lore-insuff-space")
                    .replace("{prefix}", Utility.getMsg(p, "prefix")));
            return;
        }

        try {
            for (int i = 0; i < 9; i++) {
                ItemStack itemSlot = p.getInventory().getItem(i);
                if (hotbar.get(i) != cat) continue;

                if (itemSlot != null) {

                    if (BedWars.nms.isSword(item) && hotbar.get(i) == Category.MELEE) {
                        if (Utility.isItemHigherTier(item, itemSlot)) {
                            inv.addItem(itemSlot);
                            unbreakable(item);
                            for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                                item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                            }
                            inv.setItem(i, item);
                            CategoryContent.takeMoney(p, currency, price);
                        } else {
                            unbreakable(item);
                            for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                                item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                            }
                            inv.addItem(item);
                            CategoryContent.takeMoney(p, currency, price);
                        }
                    } else if (vs.getShopUpgradeIdentifier(item) != null && vs.getShopUpgradeIdentifier(item).equals(identifier)) {
                        inv.setItem(i, vs.setShopUpgradeIdentifier(item, identifier));
                        CategoryContent.takeMoney(p, currency, price);
                    } else if (item.getType() == itemSlot.getType() && item.getDurability() == itemSlot.getDurability()) {
                        if (itemSlot.getAmount() + item.getAmount() > itemSlot.getType().getMaxStackSize()) {
                            int restAmount = itemSlot.getAmount() + item.getAmount() - itemSlot.getType().getMaxStackSize();
                            ItemStack finalItem = new ItemStack(item.getType(), restAmount);
                            Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> {
                                itemSlot.setAmount(itemSlot.getType().getMaxStackSize());
                                inv.addItem(BedWars.nms.addCustomData(Utility.formatItemStack(finalItem, t), ""));
                                CategoryContent.takeMoney(p, currency, price);
                            }, 1L);
                        } else {
                            itemSlot.setAmount(itemSlot.getAmount() + item.getAmount());
                            CategoryContent.takeMoney(p, currency, price);
                        }
                    } else {
                        if (Utility.getItemCategory(itemSlot) == cat && !vs.getShopUpgradeIdentifier(itemSlot).equalsIgnoreCase(identifier)) continue;

                        if (cc.isPermanent()) {
                            unbreakable(item);
                            item = vs.setShopUpgradeIdentifier(item, identifier);
                        }
                        if (BedWars.nms.isSword(item)) {
                            unbreakable(item);
                            for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                                item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                            }
                        }
                        inv.setItem(i, item);
                        CategoryContent.takeMoney(p, currency, price);
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getInstance(), () -> inv.addItem(itemSlot), 2L);
                    }
                } else {
                    if (cc.isPermanent()) {
                        unbreakable(item);
                        item = vs.setShopUpgradeIdentifier(item, identifier);
                    }
                    if (BedWars.nms.isSword(item)) {
                        unbreakable(item);
                        for (TeamEnchant teamEnchant : t.getSwordsEnchantments()) {
                            item.addEnchantment(teamEnchant.getEnchantment(), teamEnchant.getAmplifier());
                        }
                    }
                    inv.setItem(i, item);
                    CategoryContent.takeMoney(p, currency, price);
                }

                Sounds.playSound(buySound, p);
                e.setCancelled(true);
                p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                        .replace("{prefix}", Utility.getMsg(p, "prefix"))
                        .replace("{item}", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                        .replace("{color}", "")
                        .replace("{tier}", !BedWars.nms.isTool(item) ? "" : CategoryContent.getRomanNumber(cachedItem.getTier())));
                break;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void unbreakable(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        BedWars.nms.setUnbreakable(meta);
        itemStack.setItemMeta(meta);
    }
}
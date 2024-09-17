package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.shop.ICategoryContent;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.arena.team.ITeam;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShopBuy implements Listener {
    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        // MAIN VARIABLES
        Player p = e.getBuyer();
        IHotbarPlayer hp = HotbarManager.getAPI().getHotbarPlayer(e.getBuyer());
        Category cat = HotbarUtils.getCategoryFromString(e.getCategoryContent().getIdentifier());
        List<Category> hotbar = hp.getHotbarAsList();

        // CHECKS
        if (cat == null || cat == Category.NONE) return;

        // ITEM BOUGHT VARIABLES
        VersionSupport vs = HotbarManager.getBW1058Api().getVersionSupport();
        ITeam t = e.getArena().getTeam(p);
        ICategoryContent cc = e.getCategoryContent();
        String identifier = cc.getIdentifier();
        IContentTier content = cc.getContentTiers().get(0);
        ShopCache cache = ShopCache.getShopCache(p.getUniqueId());
        AtomicReference<ShopCache.CachedItem> cachedItem = new AtomicReference<>(cache.getCachedItem(identifier));
        IContentTier tier = cachedItem.get() == null ? cc.getContentTiers().get(0) : e.getCategoryContent().getContentTiers().get(cachedItem.get().getTier()-1);
        Material currency = tier.getCurrency();
        int price = content.getPrice();
        ItemStack item = Utility.formatItemStack(content.getBuyItemsList().get(0).getItemStack(), t);
        if (BedWars.nms.isSword(item)) {
            p.getInventory().remove(Material.getMaterial(BedWars.getForCurrentVersion("WOOD_SWORD", "WOOD_SWORD", "WOODEN_SWORD")));
        }
        if (BedWars.nms.isTool(item)) {
            ItemMeta meta = item.getItemMeta();
            BedWars.nms.setUnbreakable(meta);
            item.setItemMeta(meta);
        }

        // SOUNDS
        String buySound = "shop-bought";

        if (!hotbar.contains(cat)) return;

        if (p.getInventory().firstEmpty() == -1) {
            e.setCancelled(true);
            return;
        }

        try {
            for (int i = 0; i < 9; i++) {
                ItemStack itemSlot = p.getInventory().getItem(i);
                if (hotbar.get(i) != cat) continue;

                e.setCancelled(true);

                if (itemSlot != null) {
                    if (vs.getShopUpgradeIdentifier(item) != null && vs.getShopUpgradeIdentifier(item).equals(identifier)) {
                        cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                        cachedItem.set(cache.getCachedItem(identifier));
                        item = cc.getContentTiers().get(cachedItem.get().getTier() - 1).getBuyItemsList().get(0).getItemStack();
                        p.getInventory().setItem(i, vs.setShopUpgradeIdentifier(item, identifier));
                        CategoryContent.takeMoney(p, currency, price);
                    } else if (item.getType() == itemSlot.getType() && item.getDurability() == itemSlot.getDurability()) {
                        if (itemSlot.getAmount() + item.getAmount() > itemSlot.getType().getMaxStackSize()) {
                            int restAmount = itemSlot.getAmount() + item.getAmount() - itemSlot.getType().getMaxStackSize();
                            ItemStack finalItem = new ItemStack(item.getType(), restAmount);
                            Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
                                itemSlot.setAmount(itemSlot.getType().getMaxStackSize());
                                p.getInventory().addItem(BedWars.nms.addCustomData(Utility.formatItemStack(finalItem, t), ""));
                                CategoryContent.takeMoney(p, currency, price);
                            }, 1L);
                        } else {
                            itemSlot.setAmount(itemSlot.getAmount() + item.getAmount());
                            CategoryContent.takeMoney(p, currency, price);
                        }
                    } else {
                        if (Utility.getItemCategory(itemSlot) == cat) continue;

                        int finalI = i;
                        AtomicReference<ItemStack> finalItem = new AtomicReference<>(item);
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
                            if (cc.isPermanent()) {
                                cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                                cachedItem.set(cache.getCachedItem(identifier));
                                finalItem.set(cc.getContentTiers().get(cachedItem.get().getTier() - 1).getBuyItemsList().get(0).getItemStack());
                                finalItem.set(BedWars.nms.addCustomData(Utility.formatItemStack(finalItem.get(), t), ""));
                                p.getInventory().setItem(finalI, vs.setShopUpgradeIdentifier(finalItem.get(), identifier));
                            } else {
                                p.getInventory().setItem(finalI, BedWars.nms.addCustomData(finalItem.get(), ""));
                            }
                            CategoryContent.takeMoney(p, currency, price);
                        }, 1L);
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> p.getInventory().addItem(itemSlot), 2L);
                    }
                } else {
                    if (cc.isPermanent()) {
                        cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                        cachedItem.set(cache.getCachedItem(identifier));
                        item = cc.getContentTiers().get(cachedItem.get().getTier() - 1).getBuyItemsList().get(0).getItemStack();
                        item = Utility.formatItemStack(item, t);
                        ItemMeta meta = item.getItemMeta();
                        BedWars.nms.setUnbreakable(meta);
                        item.setItemMeta(meta);
                        p.getInventory().setItem(i, vs.setShopUpgradeIdentifier(item, identifier));
                        CategoryContent.takeMoney(p, currency, price);
                    } else {
                        p.getInventory().setItem(i, item);
                        CategoryContent.takeMoney(p, currency, price);
                    }
                }

                Sounds.playSound(buySound, p);
                p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                        .replace("{prefix}", Utility.getMsg(p, "prefix"))
                        .replace("{item}", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                        .replace("{color}", "")
                        .replace("{tier}", cachedItem.get() == null ? "" : CategoryContent.getRomanNumber(cachedItem.get().getTier())));
                break;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.shop.ICategoryContent;
import com.andrei1058.bedwars.api.arena.shop.IContentTier;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.shop.ShopCache;
import com.andrei1058.bedwars.shop.main.CategoryContent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

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
        ICategoryContent cc = e.getCategoryContent();
        String identifier = cc.getIdentifier();
        IContentTier content = cc.getContentTiers().get(0);
        ShopCache cache = ShopCache.getShopCache(p.getUniqueId());
        ShopCache.CachedItem cachedItem = cache.getCachedItem(identifier);
        IContentTier tier = cachedItem == null ? cc.getContentTiers().get(0) : e.getCategoryContent().getContentTiers().get(cachedItem.getTier()-1);
        Material currency = tier.getCurrency();
        int price = content.getPrice();
        ItemStack item = Utility.formatItemStack(content.getBuyItemsList().get(0).getItemStack(), e.getArena().getTeam(p));
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(new File(BedWars.plugin.getDataFolder(), "sounds.yml"));

        // SOUNDS
        String buySound = sounds.getString("shop-bought.sound");
        float buySoundPitch = (float) sounds.getDouble("shop-bought.pitch");
        float buySoundVolume = (float) sounds.getDouble("shop-bought.volume");


        if (!hotbar.contains(cat)) return;

        if (p.getInventory().firstEmpty() == -1) {
            e.setCancelled(true);
            return;
        }

        try {
            for (int i = 0; i < 9; i++) {
                if (hotbar.get(i) == cat) {
                    if (p.getInventory().getItem(i) != null) {
                        if (HotbarManager.getBW1058Api().getVersionSupport().getTag(p.getInventory().getItem(i), "identifier") != null && HotbarManager.getBW1058Api().getVersionSupport().getTag(p.getInventory().getItem(i), "identifier").equals(identifier)) {
                            cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                            cachedItem = cache.getCachedItem(identifier);
                            item = cc.getContentTiers().get(cachedItem.getTier()-1).getBuyItemsList().get(0).getItemStack();
                            item = Utility.formatItemStack(item, e.getArena().getTeam(p));
                            p.getInventory().setItem(i, HotbarManager.getBW1058Api().getVersionSupport().setTag(item, "identifier", identifier));
                        } else {
                            if (item.getType() == p.getInventory().getItem(i).getType() && item.getDurability() == p.getInventory().getItem(i).getDurability()) {
                                if (p.getInventory().getItem(i).getAmount() >= p.getInventory().getItem(i).getType().getMaxStackSize())
                                    continue;
                                else {
                                    if (p.getInventory().getItem(i).getAmount() + item.getAmount() > p.getInventory().getItem(i).getType().getMaxStackSize()) {
                                        int finalI = i;
                                        int restAmount = p.getInventory().getItem(i).getAmount() - p.getInventory().getItem(i).getType().getMaxStackSize() + item.getAmount();
                                        ItemStack finalItem = item;
                                        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> {
                                            p.getInventory().getItem(finalI).setAmount(p.getInventory().getItem(finalI).getType().getMaxStackSize());
                                            p.getInventory().addItem(new ItemStack(finalItem.getType(), restAmount));
                                        }, 1L);
                                    } else {
                                        p.getInventory().getItem(i).setAmount(p.getInventory().getItem(i).getAmount() + item.getAmount());
                                    }
                                }
                            } else {
                                if (Utility.getItemCategory(p.getInventory().getItem(i)) == cat) continue;
                                ItemStack addedItem = p.getInventory().getItem(i);
                                int finalI = i;
                                ItemStack finalItem1 = item;
                                Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> p.getInventory().setItem(finalI, finalItem1), 1L);
                                Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> p.getInventory().addItem(addedItem), 2L);
                            }
                        }
                    } else {
                        if (cc.isPermanent()) {
                            cache.upgradeCachedItem((CategoryContent) cc, cc.getSlot());
                            cachedItem = cache.getCachedItem(identifier);
                            item = cc.getContentTiers().get(cachedItem.getTier()-1).getBuyItemsList().get(0).getItemStack();
                            item = Utility.formatItemStack(item, e.getArena().getTeam(p));
                            p.getInventory().setItem(i, HotbarManager.getBW1058Api().getVersionSupport().setTag(item, "identifier", identifier));
                        } else {
                            p.getInventory().setItem(i, item);
                        }
                    }

                    Utility.playSound(p, buySound, buySoundVolume, buySoundPitch);
                    p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                            .replace("{prefix}", Utility.getMsg(p, "prefix"))
                            .replace("{item}", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                            .replace("{color}", "")
                            .replace("{tier}", cachedItem == null ? "" : CategoryContent.getRomanNumber(cachedItem.getTier())));
                    CategoryContent.takeMoney(p, currency, price);
                    e.setCancelled(true);
                    break;
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

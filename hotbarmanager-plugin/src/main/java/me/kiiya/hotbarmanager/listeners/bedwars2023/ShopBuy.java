package me.kiiya.hotbarmanager.listeners.bedwars2023;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.shop.IContentTier;
import com.tomkeuper.bedwars.api.events.shop.ShopBuyEvent;
import com.tomkeuper.bedwars.api.shop.ICachedItem;
import com.tomkeuper.bedwars.shop.ShopCache;
import com.tomkeuper.bedwars.shop.main.CategoryContent;
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
        String identifier = e.getCategoryContent().getIdentifier();
        IContentTier content = e.getCategoryContent().getContentTiers().get(0);
        ShopCache cache = ShopCache.getInstance().getShopCache(p.getUniqueId());
        ICachedItem cachedItem = cache.getCachedItem(identifier);
        IContentTier tier = e.getCategoryContent().getContentTiers().size() > 1 ? e.getCategoryContent().getContentTiers().get(1) : null;
        Material currency = CategoryContent.getCurrency(CategoryContent.getCurrencyMsgPath(content));
        int price = content.getPrice();
        int amount = content.getBuyItemsList().get(0).getItemStack().getAmount();
        ItemStack item = Utility.formatItemStack(new ItemStack(e.getCategoryContent().getItemStack(p).getType(), amount), e.getArena().getTeam(p));
        YamlConfiguration sounds = YamlConfiguration.loadConfiguration(new File(BedWars.plugin.getDataFolder(), "sounds.yml"));

        // SOUNDS
        String buySound = sounds.getString("shop-bought.sound");
        float buySoundPitch = (float) sounds.getDouble("shop-bought.pitch");
        float buySoundVolume = (float) sounds.getDouble("shop-bought.volume");


        if (!hotbar.contains(cat)) return;

        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(Utility.getMsg(p, "upgrades-lore-insuff-space")
                    .replace("%bw_prefix%", Utility.getMsg(p, "prefix"))
                    .replace("%bw_lang_prefix%", Utility.getMsg(p, "prefix")));
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (hotbar.get(i) == cat) {
                Utility.info("Found category " + cat + " at slot " + i);
                CategoryContent.takeMoney(p, currency, price);
                Utility.info("Took " + price + " " + currency + " from " + p.getName());
                if (p.getInventory().getItem(i) != null) {
                    if (item.getType() == p.getInventory().getItem(i).getType() && item.getDurability() == p.getInventory().getItem(i).getDurability()) {
                        if (p.getInventory().getItem(i).getAmount() >= p.getInventory().getItem(i).getType().getMaxStackSize()) continue;
                        else {
                            if (p.getInventory().getItem(i).getAmount() + item.getAmount() > p.getInventory().getItem(i).getType().getMaxStackSize()) {
                                int finalI = i;
                                int restAmount = p.getInventory().getItem(i).getAmount() - p.getInventory().getItem(i).getType().getMaxStackSize() + item.getAmount();
                                Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () ->{
                                    p.getInventory().getItem(finalI).setAmount(p.getInventory().getItem(finalI).getType().getMaxStackSize());
                                    p.getInventory().addItem(new ItemStack(item.getType(), restAmount));
                                }, 1L);
                            } else {
                                p.getInventory().getItem(i).setAmount(p.getInventory().getItem(i).getAmount() + item.getAmount());
                            }
                        }
                    } else {
                        if (Utility.getItemCategory(p.getInventory().getItem(i)) == cat) continue;
                        ItemStack addedItem = p.getInventory().getItem(i);
                        int finalI = i;
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> p.getInventory().setItem(finalI, item), 1L);
                        Bukkit.getScheduler().runTaskLater(HotbarManager.getPlugins(), () -> p.getInventory().addItem(addedItem), 2L);
                    }
                } else {
                    p.getInventory().setItem(i, item);
                }
                Utility.playSound(p, buySound, buySoundVolume, buySoundPitch);
                p.sendMessage(Utility.getMsg(p, "shop-new-purchase")
                        .replace("%bw_prefix%", Utility.getMsg(p, "prefix"))
                        .replace("%bw_lang_prefix%", Utility.getMsg(p, "prefix"))
                        .replace("%bw_item%", Utility.getMsg(p, "shop-items-messages." + identifier.split("\\.")[0] + ".content-item-" + identifier.split("\\.")[2] + "-name"))
                        .replace("%bw_color%", "")
                        .replace("%bw_tier%", tier == null ? "" : CategoryContent.getRomanNumber(cachedItem.getTier())));
                e.setCancelled(true);
                break;
            }
        }
    }
}

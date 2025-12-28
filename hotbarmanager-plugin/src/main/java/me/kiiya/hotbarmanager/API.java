package me.kiiya.hotbarmanager;


import me.kiiya.hotbarmanager.api.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.api.hotbar.SortType;
import me.kiiya.hotbarmanager.api.menu.IShopCacheManager;
import me.kiiya.hotbarmanager.menu.HotbarManagerMenu;
import me.kiiya.hotbarmanager.menu.ShopInventoryManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API implements HotbarManager {

    @Override
    public SortType getSortType() {
        return me.kiiya.hotbarmanager.HotbarManager.getManager().getSortType();
    }

    @Override
    public IHotbarPlayer getHotbarPlayer(Player player) {
        return me.kiiya.hotbarmanager.HotbarManager.getManager().getHotbarPlayer(player);
    }

    @Override
    public IHotbarPlayer getHotbarPlayer(UUID uuid) {
        return me.kiiya.hotbarmanager.HotbarManager.getManager().getHotbarPlayer(uuid);
    }

    @Override
    public IHotbarManager getHotbarManager() {
        return me.kiiya.hotbarmanager.HotbarManager.getManager();
    }

    @Override
    public IShopCacheManager getCacheManager(String group) {
        return me.kiiya.hotbarmanager.HotbarManager.getCacheManager(group);
    }


    @Override
    public HotbarManager.MenuUtil getMenuUtil() {
        return new MenuUtil();
    }

    public static class MenuUtil implements HotbarManager.MenuUtil {
        public void openHotbarMenu(Player player) {
            new HotbarManagerMenu(player);
        }

        @Override
        public void openPerItemMenu(Player player) {
            new ShopInventoryManager(player, "default");
        }

        @Override
        public void openPerItemMenu(Player player, String group) {
            new ShopInventoryManager(player, group);
        }
    }
}

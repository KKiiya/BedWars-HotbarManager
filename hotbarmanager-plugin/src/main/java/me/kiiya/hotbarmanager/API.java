package me.kiiya.hotbarmanager;

import me.kiiya.hotbarmanager.api.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.menu.HotbarManagerMenu;
import me.kiiya.hotbarmanager.player.HotbarPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API implements HotbarManager {
    @Override
    public IHotbarPlayer getHotbarPlayer(Player player) {
        return new HotbarPlayer(player);
    }

    @Override
    public IHotbarPlayer getHotbarPlayer(UUID uuid) {
        return new HotbarPlayer(uuid);
    }

    @Override
    public HotbarManager.MenuUtil getMenuUtil() {
        return new MenuUtil();
    }

    public static class MenuUtil implements HotbarManager.MenuUtil {
        public void openHotbarMenu(Player player) {
            new HotbarManagerMenu(player);
        }
    }
}

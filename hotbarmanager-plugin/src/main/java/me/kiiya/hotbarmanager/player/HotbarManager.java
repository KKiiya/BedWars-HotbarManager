package me.kiiya.hotbarmanager.player;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.config.MainConfig;
import me.kiiya.hotbarmanager.utils.Utility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class HotbarManager implements IHotbarManager, Listener {

    private static HotbarManager instance;

    private final HashMap<String, IHotbarPlayer> playersMap;
    private final List<Category> defaultSlots;
    private final MainConfig mainConfig;

    private HotbarManager() {
        this.playersMap = new HashMap<>();
        this.mainConfig = me.kiiya.hotbarmanager.HotbarManager.getMainConfig();
        this.defaultSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            defaultSlots.add(Category.getFromString(mainConfig.getString("default-slots."+i)));
        }
        Bukkit.getPluginManager().registerEvents(this, me.kiiya.hotbarmanager.HotbarManager.getInstance());
    }

    public static IHotbarManager getInstance() {
        return instance;
    }

    public static HotbarManager getPrivateInstance() {
        return instance;
    }

    public HashMap<String, IHotbarPlayer> getPlayersMap() {
        return playersMap;
    }

    public static IHotbarManager init() {
        if (instance != null) throw new RuntimeException("Tried instancing HotbarManager class while already instanced");
        instance = new HotbarManager();
        return instance;
    }

    @Override
    public IHotbarPlayer getHotbarPlayer(OfflinePlayer player) {
        return playersMap.get(player.getUniqueId().toString());
    }

    @Override
    public IHotbarPlayer getHotbarPlayer(UUID uuid) {
        return playersMap.get(uuid.toString());
    }

    @Override
    public List<Category> getDefaultSlots() {
        return Collections.unmodifiableList(defaultSlots);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        me.kiiya.hotbarmanager.HotbarManager.getInstance().getDB().createPlayerData(e.getPlayer(), defaultSlots);
        playersMap.put(e.getPlayer().getUniqueId().toString(), new HotbarPlayer(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        IHotbarPlayer hp = me.kiiya.hotbarmanager.HotbarManager.getAPI().getHotbarPlayer(e.getPlayer());
        if (hp == null) return;

        hp.saveHotbar();
        hp.destroy();
    }
}

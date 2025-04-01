package me.kiiya.hotbarmanager.player;

import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

import static me.kiiya.hotbarmanager.utils.Utility.debug;

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

    @Override
    @Deprecated
    public void saveHotbars(boolean destroy) {
        for (IHotbarPlayer hp : playersMap.values()) {
            hp.saveHotbar(destroy, true);
            if (destroy) hp.destroy(false);
        }
    }

    @Override
    public void saveHotbars(boolean destroy, boolean runTask) {
        for (IHotbarPlayer hp : playersMap.values()) {
            hp.saveHotbar(destroy, runTask);
            if (destroy) hp.destroy(false);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        Player p = e.getPlayer();

        debug("Player " + p.getName() + " joined the server.");
        me.kiiya.hotbarmanager.HotbarManager.getInstance().getDB().createPlayerData(p, defaultSlots);
        playersMap.put(p.getUniqueId().toString(), new HotbarPlayer(p));
        debug("Player " + p.getName() + " data has been loaded.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (e == null) return;
        if (e.getPlayer() == null) return;

        Player p = e.getPlayer();

        debug("Player " + e.getPlayer().getName() + " left the server.");
        IHotbarPlayer hp = playersMap.get(p.getUniqueId().toString());
        if (hp == null) return;

        hp.destroy(true);
        debug("Player " + p.getName() + " data has been saved and destroyed.");
    }
}

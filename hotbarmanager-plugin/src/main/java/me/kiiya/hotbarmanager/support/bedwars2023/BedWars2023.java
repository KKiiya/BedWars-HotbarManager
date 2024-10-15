package me.kiiya.hotbarmanager.support.bedwars2023;

import com.tomkeuper.bedwars.api.BedWars;
import me.kiiya.hotbarmanager.utils.Support;
import org.bukkit.Bukkit;
import static me.kiiya.hotbarmanager.HotbarManager.bw2023Api;
import static me.kiiya.hotbarmanager.HotbarManager.support;

public class BedWars2023 {

    public BedWars2023() {
        start();
    }

    private void start() {
        support = Support.BEDWARS2023;
        bw2023Api = Bukkit.getServicesManager().getRegistration(BedWars.class).getProvider();

        bw2023Api.getAddonsUtil().registerAddon(new BedWarsAddon());
    }
}

package me.kiiya.hotbarmanager.listeners.bedwars1058;

import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import me.kiiya.hotbarmanager.HotbarManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import me.kiiya.hotbarmanager.api.hotbar.IHotbarPlayer;
import me.kiiya.hotbarmanager.utils.HotbarUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopBuy implements Listener {
    @EventHandler
    public void onBuy(ShopBuyEvent e) {
        IHotbarPlayer p = HotbarManager.getAPI().getHotbarPlayer(e.getBuyer());
        Category cat = HotbarUtils.getCategoryFromString(e.getCategoryContent().getIdentifier());

    }
}

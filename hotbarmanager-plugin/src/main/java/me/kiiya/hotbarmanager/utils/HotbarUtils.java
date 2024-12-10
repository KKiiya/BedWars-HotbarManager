package me.kiiya.hotbarmanager.utils;

import me.kiiya.hotbarmanager.api.config.ConfigManager;
import me.kiiya.hotbarmanager.api.hotbar.Category;
import java.util.HashMap;

public class HotbarUtils {

    private final HashMap<String, Integer> posForSlot;
    private final HashMap<String, Integer> slotForPos;
    private static HotbarUtils instance;

    private HotbarUtils(ConfigManager config) {
        posForSlot = new HashMap<>();
        slotForPos = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            slotForPos.put(i + "", config.getInt("hotbar-slots.position-" + i));
            posForSlot.put(config.getInt("hotbar-slots.position-" + i) + "", i);
        }
    }

    public static void initialize(ConfigManager config) {
        instance = new HotbarUtils(config);
    }

    public static HotbarUtils getInstance() {
        return instance;
    }


    public static Category getCategoryFromString(String path) {
        String category =  ((path.split("\\.")[0]).split("-")[0]).toUpperCase();
        if (!Category.getCategoriesAsStringList().contains(category)) return Category.NONE;
        return Category.getFromString(category);
    }

    public int getSlotForPos(int position) {
        if (slotForPos.get(position + "") == null) return -1;
        return slotForPos.get(position + "");
    }

    public int getPosForSlot(int slot) {
        if (posForSlot.get(slot + "") == null) return -1;
        return posForSlot.get(slot + "");
    }
}

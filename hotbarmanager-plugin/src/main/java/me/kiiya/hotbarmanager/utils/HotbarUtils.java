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

        slotForPos.put("0", config.getInt("hotbar-slots.position-0"));
        slotForPos.put("1", config.getInt("hotbar-slots.position-1"));
        slotForPos.put("2", config.getInt("hotbar-slots.position-2"));
        slotForPos.put("3", config.getInt("hotbar-slots.position-3"));
        slotForPos.put("4", config.getInt("hotbar-slots.position-4"));
        slotForPos.put("5", config.getInt("hotbar-slots.position-5"));
        slotForPos.put("6", config.getInt("hotbar-slots.position-6"));
        slotForPos.put("7", config.getInt("hotbar-slots.position-7"));
        slotForPos.put("8", config.getInt("hotbar-slots.position-8"));

        posForSlot.put(config.getInt("hotbar-slots.position-0") + "", 0);
        posForSlot.put(config.getInt("hotbar-slots.position-1") + "", 1);
        posForSlot.put(config.getInt("hotbar-slots.position-2") + "", 2);
        posForSlot.put(config.getInt("hotbar-slots.position-3") + "", 3);
        posForSlot.put(config.getInt("hotbar-slots.position-4") + "", 4);
        posForSlot.put(config.getInt("hotbar-slots.position-5") + "", 5);
        posForSlot.put(config.getInt("hotbar-slots.position-6") + "", 6);
        posForSlot.put(config.getInt("hotbar-slots.position-7") + "", 7);
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

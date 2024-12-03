package me.kiiya.hotbarmanager.support.version;

import me.kiiya.hotbarmanager.api.support.VersionSupport;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class v1_12_R1 extends VersionSupport {

    @Override
    public ItemStack setItemTag(ItemStack item, String key, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag() != null ? nmsItem.getTag() : new NBTTagCompound();
        tag.setString(key, value);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public String getItemTag(ItemStack item, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag();
        if (tag != null && tag.hasKey(key)) return tag.getString(key);
        return null;
    }

    @Override
    public Material getSkullMaterial() {
        return Material.SKULL_ITEM;
    }

    @Override
    public boolean isPlayerHead(ItemStack item) {
        return item.getType() == Material.SKULL_ITEM && item.getDurability() == 3;
    }
}
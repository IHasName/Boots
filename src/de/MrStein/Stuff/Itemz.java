package de.MrStein.Stuff;

import de.MrStein.Boots;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class Itemz {
    List<String> lores = new ArrayList<>();
    ItemStack stack = null;
    ItemMeta meta = null;
    LeatherArmorMeta lmeta = null;
    Color rgb;
    short damage = -1;

    public Itemz(Material material) {
        stack = new ItemStack(material);
        meta = stack.getItemMeta();
        if (material == Material.LEATHER_BOOTS) {
            lmeta = (LeatherArmorMeta) stack.getItemMeta();
        }
    }

    public Itemz(ItemStack stack) {
        this.stack = stack;
        meta = stack.getItemMeta();
        if (stack.getType() == Material.LEATHER_BOOTS) {
            lmeta = (LeatherArmorMeta) stack.getItemMeta();
        }
    }

    public Itemz setDamage(short damage) {
        this.damage = damage;
        return this;
    }

    public Itemz setDisplayName(String displayName) {
        meta.setDisplayName(displayName);
        if (stack.getType() == Material.LEATHER_BOOTS) {
            lmeta.setDisplayName(displayName);
        }
        return this;
    }

    public Itemz addLore(String lore) {
        String[] lines = lore.split("\n");
        for (int i = 0; i < lines.length; i++) {
            lores.add(lines[i]);
        }
        return this;
    }

    public Itemz addLore(List<String> lore) {
        lores.addAll(lore);
        return this;
    }

    public Itemz rgbColor(int r, int g, int b) {
        if (stack.getType() != Material.LEATHER_BOOTS)
            throw new IllegalArgumentException("Cannot dye non Leather Item");
        rgb = Color.fromRGB(r, g, b);
        return this;
    }

    public ItemStack build() {
        ItemStack stek;
        if (damage > 0) {
            stek = new ItemStack(stack.getType(), 1, damage);
        } else {
            stek = stack;
        }
        if (!lores.isEmpty()) {
            if (stack.getType() == Material.LEATHER_BOOTS) {
                lmeta.setLore(lores);
            } else {
                meta.setLore(lores);
            }
        }
        if (stack.getType() == Material.LEATHER_BOOTS) {
            lmeta.setColor(rgb);
        }
        if (stack.getType() == Material.LEATHER_BOOTS && Boots.config.get().getBoolean("hidenbt")) {
            lmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            stek.setItemMeta(lmeta);
        } else if(stack.getType() != Material.LEATHER_BOOTS && Boots.config.get().getBoolean("hidenbt")) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            stek.setItemMeta(meta);
        }
        return stek;
    }

}

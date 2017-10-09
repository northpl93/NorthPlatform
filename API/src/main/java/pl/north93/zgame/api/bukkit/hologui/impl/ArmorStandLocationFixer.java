package pl.north93.zgame.api.bukkit.hologui.impl;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Klasa poprawiajaca wysokosc armorstanda dla konkretnych itemow
 */
final class ArmorStandLocationFixer
{
    /**
     * Instancja tej klasy.
     */
    public static final ArmorStandLocationFixer INSTANCE = new ArmorStandLocationFixer();

    /**
     * Zawiera liste przedmiotow uznawanych przez Bukkita za solidne,
     * ale renderowane przez klienta jak itemy.
     */
    private static final Collection<Material> SOLID_RENDERED_AS_ITEMS = Arrays.asList(Material.BARRIER);

    public Location fixLocation(final Location location, final ItemStack itemStack)
    {
        double y = location.getY();
        y -= 0.75; // podstawowe wyrownanie 6/8

        final Material type = itemStack.getType();
        if (type == Material.SKULL_ITEM) // glowy maja specjalny rendering
        {
            y += 0.30; // nie bylo liczone, na oko
        }
        else if (this.isSolid(type)) // materialy renderowane jako bloki
        {
            y += 0.375; // 3/8
        }

        return new Location(location.getWorld(), location.getX(), y, location.getZ(), location.getYaw(), location.getPitch());
    }

    private boolean isSolid(final Material material)
    {
        return material.isSolid() && ! SOLID_RENDERED_AS_ITEMS.contains(material);
    }
}

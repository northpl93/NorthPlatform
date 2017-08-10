package pl.arieals.minigame.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import pl.arieals.minigame.bedwars.arena.Team;

public final class TeamArmorUtils
{
    private TeamArmorUtils()
    {
    }

    public static void updateArmor(final Player player, final Team team)
    {
        final Color color = chatColorToColor(team.getColor());

        final ItemStack helmet = createColorArmor(Material.LEATHER_HELMET, color);
        final ItemStack chestPlate = createColorArmor(Material.LEATHER_CHESTPLATE, color);

        final ItemStack[] armorContents = player.getInventory().getArmorContents();
        armorContents[2] = chestPlate;
        armorContents[3] = helmet;
        player.getInventory().setArmorContents(armorContents);
    }

    public static ItemStack createColorArmor(final Material leatherArmor, final Color color)
    {
        final ItemStack item = new ItemStack(leatherArmor, 1);
        final LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
        leatherMeta.setColor(color);
        leatherMeta.spigot().setUnbreakable(true);
        item.setItemMeta(leatherMeta);
        return item;
    }

    public static Color chatColorToColor(final ChatColor color)
    {
        switch (color)
        {
            case BLACK:
                return Color.BLACK;
            case DARK_BLUE:
                return Color.BLUE;
            case DARK_GREEN:
                return Color.GREEN;
            case DARK_AQUA:
                return Color.AQUA;
            case DARK_RED:
                return Color.RED;
            case DARK_PURPLE:
                return Color.PURPLE;
            case GOLD:
                return Color.YELLOW;
            case GRAY:
                return Color.GRAY;
            case DARK_GRAY:
                return Color.GRAY;
            case BLUE:
                return Color.BLUE;
            case GREEN:
                return Color.fromRGB(8375321); //lime
            case AQUA:
                return Color.AQUA;
            case RED:
                return Color.RED;
            case LIGHT_PURPLE:
                return Color.fromRGB(15892389);
            case YELLOW:
                return Color.YELLOW;
            case WHITE:
                return Color.WHITE;
            default:
                throw new IllegalArgumentException(color.toString());
        }
    }

    public static DyeColor chatColorToDyeColor(final ChatColor color)
    {
        switch (color)
        {
            case BLACK:
                return DyeColor.BLACK;
            case DARK_BLUE:
                return DyeColor.BLUE;
            case DARK_GREEN:
                return DyeColor.GREEN;
            case DARK_AQUA:
                return DyeColor.LIGHT_BLUE;
            case DARK_RED:
                return DyeColor.RED;
            case DARK_PURPLE:
                return DyeColor.PURPLE;
            case GOLD:
                return DyeColor.YELLOW;
            case GRAY:
                return DyeColor.GRAY;
            case DARK_GRAY:
                return DyeColor.GRAY;
            case BLUE:
                return DyeColor.BLUE;
            case GREEN:
                return DyeColor.LIME;
            case AQUA:
                return DyeColor.LIGHT_BLUE;
            case RED:
                return DyeColor.RED;
            case LIGHT_PURPLE:
                return DyeColor.PINK;
            case YELLOW:
                return DyeColor.YELLOW;
            case WHITE:
                return DyeColor.WHITE;
            default:
                throw new IllegalArgumentException(color.toString());
        }
    }
}

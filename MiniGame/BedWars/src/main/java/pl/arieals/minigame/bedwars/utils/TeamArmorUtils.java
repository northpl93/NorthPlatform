package pl.arieals.minigame.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
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

        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        final LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(color);
        helmetMeta.spigot().setUnbreakable(true);
        helmet.setItemMeta(helmetMeta);

        final ItemStack chestPlate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        final LeatherArmorMeta chestPlateMeta = (LeatherArmorMeta) chestPlate.getItemMeta();
        chestPlateMeta.setColor(color);
        chestPlateMeta.spigot().setUnbreakable(true);
        chestPlate.setItemMeta(helmetMeta);

        final ItemStack[] armorContents = player.getInventory().getArmorContents();
        armorContents[2] = chestPlate;
        armorContents[3] = helmet;
        player.getInventory().setArmorContents(armorContents);
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
                return Color.GREEN;
            case AQUA:
                return Color.AQUA;
            case RED:
                return Color.RED;
            case LIGHT_PURPLE:
                return Color.PURPLE;
            case YELLOW:
                return Color.YELLOW;
            case WHITE:
                return Color.WHITE;
            default:
                throw new IllegalArgumentException(color.toString());
        }
    }
}

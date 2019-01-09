package pl.north93.northplatform.antycheat.utils;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PlayerConnection;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public final class PlayerUtils
{
    /**
     * Wymusza pełną aktualizacją Entity Metadata (atrybutów) danego gracza.
     *
     * @param player Gracz któremu wymuszamy aktualizację atrybutów.
     */
    public static void updateProperties(final Player player)
    {
        final CraftPlayer craft = INorthPlayer.asCraftPlayer(player);
        final EntityPlayer entity = craft.getHandle();

        final PlayerConnection connection = entity.playerConnection;
        connection.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));
    }
}

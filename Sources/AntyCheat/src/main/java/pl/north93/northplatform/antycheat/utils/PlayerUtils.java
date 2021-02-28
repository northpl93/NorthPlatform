package pl.north93.northplatform.antycheat.utils;

import static pl.north93.northplatform.api.bukkit.utils.nms.EntityTrackerHelper.toNmsPlayer;


import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PlayerConnection;

import org.bukkit.entity.Player;

public final class PlayerUtils
{
    /**
     * Wymusza pełną aktualizacją Entity Metadata (atrybutów) danego gracza.
     *
     * @param player Gracz któremu wymuszamy aktualizację atrybutów.
     */
    public static void updateProperties(final Player player)
    {
        final EntityPlayer entityPlayer = toNmsPlayer(player);

        final PlayerConnection connection = entityPlayer.playerConnection;
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), entityPlayer.getDataWatcher(), true));
    }
}

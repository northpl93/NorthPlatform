package pl.north93.northplatform.api.bukkit.map.impl;

import java.util.Collections;

import net.minecraft.server.v1_12_R1.PacketPlayOutMap;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;

import io.netty.channel.Channel;
import pl.north93.northplatform.api.bukkit.map.IMapCanvas;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

final class CanvasUploader
{
    public static void doUpload(final INorthPlayer player, final int mapId, final IMapCanvas old, final IMapCanvas newCanvas)
    {
        final CraftPlayer craftPlayer = player.getCraftPlayer();

        final PacketPlayOutMap packet = new PacketPlayOutMap(mapId, (byte) 4, false, Collections.emptyList(), newCanvas.getBytes(), 0, 0, 128, 128);

        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channel.writeAndFlush(packet);
    }
}

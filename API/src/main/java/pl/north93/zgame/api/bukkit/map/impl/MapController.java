package pl.north93.zgame.api.bukkit.map.impl;

import java.util.List;

import net.minecraft.server.v1_10_R1.EntityPlayer;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import io.netty.channel.Channel;
import javafx.collections.MapChangeListener;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.bukkit.utils.nms.EntityMetaPacketHelper;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class MapController
{
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private IBukkitExecutor bukkitExecutor;

    @Bean
    private MapController()
    {
    }

    public void handlePlayerEnter(final MapImpl map, final CraftPlayer player)
    {
        final PlayerMapData playerMapData = this.getPlayerMapData(player);
        final int mapId = playerMapData.getMapId(map);

        // wysylamy do gracza informacje o umieszczeniu mapy w ramce
        this.uploadFilledMapItem(player, map.getFrameEntityId(), mapId);

        if (! playerMapData.hasAnyServerCanvas(map))
        {
            // odpalamy rendering mapy i nie robimy dalej sprawdzen czy trzeba zaktualizowac
            // bo trzeba na pewno
            map.getBoard().renderFor(player);
            return;
        }

        if (playerMapData.isClientCanvasMatchesServer(map))
        {
            // canvas bedacy u klienta pasuje do tego na serwerze
            return;
        }

        // uploadujemy canvas serwera do klienta i ustawiamy go jako aktywny u klienta
        playerMapData.uploadServerCanvasToClient(map);
    }

    private void uploadFilledMapItem(final CraftPlayer craftPlayer, final int frameEntityId, final int mapId)
    {
        final EntityMetaPacketHelper helper = new EntityMetaPacketHelper(frameEntityId);

        final ItemStack mapItem = new ItemStack(Material.MAP, 0, (short) mapId);
        helper.addMeta(6, EntityMetaPacketHelper.MetaType.SLOT, mapItem);

        final Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channel.writeAndFlush(helper.complete());
    }

    public void updateFullBoard(final Player player, final BoardImpl board, final MapCanvasImpl mapCanvas)
    {
        final PlayerMapData playerMapData = this.getPlayerMapData(player);

        for (int i = 0; i < board.getWidth(); i++)
        {
            for (int j = 0; j < board.getHeight(); j++)
            {
                final MapCanvasImpl subMapCanvas = mapCanvas.getSubMapCanvas(i, j);
                final MapImpl map = board.getMap(i, j);

                final MapContainer mapContainer = playerMapData.getOrComputeContainer(map);
                mapContainer.setServerCanvas(subMapCanvas);

                if (playerMapData.isMapVisible(map))
                {
                    playerMapData.uploadServerCanvasToClient(map);
                }
            }
        }
    }

    public PlayerMapData getPlayerMapData(final Player player)
    {
        final List<MetadataValue> playerMapData = player.getMetadata("PlayerMapData");
        if (! playerMapData.isEmpty())
        {
            return (PlayerMapData) playerMapData.get(0).value();
        }

        final PlayerMapData newPlayerMapData = new PlayerMapData(player);
        player.setMetadata("PlayerMapData", new FixedMetadataValue(this.apiCore.getPluginMain(), newPlayerMapData));
        return newPlayerMapData;
    }

    public MapChangeListener<? super EntityPlayer, ? super Boolean> trackerListener(final MapImpl map)
    {
        return change -> this.bukkitExecutor.sync(() ->
        {
            final CraftPlayer player = change.getKey().getBukkitEntity();
            if (change.wasAdded())
            {
                this.handlePlayerEnter(map, player);
            }
        });
    }
}

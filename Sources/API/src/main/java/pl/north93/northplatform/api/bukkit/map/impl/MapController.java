package pl.north93.northplatform.api.bukkit.map.impl;

import java.util.List;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import io.netty.channel.Channel;
import lombok.ToString;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.nms.EntityMetaPacketHelper;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@ToString(onlyExplicitlyIncluded = true)
class MapController implements Listener
{
    @Inject
    private IBukkitExecutor bukkitExecutor;
    @Inject
    private IBukkitServerManager serverManager;

    private final RendererScheduler rendererScheduler;

    @Bean
    private MapController()
    {
        this.rendererScheduler = new RendererScheduler(this);
    }

    public void handlePlayerEnter(final MapImpl map, final INorthPlayer player)
    {
        final PlayerMapData playerMapData = this.getPlayerMapData(player);
        final int mapId = playerMapData.getMapId(map);

        // wysylamy do gracza informacje o umieszczeniu mapy w ramce
        this.uploadFilledMapItem(player, map.getFrameEntityId(), mapId);

        if (! playerMapData.hasAnyServerCanvas(map))
        {
            final BoardImpl board = map.getBoard();
            if (board.getRenderer() != null && ! this.rendererScheduler.isRendererScheduled(board, player))
            {
                // odpalamy rendering calej tablicy jesli renderer nie jest nullem
                // i jeszcze nie zaplanowalismy renderowania
                this.doRenderingFor(player, board);
            }

            // w obydwu przypadkach czekamy na zakonczenie renderowania
            return;
        }
        else if (playerMapData.isClientCanvasMatchesServer(map))
        {
            // canvas bedacy u klienta pasuje do tego na serwerze
            return;
        }

        // uploadujemy canvas serwera do klienta i ustawiamy go jako aktywny u klienta
        playerMapData.uploadServerCanvasToClient(map);
    }

    private void uploadFilledMapItem(final INorthPlayer player, final int frameEntityId, final int mapId)
    {
        final EntityMetaPacketHelper helper = new EntityMetaPacketHelper(frameEntityId);

        final ItemStack mapItem = new ItemStack(Material.MAP, 1, (short) mapId);
        helper.addMeta(6, EntityMetaPacketHelper.MetaType.SLOT, mapItem);

        final EntityPlayer entityPlayer = player.getCraftPlayer().getHandle();
        final Channel channel = entityPlayer.playerConnection.networkManager.channel;
        channel.writeAndFlush(helper.complete());
    }

    public void doRenderingFor(final INorthPlayer player, final BoardImpl board)
    {
        this.rendererScheduler.abortIfRenderingInProgress(board, player);
        this.rendererScheduler.scheduleRenderer(player, board);
    }

    public void pushNewCanvasToBoardForPlayer(final INorthPlayer player, final BoardImpl board, final MapCanvasImpl mapCanvas)
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

    public PlayerMapData getPlayerMapData(final INorthPlayer player)
    {
        return player.getPlayerData(PlayerMapData.class, PlayerMapData::new);
    }

    public void deletePlayerMapData(final INorthPlayer player)
    {
        player.removePlayerData(PlayerMapData.class);
    }

    /*default*/ MapImpl getMapFromEntity(final org.bukkit.entity.Entity entity)
    {
        final List<MetadataValue> metadata = entity.getMetadata("map_mapImpl");
        if (metadata.isEmpty())
        {
            return null;
        }

        return (MapImpl) metadata.get(0).value();
    }

    /*default*/ void updateMapInEntity(final ItemFrame itemFrame, final MapImpl map)
    {
        itemFrame.setMetadata("map_mapImpl", this.serverManager.createFixedMetadataValue(map));
    }
}

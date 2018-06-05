package pl.north93.zgame.api.bukkit.map.impl;

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northspigot.event.entity.EntityTrackedPlayerEvent;
import pl.north93.zgame.api.bukkit.packets.event.AsyncPacketOutEvent;
import pl.north93.zgame.api.bukkit.packets.wrappers.WrapperPlayOutEntityMetadata;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class MapListener implements AutoListener
{
    @Inject
    private MapManagerImpl mapManager;
    @Inject
    private MapController  mapController;

    @EventHandler
    public void handleMapUploadWhenTracked(final EntityTrackedPlayerEvent event)
    {
        final MapImpl map = this.mapController.getMapFromEntity(event.getEntity());
        if (map == null)
        {
            return;
        }

        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        this.mapController.handlePlayerEnter(map, player);
    }

    @EventHandler
    public void deletePlayerMapData(final PlayerQuitEvent event)
    {
        // nie zajmujemy pamieci i upewniamy sie ze po ponownym wejsciu wszystko bedzie ok
        this.mapController.deletePlayerMapData(event.getPlayer());
    }

    @EventHandler
    public void resetCanvasesWhenRespawn(final PlayerRespawnEvent event)
    {
        final PlayerMapData data = this.mapController.getPlayerMapData(event.getPlayer());

        // Respawn u klienta powoduje zresetowanie wszystkich zcachowanych kanw,
        // dlatego my robimy to samo na serwerze.
        data.resetAllClientSideCanvases();
    }

    @EventHandler
    public void resetCanvasesWhenWorldSwitch(final PlayerChangedWorldEvent event)
    {
        final PlayerMapData data = this.mapController.getPlayerMapData(event.getPlayer());

        // Zmiana swiata u klienta powoduje dziwne zachowanie i niewyswietlanie map.
        data.resetAllClientSideCanvases();
    }

    @EventHandler
    public void onInteractWithMap(final PlayerInteractAtEntityEvent event)
    {
        final Entity entity = event.getRightClicked();
        if (! (entity instanceof ItemFrame))
        {
            return;
        }

        final int entityId = entity.getEntityId();
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (board.isEntityBelongsToBoard(entityId))
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onMapMetadata(final AsyncPacketOutEvent event)
    {
        // system map wysyla entity metadata w ByteBufie wiec ten listener
        // tego nie zlapie.
        if (! event.isType(PacketPlayOutEntityMetadata.class))
        {
            return;
        }

        final PacketPlayOutEntityMetadata packet = (PacketPlayOutEntityMetadata) event.getPacket();
        final WrapperPlayOutEntityMetadata wrapper = new WrapperPlayOutEntityMetadata(packet);

        final int entityId = wrapper.getEntityId();
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (board.isEntityBelongsToBoard(entityId))
            {
                // blokujemy wszystkie Entity Metadata dotyczace naszej ramki
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

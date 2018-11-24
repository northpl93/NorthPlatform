package pl.north93.northplatform.api.bukkit.map.impl;

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northspigot.event.entity.EntityTrackedPlayerEvent;
import pl.north93.northplatform.api.bukkit.protocol.wrappers.WrapperPlayOutEntityMetadata;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.protocol.PacketEvent;
import pl.north93.northplatform.api.bukkit.protocol.PacketHandler;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

public class MapListener implements AutoListener
{
    @Inject
    private MapManagerImpl mapManager;
    @Inject
    private MapController  mapController;

    @Bean
    private MapListener()
    {
    }

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
    public void deleteBoardWhenWorldUnloads(final WorldUnloadEvent event)
    {
        final World world = event.getWorld();
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (world.equals(board.getWorld()))
            {
                // pozostawienie tablic w niezaladowanych swiatach powoduje wyciek pamieci
                this.mapManager.removeBoard(board);
            }
        }
    }

    @EventHandler
    public void onInteractWithMap(final PlayerInteractAtEntityEvent event)
    {
        final Entity entity = event.getRightClicked();
        if (! (entity instanceof ItemFrame))
        {
            return;
        }

        if (this.isEntityBelongsToAnyBoard(entity.getEntityId()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMapDestroy(final HangingBreakEvent event)
    {
        final Hanging entity = event.getEntity();
        if (! (entity instanceof ItemFrame))
        {
            return;
        }

        if (this.isEntityBelongsToAnyBoard(entity.getEntityId()))
        {
            event.setCancelled(true);
        }
    }

    @PacketHandler
    public void onAsyncMapMetadata(final PacketEvent<PacketPlayOutEntityMetadata> event)
    {
        // system map wysyla entity metadata w ByteBufie wiec ten listener
        // tego nie zlapie.
        final WrapperPlayOutEntityMetadata wrapper = new WrapperPlayOutEntityMetadata(event.getPacket());

        // blokujemy wszystkie Entity Metadata dotyczace naszej ramki
        if (this.isEntityBelongsToAnyBoard(wrapper.getEntityId()))
        {
            event.setCancelled(true);
        }
    }

    private boolean isEntityBelongsToAnyBoard(final int entityId)
    {
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (board.isEntityBelongsToBoard(entityId))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

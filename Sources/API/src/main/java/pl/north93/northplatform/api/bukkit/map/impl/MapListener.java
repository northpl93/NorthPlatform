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

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.player.event.PlayerPlatformLocaleChangedEvent;
import pl.north93.northplatform.api.bukkit.protocol.PacketEvent;
import pl.north93.northplatform.api.bukkit.protocol.PacketHandler;
import pl.north93.northplatform.api.bukkit.protocol.wrappers.WrapperPlayOutEntityMetadata;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northspigot.event.entity.EntityTrackedPlayerEvent;

@ToString(onlyExplicitlyIncluded = true)
public class MapListener implements AutoListener
{
    @Inject
    private MapManagerImpl mapManager;
    @Inject
    private MapController mapController;

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
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        this.mapController.deletePlayerMapData(player);
    }

    @EventHandler
    public void resetCanvasesWhenRespawn(final PlayerRespawnEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        final PlayerMapData data = this.mapController.getPlayerMapData(player);

        // Respawn u klienta powoduje zresetowanie wszystkich zcachowanych kanw,
        // dlatego my robimy to samo na serwerze.
        data.resetAllClientSideCanvases();
    }

    @EventHandler
    public void resetCanvasesWhenWorldSwitch(final PlayerChangedWorldEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        final PlayerMapData data = this.mapController.getPlayerMapData(player);

        // Zmiana swiata u klienta powoduje dziwne zachowanie i niewyswietlanie map.
        data.resetAllClientSideCanvases();
    }

    @EventHandler
    public void triggerRenderingOnLanguageChange(final PlayerPlatformLocaleChangedEvent event)
    {
        final INorthPlayer player = event.getPlayer();
        for (final BoardImpl board : this.mapManager.getBoards())
        {
            if (board.isVisibleBy(player))
            {
                board.renderFor(player);
            }
        }
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
}

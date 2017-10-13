package pl.arieals.lobby.chest.opening;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.chest.animation.ChestAnimationController;
import pl.arieals.lobby.chest.loot.LootResult;
import pl.arieals.lobby.chest.opening.event.BeginChestOpeningEvent;
import pl.arieals.lobby.chest.opening.event.CloseOpeningGuiEvent;
import pl.arieals.lobby.chest.opening.event.NextChestEvent;
import pl.arieals.lobby.chest.opening.event.OpenOpeningGuiEvent;
import pl.arieals.lobby.chest.opening.event.PresentOpeningResultsEvent;
import pl.north93.zgame.api.bukkit.entityhider.EntityVisibility;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.bukkit.hologui.IHoloContext;
import pl.north93.zgame.api.bukkit.hologui.IHoloGuiManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class HubOpeningListener implements AutoListener
{
    @Inject
    private ChestOpeningController   chestOpeningController;
    @Inject
    private IEntityHider             entityHider;
    @Inject
    private IHoloGuiManager          holoGuiManager;
    @Inject
    private ChestAnimationController chestAnimationController;

    @EventHandler
    public void onStartOpening(final OpenOpeningGuiEvent event)
    {
        final Player player = event.getPlayer();

        // ukrywamy gracza przed teleportacja zeby nie mignelo
        final Set<Entity> playerSet = Collections.singleton(player);
        this.entityHider.setVisibility(EntityVisibility.HIDDEN, playerSet);

        // teleportujemy gracza do lokacji docelowej
        final Location location = event.getSession().getPlayerLocation();
        player.teleport(location);

        // wywolujemy pierwsza skrzynke (jesli gracz ja ma)
        this.chestOpeningController.nextChest(player);

        // otwieramy graczu UI skrzynek
        this.holoGuiManager.openGui(player, location, new OpeningHoloGui());
    }

    @EventHandler
    public void onShiftPress(final PlayerToggleSneakEvent event)
    {
        if (! event.isSneaking())
        {
            return;
        }

        final Player player = event.getPlayer();
        if (this.chestOpeningController.isCurrentlyInOpening(player))
        {
            // konczymy otwieranie
            this.chestOpeningController.closeOpeningGui(player);
        }
    }

    @EventHandler
    public void onEndOpening(final CloseOpeningGuiEvent event)
    {
        final Player player = event.getSession().getPlayer();

        // niszczymy animacje
        this.chestAnimationController.destroyAnimation(player);

        // zamykamy interfejs holograficzny
        this.holoGuiManager.closeGui(player);

        // przywracamy widocznosc gracza
        this.entityHider.setVisibility(EntityVisibility.NEUTRAL, Collections.singleton(player));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void nextChestResetPreviousState(final NextChestEvent event)
    {
        final Player player = event.getPlayer();

        final IHoloContext playerContext = this.holoGuiManager.getPlayerContext(player);
        if (playerContext == null || ! (playerContext.getGui() instanceof OpeningHoloGui))
        {
            // jesli kontekst gracza to null lub jesli gracz nie ma otwartego dobrego
            // gui to je otwieramy
            final Location guiLocation = event.getOpeningSession().getPlayerLocation();
            this.holoGuiManager.openGui(player, guiLocation, new OpeningHoloGui());
        }

        // upewniamy sie ze animacja zostala zniszczona
        this.chestAnimationController.destroyAnimation(player);
    }

    @EventHandler
    public void onNextChestShown(final NextChestEvent event)
    {
        final Player player = event.getPlayer();
        final IOpeningSession session = event.getOpeningSession();

        // todo jesli event zostal anulowany wyswietlamy ze nie ma skrzynki.

        // tworzymy nowa animacje
        final Location location = session.getConfig().getChestLocation().toBukkit(player.getWorld());
        this.chestAnimationController.createAnimation(player, location);
    }

    @EventHandler
    public void onOpeningBegin(final BeginChestOpeningEvent event)
    {
        // gdy kliknelismy skrzynke i zaczelismy otwieranie
        // to zamykamy gui
        this.holoGuiManager.closeGui(event.getPlayer());
    }

    @EventHandler
    public void showOpeningResults(final PresentOpeningResultsEvent event)
    {
        final LootResult result = event.getResult();

        final OpeningResultHoloGui resultsGui = new OpeningResultHoloGui(result);
        final Location guiLocation = event.getOpeningSession().getPlayerLocation();

        this.holoGuiManager.openGui(event.getPlayer(), guiLocation, resultsGui);
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event)
    {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (from.getX() == to.getX() && from.getZ() == to.getZ())
        {
            // jesli gracz nie poruszyl sie na osiach x i z to nic nie robimy
            return;
        }

        if (this.chestOpeningController.isCurrentlyInOpening(event.getPlayer()))
        {
            // jesli gracz otwiera skrzynke to blokujemy ruch
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event)
    {
        // gdy gracz wychodzi z serwera to wymuszamy zakonczenie openingu
        this.chestOpeningController.closeOpeningGui(event.getPlayer());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

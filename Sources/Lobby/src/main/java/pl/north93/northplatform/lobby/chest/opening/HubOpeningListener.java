package pl.north93.northplatform.lobby.chest.opening;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.entityhider.EntityVisibility;
import pl.north93.northplatform.api.bukkit.entityhider.IEntityHider;
import pl.north93.northplatform.api.bukkit.gui.impl.GuiTracker;
import pl.north93.northplatform.api.bukkit.hologui.ActionBarKeeper;
import pl.north93.northplatform.api.bukkit.hologui.IHoloContext;
import pl.north93.northplatform.api.bukkit.hologui.IHoloGuiManager;
import pl.north93.northplatform.api.bukkit.player.IBukkitPlayers;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.PluralForm;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.lobby.chest.loot.LootResult;
import pl.north93.northplatform.lobby.chest.opening.event.BeginChestOpeningEvent;
import pl.north93.northplatform.lobby.chest.opening.event.CloseOpeningGuiEvent;
import pl.north93.northplatform.lobby.chest.opening.event.NextChestEvent;
import pl.north93.northplatform.lobby.chest.opening.event.OpenOpeningGuiEvent;
import pl.north93.northplatform.lobby.chest.opening.event.PresentOpeningResultsEvent;

public class HubOpeningListener implements AutoListener
{
    @Inject
    private IEntityHider entityHider;
    @Inject
    private IBukkitPlayers bukkitPlayers;
    @Inject
    private IHoloGuiManager holoGuiManager;
    @Inject
    private ActionBarKeeper actionBarKeeper;
    @Inject
    private ChestOpeningController chestOpeningController;
    @Inject
    private GuiTracker guiTracker;
    @Inject @Messages("ChestOpening")
    private MessagesBox messages;

    @EventHandler
    public void onStartOpening(final OpenOpeningGuiEvent event)
    {
        final INorthPlayer player = event.getPlayer();

        // zamykamy hotbara aby nic sie nie bugowalo
        if (this.guiTracker.hasHotbarMenu(player))
        {
            this.guiTracker.closeHotbarMenu(player);
        }

        // ukrywamy gracza przed teleportacja zeby nie mignelo
        final Set<Entity> playerSet = Collections.singleton(player);
        this.entityHider.setVisibility(EntityVisibility.HIDDEN, playerSet);

        // teleportujemy gracza do lokacji docelowej
        final Location location = event.getSession().getPlayerLocation();
        player.teleport(location);

        // wywolujemy pierwsza skrzynke (jesli gracz ja ma) i przy okazji GUI
        this.chestOpeningController.nextChest(player);
    }

    @EventHandler
    public void onEndOpening(final CloseOpeningGuiEvent event)
    {
        final INorthPlayer player = event.getSession().getPlayer();

        // usuwamy action bar z iloscia skrzynek jesli gracz go mial
        this.actionBarKeeper.reset(player);

        // zamykamy interfejs holograficzny
        this.holoGuiManager.closeGui(player);

        // przywracamy widocznosc gracza
        this.entityHider.setVisibility(EntityVisibility.NEUTRAL, Collections.singleton(player));

        // teleportujemy gracza na spawn huba
        final LocalHubServer hubServer = this.chestOpeningController.getThisHubServer();
        hubServer.movePlayerToHub(player, hubServer.getHubWorld(player).getHubId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void nextChestResetPreviousState(final NextChestEvent event)
    {
        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());

        final IHoloContext playerContext = this.holoGuiManager.getPlayerContext(player);
        if (playerContext == null || ! (playerContext.getGui() instanceof OpeningHoloGui))
        {
            // jesli kontekst gracza to null lub jesli gracz nie ma otwartego dobrego
            // gui to je otwieramy
            final Location guiLocation = event.getOpeningSession().getPlayerLocation();
            this.holoGuiManager.openGui(player, guiLocation, new OpeningHoloGui(event.getOpeningSession()));
        }
    }

    @EventHandler
    public void onNextChestShown(final NextChestEvent event)
    {
        final INorthPlayer player = event.getPlayer();

        final Integer chests = this.chestOpeningController.getChests(player);
        assert chests != null; // null moze byc tylko gdy gracz nie posiada sesji

        if (event.isCancelled())
        {
            // gracz nie ma skrzynek
            this.actionBarKeeper.setActionBar(player, TranslatableString.of(this.messages, "@actionbar.no_chests"));
            return;
        }

        // ustawiamy action bar z iloscia skrzynek
        final String chestsWord = PluralForm.transformKey("@chests", chests);
        final Vars<Object> vars = Vars.<Object>of("num", chests).and("word", TranslatableString.of(this.messages, chestsWord));
        final TranslatableString translatableString = TranslatableString.of(this.messages, "@actionbar.you_have_chests$num,word").withVars(vars);

        this.actionBarKeeper.setActionBar(player, translatableString);
    }

    @EventHandler
    public void onChestOpeningStarted(final BeginChestOpeningEvent event)
    {
        // gdy gracz zacznie otwierac skrzynke to usuwamy action bar
        this.actionBarKeeper.reset(event.getPlayer());
    }

    @EventHandler
    public void showOpeningResults(final PresentOpeningResultsEvent event)
    {
        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());
        final LootResult result = event.getResult();

        final OpeningResultHoloGui resultsGui = new OpeningResultHoloGui(result);
        final Location guiLocation = event.getOpeningSession().getPlayerLocation();

        this.holoGuiManager.openGui(player, guiLocation, resultsGui);
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

        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());
        if (this.chestOpeningController.isCurrentlyInOpening(player))
        {
            // jesli gracz otwiera skrzynke to blokujemy ruch
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event)
    {
        final INorthPlayer player = this.bukkitPlayers.getPlayer(event.getPlayer());

        // gdy gracz wychodzi z serwera to wymuszamy zakonczenie openingu
        this.chestOpeningController.closeOpeningGui(player);
    }

    /*@EventHandler
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
    }*/

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

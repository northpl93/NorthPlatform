package pl.arieals.lobby.chest.opening;

import javax.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.api.minigame.server.lobby.hub.LocalHubServer;
import pl.arieals.lobby.chest.ChestService;
import pl.arieals.lobby.chest.ChestType;
import pl.arieals.lobby.chest.loot.ChestLootService;
import pl.arieals.lobby.chest.loot.LootResult;
import pl.arieals.lobby.chest.opening.event.BeginChestOpeningEvent;
import pl.arieals.lobby.chest.opening.event.CloseOpeningGuiEvent;
import pl.arieals.lobby.chest.opening.event.NextChestEvent;
import pl.arieals.lobby.chest.opening.event.OpenOpeningGuiEvent;
import pl.arieals.lobby.chest.opening.event.PresentOpeningResultsEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.Main;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class ChestOpeningController
{
    @Inject
    private BukkitApiCore    apiCore;
    @Inject
    private MiniGameServer   miniGameServer;
    @Inject
    private ChestService     chestService;
    @Inject
    private ChestLootService lootService;

    @Bean
    private ChestOpeningController()
    {
    }

    public boolean isCurrentlyInOpening(final Player player)
    {
        return this.getSession(player) != null;
    }

    public void openOpeningGui(final Player player)
    {
        final HubWorld hubWorld = this.getThisHubServer().getHubWorld(player);

        final HubOpeningConfig config = HubOpeningConfigCache.INSTANCE.getConfig(hubWorld); // pobieramy zachowany config
        final OpeningSessionImpl openingSession = new OpeningSessionImpl(player, hubWorld, config); // tworzymy sesje

        this.updateSession(player, openingSession);
        this.apiCore.callEvent(new OpenOpeningGuiEvent(openingSession));

        this.apiCore.getLogger().log(Level.INFO, "[Lobby] Player {0} is now in opening gui on hub {1}", new Object[]{player.getName(), hubWorld.getHubId()});
    }

    public void closeOpeningGui(final Player player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // gracz nie ma aktywnej sesji otwierania
            return;
        }
        this.apiCore.callEvent(new CloseOpeningGuiEvent(session));

        // usuwamy sesje gracza
        this.updateSession(player, null);

        this.apiCore.getLogger().log(Level.INFO, "[Lobby] Player {0} exited chest opening", player.getName());
    }

    /**
     * Zwraca ilosc posiadanych przez gracza skrzynek aktualnego typu
     * pobranego z sesji otwierania.
     * Jesli gracz aktualnie nie otwiera skrzynek to zostanie zwrocony null.
     *
     * @param player Gracz ktoremu sprawdzamy ilosc skrzynek.
     * @return Ilosc skrzynek lub null.
     */
    public @Nullable Integer getChests(final Player player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // wywolano metode bez aktywnej sesji, nic nie robimy dalej
            return null;
        }

        final ChestType chestType = this.chestService.getType(session.getConfig().getChestType());
        return this.chestService.getChests(player, chestType);
    }

    /**
     * Usuwa aktualna animacje skrzynki, i jesli gracz posiada skrzynke
     * danego typu (pobrany z sesji) ueuchamia animacje.
     *
     * @param player Gracz ktoremu chcemy podac kolejna skrzynke.
     */
    public void nextChest(final Player player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // wywolano metode bez aktywnej sesji, nic nie robimy dalej
            return;
        }

        final ChestType chestType = this.chestService.getType(session.getConfig().getChestType());
        final int chests = this.chestService.getChests(player, chestType);
        session.setChests(chests); // aktualizujemy ilosc posiadanych skrzynek w sesji

        final NextChestEvent event = new NextChestEvent(player, session);
        event.setCancelled(chests <= 0); // jak nie ma skrzynek to anulujemy event

        this.apiCore.callEvent(event);
        if (event.isCancelled())
        {
            return;
        }

        this.apiCore.getLogger().log(Level.INFO, "[Lobby] Giving next chest to {0}", player.getName());
    }

    /**
     * Podczas gdy jest aktywna sesja, probuje rozpoczac otwieranie skrzynki.
     *
     * @param player Gracz ktoremu chcemy otworzyc skrzynke.
     * @return Wyniki otwierania jako CompletableFuture.
     */
    public boolean beginChestOpening(final Player player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // wywolano metode bez aktywnej sesji, nic nie robimy dalej
            return false;
        }

        this.apiCore.getLogger().log(Level.INFO, "[Lobby] Player {0} requested chest open", player.getName());

        final ChestType type = this.chestService.getType(session.getConfig().getChestType());
        session.setLastResults(this.lootService.openChest(player, type));

        final BeginChestOpeningEvent event = this.apiCore.callEvent(new BeginChestOpeningEvent(player, session, type));
        return ! event.isCancelled(); // jak nie anulowana to znaczy, ze sie udalo
    }

    /**
     * Wyswietla rezultat otwierania skrzynki.
     *
     * @param player Gracz ktoremy prezentujemy rezultaty.
     */
    public void showOpeningResults(final Player player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // wywolano metode bez aktywnej sesji, nic nie robimy dalej
            return;
        }

        final CompletableFuture<LootResult> lastResults = session.getLastResults();
        if (lastResults == null)
        {
            // wystapil blad podczas otwierania skrzynki (task sie nie zakobczyl?)
            this.apiCore.getLogger().log(Level.WARNING, "lastResults is null in showOpeningResults. Player: {0}", player.getName());

            // restart jest dobry na wszystko, wywalamy gracza z otwierania skrzynek
            this.closeOpeningGui(player);
            return;
        }

        try
        {
            this.apiCore.callEvent(new PresentOpeningResultsEvent(player, session, lastResults.get()));
        }
        catch (final InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            this.closeOpeningGui(player); // jak cos sie zepsulo to wywalamy gracza z openingu
        }
    }

    private void updateSession(final Player player, final OpeningSessionImpl session)
    {
        Preconditions.checkNotNull(player);
        final Main pluginMain = this.apiCore.getPluginMain();
        if (session == null)
        {
            player.removeMetadata("lobby/chestOpeningSession", pluginMain);
        }
        else
        {
            player.setMetadata("lobby/chestOpeningSession", new FixedMetadataValue(pluginMain, session));
        }
    }

    public @Nullable
    OpeningSessionImpl getSession(final Player player)
    {
        final List<MetadataValue> metadata = player.getMetadata("lobby/chestOpeningSession");
        if (metadata.isEmpty())
        {
            return null;
        }
        return (OpeningSessionImpl) metadata.get(0).value();
    }

    // zwraca obiekt LocalHub reprezentujacy ten serwer hostujacy huby.
    private LocalHubServer getThisHubServer()
    {
        final LobbyManager serverManager = this.miniGameServer.getServerManager();
        return serverManager.getLocalHub();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

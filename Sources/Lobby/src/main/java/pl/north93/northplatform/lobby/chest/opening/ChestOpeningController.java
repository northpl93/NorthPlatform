package pl.north93.northplatform.lobby.chest.opening;

import javax.annotation.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.lobby.chest.ChestService;
import pl.north93.northplatform.lobby.chest.ChestType;
import pl.north93.northplatform.lobby.chest.loot.ChestLootService;
import pl.north93.northplatform.lobby.chest.loot.LootResult;
import pl.north93.northplatform.lobby.chest.opening.event.BeginChestOpeningEvent;
import pl.north93.northplatform.lobby.chest.opening.event.CloseOpeningGuiEvent;
import pl.north93.northplatform.lobby.chest.opening.event.NextChestEvent;
import pl.north93.northplatform.lobby.chest.opening.event.OpenOpeningGuiEvent;
import pl.north93.northplatform.lobby.chest.opening.event.PresentOpeningResultsEvent;

@Slf4j
public class ChestOpeningController
{
    @Inject
    private LobbyManager lobbyManager;
    @Inject
    private ChestService chestService;
    @Inject
    private ChestLootService lootService;
    @Inject
    private IBukkitServerManager serverManager;

    @Bean
    private ChestOpeningController()
    {
    }

    public boolean isCurrentlyInOpening(final INorthPlayer player)
    {
        return this.getSession(player) != null;
    }

    public void openOpeningGui(final INorthPlayer player)
    {
        final HubWorld hubWorld = this.getThisHubServer().getHubWorld(player);

        final HubOpeningConfig config = HubOpeningConfigCache.INSTANCE.getConfig(hubWorld); // pobieramy zachowany config
        final OpeningSessionImpl openingSession = new OpeningSessionImpl(player, hubWorld, config); // tworzymy sesje

        player.setPlayerData(OpeningSessionImpl.class, openingSession);
        this.serverManager.callEvent(new OpenOpeningGuiEvent(openingSession));

        log.info("[Lobby] Player {} is now in opening gui on hub {}", player.getName(), hubWorld.getHubId());
    }

    public void closeOpeningGui(final INorthPlayer player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // gracz nie ma aktywnej sesji otwierania
            return;
        }
        this.serverManager.callEvent(new CloseOpeningGuiEvent(session));

        // usuwamy sesje gracza
        player.removePlayerData(OpeningSessionImpl.class);

        log.info("[Lobby] Player {} exited chest opening", player.getName());
    }

    /**
     * Zwraca ilosc posiadanych przez gracza skrzynek aktualnego typu
     * pobranego z sesji otwierania.
     * Jesli gracz aktualnie nie otwiera skrzynek to zostanie zwrocony null.
     *
     * @param player Gracz ktoremu sprawdzamy ilosc skrzynek.
     * @return Ilosc skrzynek lub null.
     */
    public @Nullable Integer getChests(final INorthPlayer player)
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
    public void nextChest(final INorthPlayer player)
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

        final NextChestEvent event = new NextChestEvent(session);
        event.setCancelled(chests <= 0); // jak nie ma skrzynek to anulujemy event

        this.serverManager.callEvent(event);
        if (event.isCancelled())
        {
            return;
        }

        log.info("[Lobby] Giving next chest to {}", player.getName());
    }

    /**
     * Podczas gdy jest aktywna sesja, probuje rozpoczac otwieranie skrzynki.
     *
     * @param player Gracz ktoremu chcemy otworzyc skrzynke.
     * @return Wyniki otwierania jako CompletableFuture.
     */
    public boolean beginChestOpening(final INorthPlayer player)
    {
        final OpeningSessionImpl session = this.getSession(player);
        if (session == null)
        {
            // wywolano metode bez aktywnej sesji, nic nie robimy dalej
            return false;
        }

        log.info("[Lobby] Player {} requested chest open", player.getName());

        final ChestType type = this.chestService.getType(session.getConfig().getChestType());
        session.setLastResults(this.lootService.openChest(player, type));

        final BeginChestOpeningEvent event = this.serverManager.callEvent(new BeginChestOpeningEvent(player, session, type));
        return ! event.isCancelled(); // jak nie anulowana to znaczy, ze sie udalo
    }

    /**
     * Wyswietla rezultat otwierania skrzynki.
     *
     * @param player Gracz ktoremy prezentujemy rezultaty.
     */
    public void showOpeningResults(final INorthPlayer player)
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
            // wystapil blad podczas otwierania skrzynki (task sie nie zakonczyl?)
            log.warn("lastResults is null in showOpeningResults. Player: {}", player.getName());

            // restart jest dobry na wszystko, wywalamy gracza z otwierania skrzynek
            this.closeOpeningGui(player);
            return;
        }

        try
        {
            this.serverManager.callEvent(new PresentOpeningResultsEvent(player, session, lastResults.get()));
        }
        catch (final InterruptedException | ExecutionException e)
        {
            log.error("Exception while calling PresentOpeningResultsEvent");
            this.closeOpeningGui(player); // jak cos sie zepsulo to wywalamy gracza z openingu
        }
    }

    public @Nullable OpeningSessionImpl getSession(final INorthPlayer player)
    {
        return player.getPlayerData(OpeningSessionImpl.class);
    }

    // zwraca obiekt LocalHub reprezentujacy ten serwer hostujacy huby.
    public LocalHubServer getThisHubServer()
    {
        return this.lobbyManager.getLocalHub();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

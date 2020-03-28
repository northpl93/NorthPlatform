package pl.arieals.minigame.bedwars.listener;

import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.BedWarsPlayer;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.scoreboard.GameScoreboard;
import pl.arieals.minigame.bedwars.scoreboard.LobbyScoreboard;
import pl.arieals.minigame.bedwars.shop.EliminationEffectManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.player.PlayersManager;
import pl.north93.northplatform.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerJoinArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.PlayerQuitArenaEvent;
import pl.north93.northplatform.api.minigame.server.gamehost.event.player.SpectatorJoinEvent;
import pl.north93.northplatform.api.minigame.shared.api.GamePhase;

@Slf4j
public class PlayerTeamListener implements Listener
{
    @Inject
    private BwConfig                 config;
    @Inject
    private IScoreboardManager       scoreboardManager;
    @Inject
    private EliminationEffectManager eliminationEffect;
    @Inject @Messages("BedWars")
    private MessagesBox              messages;

    @EventHandler
    public void playerJoin(final PlayerJoinArenaEvent event)
    {
        final INorthPlayer player = event.getPlayer();

        final BedWarsArena arenaData = event.getArena().getArenaData();
        final BedWarsPlayer bedWarsPlayer = new BedWarsPlayer(player, this.eliminationEffect.getEffectOf(player));
        player.setPlayerData(bedWarsPlayer);

        final Location lobbyLocation = arenaData.getConfig().getLobby().toBukkit(event.getArena().getWorld().getCurrentWorld());
        player.teleport(lobbyLocation);

        this.scoreboardManager.setLayout(player, new LobbyScoreboard());
    }

    @EventHandler
    public void gameStart(final GameStartEvent event)
    {
        final LocalArena arena = event.getArena();
        final BedWarsArena arenaData = arena.getArenaData();

        final PlayersManager playersManager = arena.getPlayersManager();
        for (final INorthPlayer player : playersManager.getPlayers())
        {
            final Team smallestTeam = arenaData.getTeams()
                                               .stream()
                                               .filter(team -> team.getPlayers().size() < this.config.getTeamSize())
                                               .min(Comparator.comparing(team -> team.getPlayers().size()))
                                               .orElse(null);
            final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);

            if (smallestTeam == null)
            {
                log.error("smallestTeam is null in gameStart on arena {} player {}", arena.getId(), player.getName());
                player.sendMessage(ChatColor.RED + "Blad krytyczny; brak wolnego teamu (niepoprawna konfiguracja areny?)");
                return;
            }

            if (playerData == null)
            {
                // todo log it
                continue;
            }

            playerData.switchTeam(smallestTeam);
            arenaData.getPlayers().add(playerData);

            final String teamNameDative = this.messages.getString(player.getLocale(), "team.dative." + smallestTeam.getName());
            player.sendMessage(this.messages, "separator");
            player.sendMessage(this.messages, "welcome", MessageLayout.CENTER, smallestTeam.getColor(), teamNameDative);
            player.sendMessage(this.messages, "separator");
        }

        for (final Player player : playersManager.getAllPlayers())
        {
            this.scoreboardManager.setLayout(player, new GameScoreboard());
        }
    }

    @EventHandler
    public void showScoreboardToSpectators(final SpectatorJoinEvent event)
    {
        final LocalArena arena = event.getArena();
        if (arena.getGamePhase() != GamePhase.STARTED)
        {
            return;
        }

        this.scoreboardManager.setLayout(event.getPlayer(), new GameScoreboard());
    }

    @EventHandler
    public void playerLeave(final PlayerQuitArenaEvent event)
    {
        final BedWarsPlayer playerData = event.getPlayer().getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        if (playerData.isEliminated())
        {
            // jesli gracz jest wyeliminowany to nie wysylamy komunikatu wyjscia
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void chestOpen(final PlayerInteractEvent event)
    {
        final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
        final Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.CHEST)
        {
            return;
        }

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null || playerData.getTeam() == null)
        {
            event.setCancelled(true);
            return;
        }

        final BedWarsArena arenaData = playerData.getTeam().getArena().getArenaData();
        final Team teamAt = arenaData.getTeamAt(block);
        if (teamAt == playerData.getTeam() || teamAt.isEliminated())
        {
            // gracz moze otwierac skrzynki na terenie swojej druzyny
            // jak team jest wyeliminowany to zawsze mozna otwierac skrzynki
            return;
        }

        // jesli team nie jest wyeliminowany to blokujemy otwarcie skrzynki
        final String teamName = this.messages.getString(player.getMyLocale(), "team.nominative." + teamAt.getName());
        player.sendMessage(this.messages, "chest_blocked", teamAt.getColor(), teamName);

        event.setCancelled(true);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

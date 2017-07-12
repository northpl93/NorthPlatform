package pl.arieals.minigame.goldhunter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.goldhunter.scoreboard.ArenaScoreboardManager;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;

public class GoldHunterArena implements IArenaData, ITickable
{
    private final Logger logger;
    private final LocalArena localArena;
    
    private final Set<GoldHunterPlayer> players = new HashSet<>();
    private final Multimap<GameTeam, GoldHunterPlayer> signedPlayers = ArrayListMultimap.create();
    
    private final ArenaScoreboardManager scoreboardManager = new ArenaScoreboardManager(this);
    
    public GoldHunterArena(LocalArena localArena)
    {
        this.logger = LogManager.getLogger("Arena " + localArena.getId());
        this.localArena = localArena;
        
        
    }
    
    private void setDefaultScoreboardProperties()
    {
        scoreboardManager.setProperties(
                "signedCount", "0",
                "requiredPlayers", localArena.getPlayersManager().getMinPlayers()
        );
    }
    
    public Logger getLogger()
    {
        return logger;
    }
    
    public LocalArena getLocalArena()
    {
        return localArena;
    }
    
    public void forEachPlayer(Consumer<GoldHunterPlayer> action)
    {
        getPlayers().forEach(action);
    }
    
    public Collection<GoldHunterPlayer> getPlayers()
    {
        return players;
    }
    
    public Collection<GoldHunterPlayer> getPlayersInTeam(GameTeam team)
    {
        return signedPlayers.get(team);
    }
    
    public int getSignedPlayersCount()
    {
        return signedPlayers.size();
    }
    
    public void playerJoin(GoldHunterPlayer player)
    {
        Preconditions.checkState(players.add(player));
        logger.debug("Player {} joined to the arena", player);
        
        player.teleportToLobby();
        
        updatePlayersCount();
        scoreboardManager.setLobbyScoreboardLayout(player);
    }
    
    public void playerLeft(GoldHunterPlayer player)
    {
        Preconditions.checkState(players.remove(player));
        logger.debug("Player {} left the arena", player);
        
        unsignFromTeam(player);
        updatePlayersCount();
    }

    public void gameStart()
    {
        logger.debug("Arena gameStart()");
        // TODO:
    }

    public void gameEnd()
    {
        logger.debug("Arena gameEnd()");
        // TODO Auto-generated method stub
        
    }

    public void gameInit()
    {
        logger.debug("Arena gameInit()");
        // TODO Auto-generated method stub
    }
    
    public void scheduleStart()
    {
        localArena.getStartScheduler().scheduleStart();
        forEachPlayer(p -> scoreboardManager.setLobbyScoreboardLayout(p));
    }
    
    public void cancelStart()
    {
        localArena.getStartScheduler().cancelStarting();
        forEachPlayer(p -> scoreboardManager.setLobbyScoreboardLayout(p));
    }
    
    public void signToTeam(GoldHunterPlayer player, GameTeam team)
    {
        Preconditions.checkArgument(player != null);
        
        if ( team == null )
        {
            team = signedPlayers.get(GameTeam.TEAM1).size() > signedPlayers.get(GameTeam.TEAM2).size() ? GameTeam.TEAM2 : GameTeam.TEAM1;
        }
        
        signedPlayers.put(team, player);
        updateSignedPlayersCount();
        logger.debug("Signed player {} to team {}", player, team);
    }
    
    public void unsignFromTeam(GoldHunterPlayer player)
    {
        // TODO: check
        signedPlayers.values().removeIf(p -> p.equals(player));
        updateSignedPlayersCount();
        logger.debug("unsigned player {} from team", player);
    }
    
    private void updatePlayersCount()
    {
        logger.debug("Current players count is {}", players.size());
        
        scoreboardManager.setProperty("playersCount", players.size());
    }
    
    private void updateSignedPlayersCount()
    {
        logger.debug("Current signed players count is {}", signedPlayers.size());
        
        scoreboardManager.setProperties("signedCount", signedPlayers.size(),
                "team1Count", signedPlayers.get(GameTeam.TEAM1).size(),
                "team2Count", signedPlayers.get(GameTeam.TEAM2).size());
        
        if ( isEnoughSignedPlayersToStart() && !localArena.getStartScheduler().isStartScheduled() )
        {
            scheduleStart();
        }
        if ( !isEnoughSignedPlayersToStart() && localArena.getStartScheduler().isStartScheduled() )
        {
            cancelStart();
        }
    }
    
    @Tick
    public void updateStartGameInfo()
    {
        // TODO: bossbar
        
        if ( localArena.getStartScheduler().isStartScheduled() )
        {
            scoreboardManager.setProperty("startCounter", localArena.getStartScheduler().getStartCountdown().getSecondsLeft());
        }
    }
    
    public boolean isEnoughSignedPlayersToStart()
    {
        return getSignedPlayersCount() >= localArena.getPlayersManager().getMinPlayers();
    }
}

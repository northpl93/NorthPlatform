package pl.arieals.minigame.goldhunter;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.bind.JAXB;

import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.minigame.goldhunter.scoreboard.ArenaScoreboardManager;
import pl.arieals.minigame.goldhunter.structure.GoldChestStructure;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class GoldHunterArena implements IArenaData, ITickable
{
    @Inject
    @GoldHunterLogger
    private Logger logger;
    
    @Inject
    private IGuiManager guiManager;
    
    private final LocalArena localArena;
    
    private final Set<GoldHunterPlayer> players = new HashSet<>();
    private final Multimap<GameTeam, GoldHunterPlayer> signedPlayers = ArrayListMultimap.create();
    
    private final ArenaScoreboardManager scoreboardManager = new ArenaScoreboardManager(this);
    private final StructureManager structureManager = new StructureManager(this);
    
    private GoldHunterMapConfig mapConfig;
    
    private final Map<GameTeam, Location> spawns = new EnumMap<>(GameTeam.class);
    
    public GoldHunterArena(LocalArena localArena)
    {
        this.localArena = localArena;
        
        setDefaultScoreboardProperties();
    }
    
    private void setDefaultScoreboardProperties()
    {
        scoreboardManager.setProperties(
                "signedCount", "0",
                "maxPlayers", localArena.getPlayersManager().getMaxPlayers()
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
    
    public World getCurrentWorld()
    {
        return localArena.getWorld().getCurrentWorld();
    }
    
    public ArenaScoreboardManager getScoreboardManager()
    {
        return scoreboardManager;
    }
    
    public StructureManager getStructureManager()
    {
        return structureManager;
    }
    
    public boolean hasGame()
    {
        return localArena.getGamePhase() != GamePhase.LOBBY && localArena.getGamePhase() != GamePhase.INITIALISING;
    }
    
    public void forEachPlayer(Consumer<GoldHunterPlayer> action)
    {
        getPlayers().forEach(action);
    }
    
    public Collection<GoldHunterPlayer> getPlayers()
    {
        return players;
    }
    
    public Collection<GoldHunterPlayer> getSignedPlayers()
    {
        return signedPlayers.values();
    }
    
    public Collection<GoldHunterPlayer> getPlayersInTeam(GameTeam team)
    {
        return signedPlayers.get(team);
    }
    
    public int getSignedPlayersCount()
    {
        return signedPlayers.size();
    }
    
    public Location getTeamSpawn(GameTeam team)
    {
        Preconditions.checkState(hasGame());
        return spawns.get(team);
    }
    
    public void playerJoin(GoldHunterPlayer player)
    {
        Preconditions.checkState(players.add(player));
        logger.debug("Player {} joined to the arena", player);
        
        player.spawnInLobby();
        
        updatePlayersCount();
        
        scoreboardManager.updateTeamColors();
    }
    
    public void playerLeft(GoldHunterPlayer player)
    {
        Preconditions.checkState(players.remove(player));
        logger.debug("Player {} left the arena", player);
        
        unsignFromTeam(player);
        updatePlayersCount();
        
        scoreboardManager.removeEntryFromTeams(player.getPlayer().getName());
    }

    public void gameStart()
    {
        logger.debug("Arena gameStart()");
        
        mapConfig = JAXB.unmarshal(localArena.getWorld().getResource("ghmap.xml"), GoldHunterMapConfig.class);
        mapConfig.validateConfig();
        
        setupSpawns();
        setupChests();
        
        signedPlayers.entries().forEach(e -> e.getValue().joinGame(e.getKey()));
        
        updateLobbyScoreboardLayout();
    }

    private void setupChests()
    {
        World world = localArena.getWorld().getCurrentWorld();
        
        mapConfig.getChestsRed().forEach(l -> addChest(l.toBukkit(world).toVector().toBlockVector(), GameTeam.RED));
        mapConfig.getChestsBlue().forEach(l -> addChest(l.toBukkit(world).toVector().toBlockVector(), GameTeam.BLUE));
        
        updateChestsCount();
    }

    private void addChest(BlockVector location, GameTeam team)
    {
        GoldChestStructure chest = new GoldChestStructure(location, team);
        Preconditions.checkState(structureManager.spawn(chest));
    }
    
    private void setupSpawns()
    {
        World world = localArena.getWorld().getCurrentWorld();
        
        spawns.put(GameTeam.RED, mapConfig.getSpawn1().toBukkit(world).add(0.5, 0.5, 0.5));
        spawns.put(GameTeam.BLUE, mapConfig.getSpawn2().toBukkit(world).add(0.5, 0.5, 0.5));
    }

    public void gameEnd()
    {
        logger.debug("Arena gameEnd()");
        
        localArena.getScheduler().runTaskLater(localArena::prepareNewCycle, 120);
    }

    public void gameInit()
    {
        for ( GoldHunterPlayer player : signedPlayers.values() )
        {
            player.exitGame();
        }
        
        Set<GoldHunterPlayer> previousSignedPlayers = new HashSet<>(signedPlayers.values());
        signedPlayers.clear();
        updateSignedPlayersCount();
        
        // resign and shuffle players in teams for next game 
        for ( GoldHunterPlayer player : previousSignedPlayers )
        {
            signToTeam(player, null);
        }
        
        mapConfig = null;
        structureManager.clearStructures();
        spawns.clear();
        
        updateLobbyScoreboardLayout();
    }
    
    public boolean canBuild(Location location)
    {
        return canBuild(location.toVector().toBlockVector());
    }
    
    public boolean canBuild(BlockVector location)
    {
        for ( Location spawnLocation : spawns.values() )
        {
            if ( location.distanceSquared(spawnLocation.clone().add(0.5, 0.5, 0.5).toVector()) <= 9 )
            {
                return false;
            }
        }
        
        return !structureManager.isStructure(location);
    }
    
    public void scheduleStart()
    {
        localArena.getStartScheduler().scheduleStart();
        updateLobbyScoreboardLayout();
    }
    
    public void cancelStart()
    {
        localArena.getStartScheduler().cancelStarting();
        updateLobbyScoreboardLayout();
    }
    
    private void updateLobbyScoreboardLayout()
    {
        if ( !hasGame() )
        {
            forEachPlayer(p -> scoreboardManager.setLobbyScoreboardLayout(p));
        }
        else
        {
            getPlayers().stream().filter(p -> !p.isIngame()).forEach(p -> scoreboardManager.setLobbyScoreboardLayout(p));
        }
    }
    
    public void signToTeam(GoldHunterPlayer player, GameTeam team)
    {
        Preconditions.checkArgument(player != null);
        
        if ( signedPlayers.values().contains(player) )
        {
            return;
        }
        
        if ( team == null )
        {
            team = signedPlayers.get(GameTeam.RED).size() > signedPlayers.get(GameTeam.BLUE).size() ? GameTeam.BLUE : GameTeam.RED;
        }
        
        signedPlayers.put(team, player);
        updateSignedPlayersCount();
        
        if ( hasGame() )
        {
            player.joinGame(team);
        }
        
        logger.debug("Signed player {} to team {}", player, team);
    }
    
    public void unsignFromTeam(GoldHunterPlayer player)
    {
        signedPlayers.values().removeIf(p -> p.equals(player));
        updateSignedPlayersCount();
        
        if ( player.isIngame() )
        {
            player.exitGame();
        }
        
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
                "team1Count", signedPlayers.get(GameTeam.RED).size(),
                "team2Count", signedPlayers.get(GameTeam.BLUE).size());
        
        if ( hasGame() && ( signedPlayers.get(GameTeam.RED).size() == 0 || signedPlayers.get(GameTeam.BLUE).size() == 0 ) )
        {
            // TODO: walkower info
            localArena.setGamePhase(GamePhase.POST_GAME);
            
            if ( signedPlayers.size() == 0 )
            {
                localArena.prepareNewCycle();
            }
        }
        
        if ( !hasGame() && isEnoughSignedPlayersToStart() && !localArena.getStartScheduler().isStartScheduled() )
        {
            scheduleStart();
        }
        if ( !hasGame() && !isEnoughSignedPlayersToStart() && localArena.getStartScheduler().isStartScheduled() )
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
    
    public void broadcastMessageIngame(String key, Object... args)
    {
        if ( hasGame() )
        {
            signedPlayers.values().forEach(p -> p.sendMessage(key, args));
        }
    }
    
    public void broadcastSeparatedMessageIngame(String key, Object... args)
    {
        if ( hasGame() )
        {
            signedPlayers.values().forEach(p -> p.sendSeparatedMessage(key, args));;
        }
    }
    
    public boolean isEnoughSignedPlayersToStart()
    {
        return getSignedPlayersCount() >= localArena.getPlayersManager().getMinPlayers();
    }
    
    public void updateChestsCount()
    {
        Collection<GoldChestStructure> chests = structureManager.getStructuresOfType(GoldChestStructure.class);
        int red = chests.stream().filter(chest -> chest.getTeam() == GameTeam.RED).mapToInt(chest -> 1).sum();
        int blue = chests.size() - red;
       
        logger.debug("Current chests: RED={} BLUE={}", red, blue);
        
        scoreboardManager.setProperties("team1Chests", red, "team2Chests", blue);
        
        if ( red == 0 )
        {
            winGame(GameTeam.BLUE);
        }
        if ( blue == 0 )
        {
            winGame(GameTeam.RED);
        }
    }
    
    private void winGame(GameTeam winnerTeam)
    {
        logger.debug("Team {} win game...", winnerTeam);
        
        // TODO: special effects, fly etc.
        
        players.forEach(p -> p.sendSeparatedMessage("win_game", winnerTeam.getColoredBoldGenitive().getValue(p.getPlayer().getLocale()).toUpperCase()));
        localArena.setGamePhase(GamePhase.POST_GAME);
    }

    public void broadcastDeath(GoldHunterPlayer goldHunterPlayer, GoldHunterPlayer lastDamager)
    {
        if ( lastDamager != null )
        {
            broadcastMessageIngame("kill_message", goldHunterPlayer.getDisplayNameBold(), lastDamager.getDisplayNameBold());
        }
        else
        {
            broadcastMessageIngame("death_message", goldHunterPlayer.getDisplayNameBold());
        }
    }
    
    @Override
    public String toString()
    {
        return "Arena[" + localArena.getId() + "]";
    }
}

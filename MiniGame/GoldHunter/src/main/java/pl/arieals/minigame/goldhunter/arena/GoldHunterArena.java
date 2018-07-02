package pl.arieals.minigame.goldhunter.arena;

import javax.xml.bind.JAXB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import org.apache.logging.log4j.Logger;

import pl.arieals.api.minigame.server.gamehost.arena.IArenaData;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.shared.api.GamePhase;
import pl.arieals.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.arena.structure.GoldChestStructure;
import pl.arieals.minigame.goldhunter.player.GameTeam;
import pl.arieals.minigame.goldhunter.player.GoldHunterPlayer;
import pl.arieals.minigame.goldhunter.player.PlayerRank;
import pl.arieals.minigame.goldhunter.scoreboard.ArenaScoreboardManager;
import pl.arieals.minigame.goldhunter.utils.TimeStringUtils;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.lang.ListUtils;

public class GoldHunterArena implements IArenaData, ITickable
{
    @Inject
    @GoldHunterLogger
    private Logger logger;
    
    @Inject
    private IGuiManager guiManager;
    
    @Inject
    @Messages("GoldHunter")
    private MessagesBox messages;
    
    private final LocalArena localArena;
    
    private final Set<GoldHunterPlayer> players = new HashSet<>();
    private final Multimap<GameTeam, GoldHunterPlayer> signedPlayers = ArrayListMultimap.create();
    
    private final ArenaScoreboardManager scoreboardManager = new ArenaScoreboardManager(this);
    private final StructureManager structureManager = new StructureManager(this);
    
    private GoldHunterMapConfig mapConfig;
    
    private final Map<GameTeam, Location> spawns = new EnumMap<>(GameTeam.class);
    
    private int gameTime;
    
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
         
        scoreboardManager.initializeScoreboardTeams(player);
        player.spawnInLobby();
        
        updatePlayersCount();
        
        scoreboardManager.updateTeamColors();
        player.updateDisplayName();
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
        
        gameTime = 0;
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
    
    private Location getSpawn(GameTeam team)
    {
        return spawns.get(team).clone();
    }
    
    private void setupSpawns()
    {
        World world = localArena.getWorld().getCurrentWorld();
        
        spawns.put(GameTeam.RED, mapConfig.getSpawn1().toBukkit(world).add(0.5, 1, 0.5));
        spawns.put(GameTeam.BLUE, mapConfig.getSpawn2().toBukkit(world).add(0.5, 1, 0.5));
    }

    public void gameEnd()
    {
        logger.debug("Arena gameEnd()");
        
        localArena.getScheduler().runTaskLater(localArena::prepareNewCycle, 170);
    }

    public void gameInit()
    {
        for ( GoldHunterPlayer player : signedPlayers.values() )
        {
            player.exitGame();
        }

        Location lobbyLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        for ( Player spectator : localArena.getPlayersManager().getSpectators() )
        {
            spectator.teleport(lobbyLocation);
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
    
    public boolean canBuild(Block block)
    {
        return canBuild(block.getLocation().toVector().toBlockVector());
    }
    
    public boolean canBuild(Location location)
    {
        return canBuild(location.toVector().toBlockVector());
    }
    
    public boolean canBuild(BlockVector location)
    {
        Vector loc = new Vector(location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);

        for ( GameTeam team : GameTeam.values() )
        {
            if ( isNearSpawn(team, loc, 4.5) )
            {
                return false;
            }
        }
        
        return !structureManager.isStructure(location);
    }
    
    public boolean isNearSpawn(GameTeam team, Vector loc, double distance)
    {
        return getSpawn(team).toVector().distanceSquared(loc) <= distance * distance;
    }
    
    public void scheduleStart()
    {
        localArena.getStartScheduler().scheduleStart();
    }
    
    public void cancelStart()
    {
        localArena.getStartScheduler().cancelStarting();
    }
    
    public void updateLobbyScoreboardLayout()
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
    
    public boolean trySignToTeam(GoldHunterPlayer player, GameTeam team)
    {
        if ( signedPlayers.size() >= localArena.getMaxPlayers() && !PlayerRank.VIP.has(player) )
        {
            player.sendMessage("arena_full");
            return false;
        }
        
        if ( team != null && !PlayerRank.VIP.has(player) )
        {
            player.sendMessage("cannot_select_team");
            return false;
        }
        
        signToTeam(player, team);
        return true;
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
        
        localArena.getMetadata().set(StandardArenaMetaData.SIGNED_PLAYERS, signedPlayers.size());
        localArena.uploadRemoteData();
        
        if ( hasGame() && ( signedPlayers.get(GameTeam.RED).size() == 0 || signedPlayers.get(GameTeam.BLUE).size() == 0 ) )
        {
            if ( localArena.getGamePhase() == GamePhase.STARTED )
            {
                winGame(signedPlayers.get(GameTeam.RED).size() == 0 ? GameTeam.BLUE : GameTeam.RED);
            }
            
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
    
//    private void walkover(GameTeam winTeam)
//    {
//        players.forEach(p -> p.sendSeparatedMessage("win_game.walkover", winTeam.getColoredBoldNominative().getValue(p.getPlayer()).toLegacyText().toUpperCase()));
//    }
    
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
        
        signedPlayers.values().forEach(p ->
        {
            p.getPlayer().setAllowFlight(true);
            p.getStatsTracker().onWin();
        });
        
        localArena.setGamePhase(GamePhase.POST_GAME);
        displayWinMessage(winnerTeam);
    }
    
    private void displayWinMessage(GameTeam winnerTeam)
    {
        displayWonTitle(winnerTeam);
        
        localArena.getScheduler().runTaskLater(this::displayChestDestroyers, 0);
        localArena.getScheduler().runTaskLater(this::displayKills, 60);
        localArena.getScheduler().runTaskLater(this::displayRewards, 120);
    }
    
    private void displayWonTitle(GameTeam winnerTeam)
    {
        TranslatableString teamName = winnerTeam.getColoredBoldNominative();
        TranslatableString subtitle = TranslatableString.of(messages, "@win_game");
        
        players.stream().map(GoldHunterPlayer::getPlayer)
                .forEach(p -> p.sendTitle(teamName.getLegacy(p.getLocale()).asString(), subtitle.getLegacy(p.getLocale()).asString(), 0, 80, 20));
    }
    
    private void displayChestDestroyers()
    {
        // TODO:
    }
    
    private void displayKills()
    {
        ArrayList<GoldHunterPlayer> players = new ArrayList<>(this.players);
        Collections.sort(players, Comparator.comparing((GoldHunterPlayer p) -> p.getStatsTracker().getKills()).reversed());
        
        Optional<GoldHunterPlayer> first = ListUtils.getIfExists(players, 0);
        Optional<GoldHunterPlayer> second = ListUtils.getIfExists(players, 1);
        Optional<GoldHunterPlayer> third = ListUtils.getIfExists(players, 2);
        
        players.forEach(p -> p.sendSeparatedMessage("kills_ranking",
                first.map(GoldHunterPlayer::getDisplayName).orElse("&7&m------&r"), first.map(x -> x.getStatsTracker().getKills() + "").orElse(""),
                second.map(GoldHunterPlayer::getDisplayName).orElse("&7&m------&r"), second.map(x -> x.getStatsTracker().getKills() + "").orElse(""),
                third.map(GoldHunterPlayer::getDisplayName).orElse("&7&m------&r"), third.map(x -> x.getStatsTracker().getKills() + "").orElse("")));
    }
    
    private void displayRewards()
    {
        for ( GoldHunterPlayer player : players )
        {
            player.sendMessage("separator");
            player.sendCenteredMessage("rewards.header");
            localArena.getRewards().renderRewards(messages, player.getPlayer());
            player.sendMessage("separator");
        }
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
    
    @Tick
    public void updateGameTime()
    {
        if ( getLocalArena().getGamePhase() == GamePhase.STARTED && MinecraftServer.currentTick % 20 == 0 )
        {
            gameTime++;
            
            String time = TimeStringUtils.minutesAndSeconds(gameTime);
            players.forEach(p -> p.getScoreboardContext().set("gameTime", time));
        }
    }
    
    @Override
    public String toString()
    {
        return "Arena[" + localArena.getId() + "]";
    }
}

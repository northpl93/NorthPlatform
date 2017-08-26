package pl.arieals.minigame.goldhunter;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import pl.arieals.minigame.goldhunter.classes.CharacterClass;
import pl.arieals.minigame.goldhunter.classes.CharacterClassManager;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GoldHunterPlayer implements ITickable
{
    @Inject
    private static IGuiManager guiManager;
    @Inject
    private static CharacterClassManager classManager;
    @Inject
    private static ITickableManager tickableManager;
    @Inject
    @Messages("GoldHunter")
    private static MessagesBox messages;
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    private final SpecialAbilityTracker abilityTracker;
    private final EffectTracker effectTracker;
    
    private final Player player;
    private final GoldHunterArena arena;
    
    private IScoreboardContext scoreboardContext;
    
    private GameTeam team;
    
    private CharacterClass selectedClass = classManager.getDefaultClass();
    private CharacterClass currentClass;
    
    private boolean doubleJumpActive;
    private int noFallDamageTicks;
    
    private boolean buildBridgeActive;
    
    private int kills;
    private int deaths;
    
    private GoldHunterPlayer lastDamager;
    private int lastDamagerTicks;
    
    public GoldHunterPlayer(Player player, GoldHunterArena arena)
    {
        this.player = player;
        this.arena = arena;
        this.abilityTracker = new SpecialAbilityTracker(this);
        this.effectTracker = new EffectTracker(this);
        
        tickableManager.addTickableObject(this);
        tickableManager.addTickableObject(abilityTracker);
    }
    
    public GoldHunterArena getArena()
    {
        return arena;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    public SpecialAbilityTracker getAbilityTracker()
    {
        return abilityTracker;
    }
    
    public EffectTracker getEffectTracker()
    {
        return effectTracker;
    }
    
    public boolean isIngame()
    {
        return team != null;
    }
    
    public GameTeam getTeam()
    {
        return team;
    }
    
    public boolean isDoubleJumpActive()
    {
        return doubleJumpActive;
    }
    
    public void setDoubleJumpActive(boolean doubleJumpActive)
    {
        logger.debug("{} doubleJumpActive = {}", this, doubleJumpActive);
        this.doubleJumpActive = doubleJumpActive;
    }
    
    public boolean isBuildBridgeActive()
    {
        return buildBridgeActive;
    }
    
    public void setBuildBridgeActive(boolean buildBrigdeActive)
    {
        logger.debug("{} buildBridgeActive = {}", this, doubleJumpActive);
        this.buildBridgeActive = buildBrigdeActive;
    }
    
    public int getNoFallDamageTicks()
    {
        return noFallDamageTicks;
    }
    
    public void setNoFallDamageTicks(int noFallDamageTicks)
    {
        this.noFallDamageTicks = noFallDamageTicks;
    }
    
    public int getShopItemLevel(String shopItemName)
    {
        // TODO:
        return 2;
    }
    
    public int getKills()
    {
        return kills;
    }
    
    public int getDeaths()
    {
        return deaths;
    }
    
    public void incrementKills()
    {
        kills++;
        scoreboardContext.set("deaths", deaths);
    }
    
    public void incrementDeaths()
    {
        deaths++;
        scoreboardContext.set("deaths", deaths);
    }
    
    public GoldHunterPlayer getLastDamager()
    {
        return lastDamager;
    }
    
    public void setLastDamager(GoldHunterPlayer lastDamager)
    {
        lastDamagerTicks = 100;
        this.lastDamager = lastDamager;
    }
    
    public boolean hasBuyed(String shopItemName, int shopItemLevel)
    {
        // TODO:
        if ( shopItemLevel > 2 )
        {
            return false;
        }
        
        return true;
    }
    
    public int getAbilityLevel(SpecialAbilityType ability)
    {
        return getShopItemLevel("ability." + ability.name().toLowerCase());
    }
    
    public CharacterClass getCurrentClass()
    {
        return currentClass;
    }
    
    public CharacterClass getSelectedClass()
    {
        return selectedClass;
    }
    
    public void selectClass(CharacterClass newClass)
    {
        Preconditions.checkArgument(newClass != null);
        selectedClass = newClass;
    }
    
    public IScoreboardContext getScoreboardContext()
    {
        return scoreboardContext;
    }
    
    public void setScoreboardContext(IScoreboardContext scoreboardContext)
    {
        this.scoreboardContext = scoreboardContext;
    }
    
    public EntityPlayer getMinecraftPlayer()
    {
        return ((CraftPlayer) player).getHandle();
    }
    
    public void exitGame()
    {
        Preconditions.checkState(isIngame());
        logger.debug("{} exit game", this);
        
        team = null;
        
        effectTracker.clearEffects();
        abilityTracker.setNewAbilityType(null);
        
        kills = 0;
        deaths = 0;
        
        spawnInLobby();
    }
    
    public void spawnInLobby()
    {
        Preconditions.checkState(!isIngame());
        logger.debug("{} spawn in lobby", this);
        
        new LobbyHotbar(this).display(player);
        arena.getScoreboardManager().setLobbyScoreboardLayout(this);
        
        teleportToLobby();
    }
    
    private void teleportToLobby()
    {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
    }
    
    public void joinGame(GameTeam team)
    {
        Preconditions.checkState(!isIngame());
        logger.debug("{} join to {}", this, team);
        
        this.team = team;
        
        guiManager.closeHotbarMenu(player);
        
        arena.getScoreboardManager().setIngameScoreboardLoayout(this);
        
        kills = 0;
        deaths = 0;
        scoreboardContext.set("kills", kills);
        scoreboardContext.set("deaths", deaths);
        
        player.teleport(respawn());
    }
    
    public Location respawn()
    {
        Preconditions.checkState(isIngame());
        logger.debug("{} respawn", this);
        
        currentClass = selectedClass;
        currentClass.applyEquipment(this);
        abilityTracker.setNewAbilityType(currentClass.getSpecialAbility());
        effectTracker.clearEffects();
        
        noFallDamageTicks = 0;
        doubleJumpActive = false;
        buildBridgeActive = false;
        // TODO give eq, set ability, effects etc.
        
        return arena.getTeamSpawn(team);
    }
    
    public String getDisplayName()
    {
        if ( !isIngame() )
        {
            return "§7" + player.getName();
        }
        else
        {
            return team.getTeamColor() + player.getName();
        }
    }
    
    public String getDisplayNameBold()
    {
        if ( !isIngame() )
        {
            return "§7§l" + player.getName();
        }
        else
        {
            return team.getTeamColor() + "§l" + player.getName();
        }
    }
    
    public String getMessage(String msgKey, Object... args)
    {
        return messages.getMessage(player.spigot().getLocale(), msgKey, args);
    }
    
    public String[] getMessageLines(String msgKey, Object... args)
    {
        return messages.getMessage(player.spigot().getLocale(), msgKey, args).split("\n");
    }
    
    public void sendSeparatedMessage(String msgKey, Object... args)
    {
        messages.sendMessage(player, "separator");
        player.sendMessage("");
        messages.sendMessage(player, msgKey, MessageLayout.CENTER, args);
        player.sendMessage("");
        messages.sendMessage(player, "separator");
    }
    
    public void sendMessage(String msgKey, Object... args)
    {
        player.sendMessage(getMessageLines(msgKey, args));
    }
    
    public void doubleJump()
    {
        logger.debug("{} doubleJump", this);
        player.setVelocity(player.getLocation().getDirection().multiply(1.6).setY(0.5));
        setNoFallDamageTicks(200);
    }
    
    @Tick
    private void updateNoFallDamageTicks()
    {
        if ( noFallDamageTicks > 0 )
        {
            noFallDamageTicks--;
        }
    }
    
    @Tick
    private void updateLastDamager()
    {
        if ( lastDamagerTicks > 0 )
        {
            lastDamagerTicks--;
            
            if ( lastDamagerTicks == 0 )
            {
                lastDamager = null;
            }
        }
    }
    
    @Tick
    private void buildBridge()
    {
        if ( !buildBridgeActive )
        {
            return;
        }
        
        
        Block underFoot = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if ( underFoot == null )
        {
            return;
        }
        
        Direction dir = Direction.fromYaw(player.getLocation().getYaw());
        
        for ( int i = 0; i < 3; i++ )
        {
            Block base = underFoot.getRelative(dir.getBlockFace(), i);
            
            buildBrigdeBlock(base);
            buildBrigdeBlock(base.getRelative(dir.turnLeft().getBlockFace()));
            buildBrigdeBlock(base.getRelative(dir.turnRight().getBlockFace()));
        }
    }
    
    private void buildBrigdeBlock(Block block)
    {
        if ( block.getType() == Material.AIR )
        {
            block.setType(Material.WOOD);
            // TODO: spawn particles;
        }
    }
    
    @Override
    public String toString()
    {
        return player.getName();
    }
}

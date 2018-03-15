package pl.arieals.minigame.goldhunter.player;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_12_R1.EntityPlayer;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.shared.Item;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.arena.ArenaBuilder;
import pl.arieals.minigame.goldhunter.arena.GoldHunterArena;
import pl.arieals.minigame.goldhunter.classes.CharacterClass;
import pl.arieals.minigame.goldhunter.classes.CharacterClassManager;
import pl.arieals.minigame.goldhunter.classes.SpecialAbilityType;
import pl.arieals.minigame.goldhunter.gui.LobbyHotbar;
import pl.arieals.minigame.goldhunter.utils.Direction;
import pl.north93.zgame.api.bukkit.entityhider.IEntityHider;
import pl.north93.zgame.api.bukkit.gui.IGuiManager;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.ITickableManager;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.bukkit.utils.itemstack.ItemStackBuilder;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class GoldHunterPlayer implements ITickable
{
    @Inject
    private static IEntityHider entityHider;
    @Inject
    private static IGuiManager guiManager;
    @Inject
    private static CharacterClassManager classManager;
    @Inject
    private static ITickableManager tickableManager;
    @Inject
    private static IGlobalShops globalShops;
    @Inject
    private static ArenaBuilder arenaBuilder;
    @Inject
    @Messages("GoldHunter")
    private static MessagesBox messages;
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    private final SpecialAbilityTracker abilityTracker;
    private final EffectTracker effectTracker;
    private final StatsTracker statsTracker;
    
    private final Player player;
    private final GoldHunterArena arena;
    
    private IScoreboardContext scoreboardContext;
    
    private GameTeam team;
    private GameTeam displayTeam;
    
    private CharacterClass selectedClass = classManager.getDefaultClass();
    private CharacterClass currentClass;
    
    private boolean doubleJumpActive;
    private int noFallDamageTicks;
    
    private boolean buildBridgeActive;
    
    private int toRefilEquipmentTicks;
    
    private boolean shadow;
    
    public GoldHunterPlayer(Player player, GoldHunterArena arena)
    {
        this.player = player;
        this.arena = arena;
        this.abilityTracker = new SpecialAbilityTracker(this);
        this.effectTracker = new EffectTracker(this);
        this.statsTracker = new StatsTracker(this);
        
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
    
    public StatsTracker getStatsTracker()
    {
        return statsTracker;
    }
    
    public boolean isIngame()
    {
        return team != null;
    }
    
    public GameTeam getTeam()
    {
        return team;
    }
    
    public GameTeam getDisplayTeam()
    {
        return displayTeam;
    }
    
    public void setDisplayTeam(GameTeam newTeam)
    {
        displayTeam = newTeam;
        
        if ( isIngame() )
        {
            setLeatherArmorColor();
        }
        
        arena.getScoreboardManager().updateTeamColors();
        updateDisplayName();
    }
    
    public boolean isShadow()
    {
        return shadow;
    }
    
    public void setShadow(boolean shadow)
    {
        this.shadow = shadow;

        arena.getSignedPlayers().forEach(p -> p.updatePlayersVisibility());
    }
    
    public boolean canSee(GoldHunterPlayer other)
    {
        return !other.isShadow() || other.getTeam() == getTeam();
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
        try
        {
            ItemsGroup group = globalShops.getGroup("GoldHunterShop");
            Item item = globalShops.getItem(group, shopItemName);
            return globalShops.getPlayer(player).getBoughtItemLevel(item);
        } catch ( Throwable e )
        {
            return 0;
        }
    }
    
    public boolean hasBuyed(String shopItemName, int shopItemLevel)
    {
        return getShopItemLevel(shopItemName) >= shopItemLevel;
    }
    
    public int getAbilityLevel(SpecialAbilityType ability)
    {
        return getShopItemLevel(ability.getShopItemName());
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
        
        if ( isIngame() )
        {
            if ( player.getLocation().distanceSquared(arena.getTeamSpawn(team)) <= 9 )
            {
                changeClass();
            }
            else 
            {
                sendMessage("class_will_be_changed_after_respawn");
            }
        }
        else
        {
            sendMessage("selected_class");
        }
    }
    
    public void changeClass()
    {
        Preconditions.checkState(selectedClass != null);
        
        currentClass = selectedClass;
        currentClass.applyEquipment(this);
        abilityTracker.setNewAbilityType(currentClass.getSpecialAbility());
        
        addLeatherHatToInventory();
        setLeatherArmorColor();
        
        updateDisplayName();
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
    
    public void addLeatherHatToInventory()
    {
        player.getInventory().setHelmet(new ItemStackBuilder().material(Material.LEATHER_HELMET).hideAttributes().unbreakable().build());
    }
    
    public void setLeatherArmorColor()
    {
        PlayerInventory inv = player.getInventory();
        
        for ( ItemStack armor : inv.getArmorContents() )
        {
            if ( armor == null || ( armor.getType() != Material.LEATHER_BOOTS && armor.getType() != Material.LEATHER_LEGGINGS 
                    && armor.getType() != Material.LEATHER_CHESTPLATE && armor.getType() != Material.LEATHER_HELMET ) )
            {
                continue;
            }
            
            LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
            meta.setColor(displayTeam.getArmorColor());
            armor.setItemMeta(meta);
        }
        
        player.updateInventory();
    }
    
    public void exitGame()
    {
        Preconditions.checkState(isIngame());
        logger.debug("{} exit game", this);
        
        team = null;
        currentClass = null;
        
        statsTracker.clear();
        
        setDisplayTeam(null);
        
        updateDisplayName();
        spawnInLobby();
    }
    
    public void spawnInLobby()
    {
        Preconditions.checkState(!isIngame());
        logger.debug("{} spawn in lobby", this);
        
        setupPlayerEntity();
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
        
        updatePlayersVisibility();
        this.team = team;
        this.displayTeam = team;
        
        guiManager.closeHotbarMenu(player);
        
        arena.getScoreboardManager().setIngameScoreboardLoayout(this);
        
        statsTracker.clear();
        
        player.teleport(respawn());
    }
    
    private void updatePlayersVisibility()
    {
        for ( GoldHunterPlayer p : arena.getSignedPlayers() )
        {
            entityHider.setEntityVisible(player, p.getPlayer(), canSee(p));
        }
    }
    
    public Location respawn()
    {
        Preconditions.checkState(isIngame());
        logger.debug("{} respawn", this);
        
        setupPlayerEntity();
        
        setDisplayTeam(team);
         
        changeClass();
        
        toRefilEquipmentTicks = currentClass.getInventoryRefilTime();
         
        return arena.getTeamSpawn(team);
    }
    
    private void setupPlayerEntity()
    {
        player.setAllowFlight(false);
        player.setFlying(false);
        
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setVelocity(new Vector(0, 0, 0));
        
        effectTracker.clearEffects();
        abilityTracker.setNewAbilityType(null);
        
        setNoFallDamageTicks(0);
        setBuildBridgeActive(false);
        setShadow(false);
        setDoubleJumpActive(false);
        
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
    }
    
    public void die()
    {
        Preconditions.checkState(isIngame());
        logger.debug("{} die", this);
        
        arena.broadcastDeath(this, statsTracker.getKiller());
        
        statsTracker.onDie();
        
        player.teleport(respawn());
        playDeathEffect();
    }
    
    private void playDeathEffect()
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false), true);
    }
    
    public void updateDisplayName()
    {
        if ( !isIngame() )
        {
            player.setDisplayName("§7" + player.getName());
            player.setPlayerListName("§7" + player.getName());
        }
        else
        {
            player.setDisplayName(getDisplayName());
            player.setPlayerListName(getDisplayName());
        }
    }
    
    public String getDisplayName()
    {
        if ( !isIngame() )
        {
            return "§7" + player.getName();
        }
        else
        {
            return displayTeam.getTeamColor() + player.getName();
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
        return messages.getMessage(player.getLocale(), msgKey, args); 
    }
    
    public String[] getMessageLines(String msgKey, Object... args)
    {
        System.out.println("Dupa");
        player.sendMessage(player.spigot().getLocale());
        player.sendMessage(player.getLocale());
        player.sendMessage(getMinecraftPlayer().locale);
        Locale locale = Locale.forLanguageTag(player.getLocale());
        player.sendMessage(locale + "");
        return messages.getMessage(player.getLocale(), msgKey, args).split("\n");
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
            Block block = underFoot.getRelative(dir.getBlockFace(), i);
            
            if ( block.getType() == Material.AIR )
            {
                arenaBuilder.tryBuild(block, Material.WOOD).whenSuccess(() -> spawnBrigdeparticles(block));
            }
        }
    }
    
    private void spawnBrigdeparticles(Block block)
    {
        double offX = ThreadLocalRandom.current().nextGaussian() * 0.13;
        double offY = Math.abs(ThreadLocalRandom.current().nextGaussian()) * 0.13;
        double offZ = ThreadLocalRandom.current().nextGaussian() * 0.13;
        block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5, 1, offX, offY, offZ, 0, null);
    }
    
    @Tick
    public void updateRefilEquipmentTicks()
    {
        if ( !isIngame() || currentClass.getInventoryRefilRule() == null )
        {
            return;
        }
        
        if ( toRefilEquipmentTicks > 0 )
        {
            toRefilEquipmentTicks--;
        }
        
        if ( toRefilEquipmentTicks == 0 )
        {
            currentClass.getInventoryRefilRule().tryRefilEquipment(this);
            toRefilEquipmentTicks = currentClass.getInventoryRefilTime();
        }
    }
    
    public void tryRefilItem(ItemStack is, int maxCount)
    {
        int currentCount = 0;
        
        InventoryView openInventory = player.getOpenInventory();
        if ( openInventory.getCursor() != null && openInventory.getCursor().getType() == is.getType() )
        {
            currentCount += openInventory.getCursor().getAmount();
        }
        
        for ( ItemStack item : openInventory.getTopInventory().getContents() )
        {
            if ( item != null && item.getType() == is.getType() )
            {
                currentCount += item.getAmount();
            }
        }
        
        for ( ItemStack item : openInventory.getBottomInventory().getContents() )
        {
            if ( item != null && item.getType() == is.getType() )
            {
                currentCount += item.getAmount();
            }
        }
        
        if ( currentCount >= maxCount )
        {
            return;
        }
        
        int diff = maxCount - currentCount;
        is.setAmount(Math.min(diff, is.getAmount()));
        player.getInventory().addItem(is);
    }
    
    @Override
    public String toString()
    {
        return player.getName();
    }
}

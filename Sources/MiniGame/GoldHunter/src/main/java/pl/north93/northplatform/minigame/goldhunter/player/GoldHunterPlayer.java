package pl.north93.northplatform.minigame.goldhunter.player;

import com.google.common.base.Preconditions;
import net.minecraft.server.v1_12_R1.DamageSource;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.slf4j.Logger;
import pl.north93.northplatform.minigame.goldhunter.GoldHunterLogger;
import pl.north93.northplatform.minigame.goldhunter.arena.ArenaBuilder;
import pl.north93.northplatform.minigame.goldhunter.arena.GoldHunterArena;
import pl.north93.northplatform.minigame.goldhunter.classes.CharacterClass;
import pl.north93.northplatform.minigame.goldhunter.classes.CharacterClassManager;
import pl.north93.northplatform.minigame.goldhunter.effect.RespawnProtection;
import pl.north93.northplatform.minigame.goldhunter.gui.LobbyHotbar;
import pl.north93.northplatform.minigame.goldhunter.utils.Direction;
import pl.north93.northplatform.api.bukkit.entityhider.IEntityHider;
import pl.north93.northplatform.api.bukkit.gui.IGuiManager;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.tick.ITickable;
import pl.north93.northplatform.api.bukkit.tick.ITickableManager;
import pl.north93.northplatform.api.bukkit.tick.Tick;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemStackBuilder;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.server.gamehost.reward.CurrencyReward;
import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    @GoldHunterLogger(useToString = true)
    private static Logger logger;
    @Inject
    private static IEconomyManager economyManager;
    
    private final SpecialAbilityTracker abilityTracker;
    private final EffectTracker effectTracker;
    private final StatsTracker statsTracker;
    private final InventoryRefilTracker inventoryRefilTracker;
    
    private /*final*/ Player player;
    private final GoldHunterArena arena;
    
    private IScoreboardContext scoreboardContext;
    
    private GameTeam team;
    private GameTeam displayTeam;
    
    private CharacterClass selectedClass = classManager.getDefaultClass();
    private CharacterClass currentClass;
    
    private boolean doubleJumpActive;
    private int noFallDamageTicks;
    
    private boolean buildBridgeActive;
    
    private boolean shadow;
    
    private int gameTime;
    
    public GoldHunterPlayer(Player player, GoldHunterArena arena)
    {
        this.player = player;
        this.arena = arena;
        this.abilityTracker = new SpecialAbilityTracker(this);
        this.effectTracker = new EffectTracker(this);
        this.statsTracker = new StatsTracker(this);
        this.inventoryRefilTracker = new InventoryRefilTracker(this);
        
        tickableManager.addTickableObject(this);
        tickableManager.addTickableObject(abilityTracker);
        tickableManager.addTickableObject(inventoryRefilTracker);
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
        
        getMinecraftPlayer().setInvisible(shadow);
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
    
    public void heal(double amount)
    {
        double currentHealth = getPlayer().getPlayer().getHealth();
        double maxHealth = getPlayer().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        
        getPlayer().setHealth(Math.min(maxHealth, currentHealth + amount));
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
        currentClass.applyEffects(this);
        currentClass.applyEquipment(this);
        abilityTracker.setNewAbilityType(currentClass.getSpecialAbility());
        effectTracker.clearEffects();
        
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
        return INorthPlayer.asCraftPlayer(player).getHandle();
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
        
        gameTime = 0;
        
        team = null;
        currentClass = null;
        
        statsTracker.clear();
        
        setDisplayTeam(null);
        
        updateDisplayName();
        updatePlayersDisplayName();
        spawnInLobby();
    }
    
    public void spawnInLobby()
    {
        Preconditions.checkState(!isIngame());
        logger.debug("{} spawn in lobby", this);
        
        setupPlayerEntity();
        new LobbyHotbar(this).display(player);
        arena.getScoreboardManager().setLobbyScoreboardLayout(this);
        updateSkullsOnScoreboard();
        
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
        
        updatePlayersDisplayName();
        updatePlayersVisibility();
        this.team = team;
        this.displayTeam = team;
        
        guiManager.closeHotbarMenu(player);
        
        arena.getScoreboardManager().setIngameScoreboardLoayout(this);
        updateSkullsOnScoreboard();
        
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
        
        effectTracker.addEffect(new RespawnProtection(), 70);
         
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
    
    public void damage(double value)
    {
        logger.debug("call GoldHunterPlayer#damage() with {}", value);
        getMinecraftPlayer().damageEntity(DamageSource.GENERIC, (float) value);
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
    
    private void updatePlayersDisplayName()
    {
        List<EntityPlayer> players = arena.getPlayers().stream().map(GoldHunterPlayer::getMinecraftPlayer).collect(Collectors.toList());
        
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, players);
        getMinecraftPlayer().playerConnection.sendPacket(packet);
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
    
    public void addReward(String rewardId, double amount)
    {
        arena.getLocalArena().getRewards().addReward(Identity.of(player), new CurrencyReward(rewardId, "minigame", amount));
        updateSkullsOnScoreboard(); // TODO: make an event
    }
    
    public void updateSkullsOnScoreboard()
    {
        if ( scoreboardContext != null )
        {
            ICurrency currency = economyManager.getCurrency("minigame");
            scoreboardContext.set("skulls", (int) economyManager.getUnsafeAccessor(currency, Identity.of(player)).getAmount());
        }
    }
    
    public String getMessage(String msgKey, Object... args)
    {
        return messages.getString(player.getLocale(), msgKey, args);
    }
    
    public String[] getMessageLines(String msgKey, Object... args)
    {
        return messages.getString(player.getLocale(), msgKey, args).split("\n");
    }
    
    public void sendSeparatedMessage(String msgKey, Object... args)
    {
        INorthPlayer player = INorthPlayer.wrap(this.player);
        player.sendMessage(messages, "separator");
        player.sendMessage(messages, msgKey, MessageLayout.CENTER, args);
        player.sendMessage(messages, "separator");
    }
    
    public void sendMessage(String msgKey, Object... args)
    {
        player.sendMessage(getMessageLines(msgKey, args));
    }
    
    public void sendCenteredMessage(String msgKey, Object... args)
    {
        INorthPlayer.wrap(player).sendMessage(messages, msgKey, MessageLayout.CENTER, args);
    }
    
    public void sendActionBar(String msgKey, Object... args)
    {
        player.sendActionBar(messages.getString(player.getLocale(), msgKey, args));
    }
    
    public void doubleJump()
    {
        logger.debug("{} doubleJump", this);
        player.setVelocity(player.getLocation().getDirection().multiply(1.6).setY(0.5));
        setNoFallDamageTicks(200);
    }
    
    @Tick
    private void updateGameTime()
    {   
        if ( isIngame() )
        {
            gameTime++;
        }
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
    private void showInfoSubtitle()
    {
        if ( MinecraftServer.currentTick % 15 != 0 )
        {
            return;
        }
        
        String titleMessage = null;
        if ( !isIngame() && !arena.getSignedPlayers().contains(this) )
        {
            titleMessage = getMessage("info.sign_to_game");
        }
        else if ( isIngame() && gameTime < 400 && arena.isNearSpawn(team, player.getLocation().toVector(), 2.5) )
        {
            titleMessage = getMessage("info.select_class");
        }
        
        
        if ( titleMessage != null )
        {
            player.sendTitle("§0", titleMessage, 0, 10, 12);
        }
    }
    
    @Override
    public String toString()
    {
        return player.getName();
    }

    
}

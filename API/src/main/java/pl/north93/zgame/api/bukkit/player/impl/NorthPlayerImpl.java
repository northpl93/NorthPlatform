package pl.north93.zgame.api.bukkit.player.impl;

import static pl.north93.zgame.api.bukkit.player.INorthPlayer.asCraftPlayer;


import javax.annotation.Nullable;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.profile.PlayerProfile;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.Sign;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.network.server.ServerProxyData;
import pl.north93.zgame.api.global.network.server.joinaction.IServerJoinAction;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.redis.observable.Value;

/*default*/ class NorthPlayerImpl implements INorthPlayer
{
    private final INetworkManager      networkManager;
    private final Player               bukkitPlayer;
    private final Value<IOnlinePlayer> playerData;

    public NorthPlayerImpl(final INetworkManager networkManager, final Player bukkitPlayer, final Value<IOnlinePlayer> playerData)
    {
        this.networkManager = networkManager;
        this.bukkitPlayer = bukkitPlayer;
        this.playerData = playerData;
    }

    @Override
    public CraftPlayer getCraftPlayer()
    {
        return (CraftPlayer) this.bukkitPlayer;
    }

    @Override
    public IPlayerTransaction openTransaction()
    {
        try
        {
            return this.networkManager.getPlayers().transaction(Identity.of(this.bukkitPlayer));
        }
        catch (final PlayerNotFoundException e)
        {
            throw new RuntimeException("Not found player in NorthPlayer.", e);
        }
    }

    @Override
    public boolean isPremium()
    {
        final IOnlinePlayer playerData = this.playerData.get();
        return playerData.isPremium();
    }

    @Override
    public Locale getMyLocale()
    {
        final IOnlinePlayer playerData = this.playerData.get();
        return playerData.getMyLocale();
    }

    @Override
    public void sendMessage(final String message, final MessageLayout layout)
    {
        final BaseComponent component = ChatUtils.fromLegacyText(message);
        this.bukkitPlayer.sendMessage(layout.processMessage(component));
    }

    @Override
    public void sendMessage(final BaseComponent component, final MessageLayout layout)
    {
        this.bukkitPlayer.sendMessage(layout.processMessage(component));
    }

    @Override
    public pl.north93.zgame.api.global.network.server.Server getCurrentServer()
    {
        final IOnlinePlayer playerData = this.playerData.get();
        return this.networkManager.getServers().withUuid(playerData.getServerId());
    }

    @Override
    public Group getGroup()
    {
        final IOnlinePlayer playerData = this.playerData.get();
        return playerData.getGroup();
    }

    @Override
    public void connectTo(final ServerProxyData server, final IServerJoinAction... actions)
    {
        final IOnlinePlayer playerData = this.playerData.get();
        playerData.connectTo(server, actions);
    }

    @Override
    public void connectTo(final String serversGroupName, final IServerJoinAction... actions)
    {
        final IOnlinePlayer playerData = this.playerData.get();
        playerData.connectTo(serversGroupName, actions);
    }

    @Override
    public boolean isDataCached()
    {
        return this.playerData.isCached();
    }

    @Override
    public MetaStore getMetaStore()
    {
        final IOnlinePlayer playerData = this.playerData.get();

        // zwracamy kopie, aby nikt nic nie zepsul, ale dalej
        // istnieje mozliwosc zmodyfikowania wartosci jesli sa one mutable.
        return new MetaStore(playerData.getMetaStore());
    }

    // wewnetrzna metoda uzywana przez implementacje.
    public Value<IOnlinePlayer> getValue()
    {
        return this.playerData;
    }

    // = = = DELEGOWANE METODY = = = //

    @Override
    public String getDisplayName()
    {
        return this.bukkitPlayer.getDisplayName();
    }

    @Override
    public void setDisplayName(final String s)
    {
        this.bukkitPlayer.setDisplayName(s);
    }

    @Override
    public String getPlayerListName()
    {
        return this.bukkitPlayer.getPlayerListName();
    }

    @Override
    public void setPlayerListName(final String s)
    {
        this.bukkitPlayer.setPlayerListName(s);
    }

    @Override
    public void setCompassTarget(final Location location)
    {
        this.bukkitPlayer.setCompassTarget(location);
    }

    @Override
    public Location getCompassTarget()
    {
        return this.bukkitPlayer.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress()
    {
        return this.bukkitPlayer.getAddress();
    }

    @Override
    public void sendRawMessage(final String s)
    {
        this.bukkitPlayer.sendRawMessage(s);
    }

    @Override
    public void kickPlayer(final String s)
    {
        this.bukkitPlayer.kickPlayer(s);
    }

    @Override
    public void chat(final String s)
    {
        this.bukkitPlayer.chat(s);
    }

    @Override
    public boolean performCommand(final String s)
    {
        return this.bukkitPlayer.performCommand(s);
    }

    @Override
    public boolean isSneaking()
    {
        return this.bukkitPlayer.isSneaking();
    }

    @Override
    public void setSneaking(final boolean b)
    {
        this.bukkitPlayer.setSneaking(b);
    }

    @Override
    public boolean isSprinting()
    {
        return this.bukkitPlayer.isSprinting();
    }

    @Override
    public void setSprinting(final boolean b)
    {
        this.bukkitPlayer.setSprinting(b);
    }

    @Override
    public void saveData()
    {
        this.bukkitPlayer.saveData();
    }

    @Override
    public void loadData()
    {
        this.bukkitPlayer.loadData();
    }

    @Override
    public void setSleepingIgnored(final boolean b)
    {
        this.bukkitPlayer.setSleepingIgnored(b);
    }

    @Override
    public boolean isSleepingIgnored()
    {
        return this.bukkitPlayer.isSleepingIgnored();
    }

    @Override
    @Deprecated
    public void playNote(final Location location, final byte b, final byte b1)
    {
        this.bukkitPlayer.playNote(location, b, b1);
    }

    @Override
    public void playNote(final Location location, final Instrument instrument, final Note note)
    {
        this.bukkitPlayer.playNote(location, instrument, note);
    }

    @Override
    public void playSound(final Location location, final Sound sound, final float v, final float v1)
    {
        this.bukkitPlayer.playSound(location, sound, v, v1);
    }

    @Override
    public void playSound(final Location location, final String s, final float v, final float v1)
    {
        this.bukkitPlayer.playSound(location, s, v, v1);
    }

    @Override
    public void playSound(final Location location, final Sound sound, final SoundCategory soundCategory, final float v, final float v1)
    {
        this.bukkitPlayer.playSound(location, sound, soundCategory, v, v1);
    }

    @Override
    public void playSound(final Location location, final String s, final SoundCategory soundCategory, final float v, final float v1)
    {
        this.bukkitPlayer.playSound(location, s, soundCategory, v, v1);
    }

    @Override
    public void stopSound(final Sound sound)
    {
        this.bukkitPlayer.stopSound(sound);
    }

    @Override
    public void stopSound(final String s)
    {
        this.bukkitPlayer.stopSound(s);
    }

    @Override
    public void stopSound(final Sound sound, final SoundCategory soundCategory)
    {
        this.bukkitPlayer.stopSound(sound, soundCategory);
    }

    @Override
    public void stopSound(final String s, final SoundCategory soundCategory)
    {
        this.bukkitPlayer.stopSound(s, soundCategory);
    }

    @Override
    @Deprecated
    public void playEffect(final Location location, final Effect effect, final int i)
    {
        this.bukkitPlayer.playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(final Location location, final Effect effect, final T t)
    {
        this.bukkitPlayer.playEffect(location, effect, t);
    }

    @Override
    @Deprecated
    public void sendBlockChange(final Location location, final Material material, final byte b)
    {
        this.bukkitPlayer.sendBlockChange(location, material, b);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(final Location location, final int i, final int i1, final int i2, final byte[] bytes)
    {
        return this.bukkitPlayer.sendChunkChange(location, i, i1, i2, bytes);
    }

    @Override
    @Deprecated
    public void sendBlockChange(final Location location, final int i, final byte b)
    {
        this.bukkitPlayer.sendBlockChange(location, i, b);
    }

    @Override
    public void sendSignChange(final Location location, final String[] strings) throws IllegalArgumentException
    {
        this.bukkitPlayer.sendSignChange(location, strings);
    }

    @Override
    public void sendMap(final MapView mapView)
    {
        this.bukkitPlayer.sendMap(mapView);
    }

    @Override
    public void sendActionBar(final String s)
    {
        this.bukkitPlayer.sendActionBar(s);
    }

    @Override
    public void sendActionBar(final char c, final String s)
    {
        this.bukkitPlayer.sendActionBar(c, s);
    }

    @Override
    public void sendMessage(final BaseComponent component)
    {
        this.bukkitPlayer.sendMessage(component);
    }

    @Override
    public void sendMessage(final BaseComponent... components)
    {
        this.bukkitPlayer.sendMessage(components);
    }

    @Override
    @Deprecated
    public void sendMessage(final ChatMessageType position, final BaseComponent... components)
    {
        this.bukkitPlayer.sendMessage(position, components);
    }

    @Override
    public void setPlayerListHeaderFooter(final BaseComponent[] baseComponents, final BaseComponent[] baseComponents1)
    {
        this.bukkitPlayer.setPlayerListHeaderFooter(baseComponents, baseComponents1);
    }

    @Override
    public void setPlayerListHeaderFooter(final BaseComponent baseComponent, final BaseComponent baseComponent1)
    {
        this.bukkitPlayer.setPlayerListHeaderFooter(baseComponent, baseComponent1);
    }

    @Override
    @Deprecated
    public void setTitleTimes(final int i, final int i1, final int i2)
    {
        this.bukkitPlayer.setTitleTimes(i, i1, i2);
    }

    @Override
    @Deprecated
    public void setSubtitle(final BaseComponent[] baseComponents)
    {
        this.bukkitPlayer.setSubtitle(baseComponents);
    }

    @Override
    @Deprecated
    public void setSubtitle(final BaseComponent baseComponent)
    {
        this.bukkitPlayer.setSubtitle(baseComponent);
    }

    @Override
    @Deprecated
    public void showTitle(final BaseComponent[] baseComponents)
    {
        this.bukkitPlayer.showTitle(baseComponents);
    }

    @Override
    @Deprecated
    public void showTitle(final BaseComponent baseComponent)
    {
        this.bukkitPlayer.showTitle(baseComponent);
    }

    @Override
    @Deprecated
    public void showTitle(final BaseComponent[] baseComponents, final BaseComponent[] baseComponents1, final int i, final int i1, final int i2)
    {
        this.bukkitPlayer.showTitle(baseComponents, baseComponents1, i, i1, i2);
    }

    @Override
    @Deprecated
    public void showTitle(final BaseComponent baseComponent, final BaseComponent baseComponent1, final int i, final int i1, final int i2)
    {
        this.bukkitPlayer.showTitle(baseComponent, baseComponent1, i, i1, i2);
    }

    @Override
    public void sendTitle(final Title title)
    {
        this.bukkitPlayer.sendTitle(title);
    }

    @Override
    public void updateTitle(final Title title)
    {
        this.bukkitPlayer.updateTitle(title);
    }

    @Override
    public void hideTitle()
    {
        this.bukkitPlayer.hideTitle();
    }

    @Override
    public void updateInventory()
    {
        this.bukkitPlayer.updateInventory();
    }

    @Override
    @Deprecated
    public void awardAchievement(final Achievement achievement)
    {
        this.bukkitPlayer.awardAchievement(achievement);
    }

    @Override
    @Deprecated
    public void removeAchievement(final Achievement achievement)
    {
        this.bukkitPlayer.removeAchievement(achievement);
    }

    @Override
    @Deprecated
    public boolean hasAchievement(final Achievement achievement)
    {
        return this.bukkitPlayer.hasAchievement(achievement);
    }

    @Override
    public void incrementStatistic(final Statistic statistic) throws IllegalArgumentException
    {
        this.bukkitPlayer.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(final Statistic statistic) throws IllegalArgumentException
    {
        this.bukkitPlayer.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(final Statistic statistic, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(final Statistic statistic, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(final Statistic statistic, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(final Statistic statistic) throws IllegalArgumentException
    {
        return this.bukkitPlayer.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(final Statistic statistic, final Material material) throws IllegalArgumentException
    {
        this.bukkitPlayer.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(final Statistic statistic, final Material material) throws IllegalArgumentException
    {
        this.bukkitPlayer.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(final Statistic statistic, final Material material) throws IllegalArgumentException
    {
        return this.bukkitPlayer.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(final Statistic statistic, final Material material, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(final Statistic statistic, final Material material, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(final Statistic statistic, final Material material, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(final Statistic statistic, final EntityType entityType) throws IllegalArgumentException
    {
        this.bukkitPlayer.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(final Statistic statistic, final EntityType entityType) throws IllegalArgumentException
    {
        this.bukkitPlayer.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(final Statistic statistic, final EntityType entityType) throws IllegalArgumentException
    {
        return this.bukkitPlayer.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(final Statistic statistic, final EntityType entityType, final int i) throws IllegalArgumentException
    {
        this.bukkitPlayer.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(final Statistic statistic, final EntityType entityType, final int i)
    {
        this.bukkitPlayer.decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(final Statistic statistic, final EntityType entityType, final int i)
    {
        this.bukkitPlayer.setStatistic(statistic, entityType, i);
    }

    @Override
    public void setPlayerTime(final long l, final boolean b)
    {
        this.bukkitPlayer.setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime()
    {
        return this.bukkitPlayer.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset()
    {
        return this.bukkitPlayer.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative()
    {
        return this.bukkitPlayer.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime()
    {
        this.bukkitPlayer.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(final WeatherType weatherType)
    {
        this.bukkitPlayer.setPlayerWeather(weatherType);
    }

    @Override
    public WeatherType getPlayerWeather()
    {
        return this.bukkitPlayer.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather()
    {
        this.bukkitPlayer.resetPlayerWeather();
    }

    @Override
    public void giveExp(final int amount)
    {
        this.bukkitPlayer.giveExp(amount);
    }

    @Override
    public void giveExp(final int i, final boolean b)
    {
        this.bukkitPlayer.giveExp(i, b);
    }

    @Override
    public int applyMending(final int i)
    {
        return this.bukkitPlayer.applyMending(i);
    }

    @Override
    public void giveExpLevels(final int i)
    {
        this.bukkitPlayer.giveExpLevels(i);
    }

    @Override
    public float getExp()
    {
        return this.bukkitPlayer.getExp();
    }

    @Override
    public void setExp(final float v)
    {
        this.bukkitPlayer.setExp(v);
    }

    @Override
    public int getLevel()
    {
        return this.bukkitPlayer.getLevel();
    }

    @Override
    public void setLevel(final int i)
    {
        this.bukkitPlayer.setLevel(i);
    }

    @Override
    public int getTotalExperience()
    {
        return this.bukkitPlayer.getTotalExperience();
    }

    @Override
    public void setTotalExperience(final int i)
    {
        this.bukkitPlayer.setTotalExperience(i);
    }

    @Override
    public float getExhaustion()
    {
        return this.bukkitPlayer.getExhaustion();
    }

    @Override
    public void setExhaustion(final float v)
    {
        this.bukkitPlayer.setExhaustion(v);
    }

    @Override
    public float getSaturation()
    {
        return this.bukkitPlayer.getSaturation();
    }

    @Override
    public void setSaturation(final float v)
    {
        this.bukkitPlayer.setSaturation(v);
    }

    @Override
    public int getFoodLevel()
    {
        return this.bukkitPlayer.getFoodLevel();
    }

    @Override
    public void setFoodLevel(final int i)
    {
        this.bukkitPlayer.setFoodLevel(i);
    }

    @Override
    public Location getBedSpawnLocation()
    {
        return this.bukkitPlayer.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(final Location location)
    {
        this.bukkitPlayer.setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(final Location location, final boolean b)
    {
        this.bukkitPlayer.setBedSpawnLocation(location, b);
    }

    @Override
    public boolean getAllowFlight()
    {
        return this.bukkitPlayer.getAllowFlight();
    }

    @Override
    public void setAllowFlight(final boolean b)
    {
        this.bukkitPlayer.setAllowFlight(b);
    }

    @Override
    @Deprecated
    public void hidePlayer(final Player player)
    {
        this.bukkitPlayer.hidePlayer(asCraftPlayer(player));
    }

    @Override
    public void hidePlayer(final Plugin plugin, final Player player)
    {
        this.bukkitPlayer.hidePlayer(plugin, asCraftPlayer(player));
    }

    @Override
    @Deprecated
    public void showPlayer(final Player player)
    {
        this.bukkitPlayer.showPlayer(asCraftPlayer(player));
    }

    @Override
    public void showPlayer(final Plugin plugin, final Player player)
    {
        this.bukkitPlayer.showPlayer(plugin, asCraftPlayer(player));
    }

    @Override
    public boolean canSee(final Player player)
    {
        return this.bukkitPlayer.canSee(asCraftPlayer(player));
    }

    @Override
    public boolean isFlying()
    {
        return this.bukkitPlayer.isFlying();
    }

    @Override
    public void setFlying(final boolean b)
    {
        this.bukkitPlayer.setFlying(b);
    }

    @Override
    public void setFlySpeed(final float v) throws IllegalArgumentException
    {
        this.bukkitPlayer.setFlySpeed(v);
    }

    @Override
    public void setWalkSpeed(final float v) throws IllegalArgumentException
    {
        this.bukkitPlayer.setWalkSpeed(v);
    }

    @Override
    public float getFlySpeed()
    {
        return this.bukkitPlayer.getFlySpeed();
    }

    @Override
    public float getWalkSpeed()
    {
        return this.bukkitPlayer.getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(final String s)
    {
        this.bukkitPlayer.setTexturePack(s);
    }

    @Override
    @Deprecated
    public void setResourcePack(final String s)
    {
        this.bukkitPlayer.setResourcePack(s);
    }

    @Override
    public void setResourcePack(final String s, final byte[] bytes)
    {
        this.bukkitPlayer.setResourcePack(s, bytes);
    }

    @Override
    public Scoreboard getScoreboard()
    {
        return this.bukkitPlayer.getScoreboard();
    }

    @Override
    public void setScoreboard(final Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException
    {
        this.bukkitPlayer.setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled()
    {
        return this.bukkitPlayer.isHealthScaled();
    }

    @Override
    public void setHealthScaled(final boolean b)
    {
        this.bukkitPlayer.setHealthScaled(b);
    }

    @Override
    public void setHealthScale(final double v) throws IllegalArgumentException
    {
        this.bukkitPlayer.setHealthScale(v);
    }

    @Override
    public double getHealthScale()
    {
        return this.bukkitPlayer.getHealthScale();
    }

    @Override
    public Entity getSpectatorTarget()
    {
        return this.bukkitPlayer.getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(final Entity entity)
    {
        this.bukkitPlayer.setSpectatorTarget(entity);
    }

    @Override
    @Deprecated
    public void sendTitle(final String s, final String s1)
    {
        this.bukkitPlayer.sendTitle(s, s1);
    }

    @Override
    public void sendTitle(final String s, final String s1, final int i, final int i1, final int i2)
    {
        this.bukkitPlayer.sendTitle(s, s1, i, i1, i2);
    }

    @Override
    public void resetTitle()
    {
        this.bukkitPlayer.resetTitle();
    }

    @Override
    public void spawnParticle(final Particle particle, final Location location, final int i)
    {
        this.bukkitPlayer.spawnParticle(particle, location, i);
    }

    @Override
    public void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i)
    {
        this.bukkitPlayer.spawnParticle(particle, v, v1, v2, i);
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final T t)
    {
        this.bukkitPlayer.spawnParticle(particle, location, i, t);
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final T t)
    {
        this.bukkitPlayer.spawnParticle(particle, v, v1, v2, i, t);
    }

    @Override
    public void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2)
    {
        this.bukkitPlayer.spawnParticle(particle, location, i, v, v1, v2);
    }

    @Override
    public void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5)
    {
        this.bukkitPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5);
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final T t)
    {
        this.bukkitPlayer.spawnParticle(particle, location, i, v, v1, v2, t);
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final T t)
    {
        this.bukkitPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, t);
    }

    @Override
    public void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final double v3)
    {
        this.bukkitPlayer.spawnParticle(particle, location, i, v, v1, v2, v3);
    }

    @Override
    public void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final double v6)
    {
        this.bukkitPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6);
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final Location location, final int i, final double v, final double v1, final double v2, final double v3, final T t)
    {
        this.bukkitPlayer.spawnParticle(particle, location, i, v, v1, v2, v3, t);
    }

    @Override
    public <T> void spawnParticle(final Particle particle, final double v, final double v1, final double v2, final int i, final double v3, final double v4, final double v5, final double v6, final T t)
    {
        this.bukkitPlayer.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6, t);
    }

    @Override
    public AdvancementProgress getAdvancementProgress(final Advancement advancement)
    {
        return this.bukkitPlayer.getAdvancementProgress(advancement);
    }

    @Override
    public String getLocale()
    {
        return this.bukkitPlayer.getLocale();
    }

    @Override
    public boolean getAffectsSpawning()
    {
        return this.bukkitPlayer.getAffectsSpawning();
    }

    @Override
    public void setAffectsSpawning(final boolean b)
    {
        this.bukkitPlayer.setAffectsSpawning(b);
    }

    @Override
    public int getViewDistance()
    {
        return this.bukkitPlayer.getViewDistance();
    }

    @Override
    public void setViewDistance(final int i)
    {
        this.bukkitPlayer.setViewDistance(i);
    }

    @Override
    public void setResourcePack(final String s, final String s1)
    {
        this.bukkitPlayer.setResourcePack(s, s1);
    }

    @Override
    public PlayerResourcePackStatusEvent.Status getResourcePackStatus()
    {
        return this.bukkitPlayer.getResourcePackStatus();
    }

    @Override
    public String getResourcePackHash()
    {
        return this.bukkitPlayer.getResourcePackHash();
    }

    @Override
    public boolean hasResourcePack()
    {
        return this.bukkitPlayer.hasResourcePack();
    }

    @Override
    public PlayerProfile getPlayerProfile()
    {
        return this.bukkitPlayer.getPlayerProfile();
    }

    @Override
    public void setPlayerProfile(final PlayerProfile playerProfile)
    {
        this.bukkitPlayer.setPlayerProfile(playerProfile);
    }

    @Override
    public Spigot spigot()
    {
        return this.bukkitPlayer.spigot();
    }

    @Override
    public String getName()
    {
        return this.bukkitPlayer.getName();
    }

    @Override
    public PlayerInventory getInventory()
    {
        return this.bukkitPlayer.getInventory();
    }

    @Override
    public Inventory getEnderChest()
    {
        return this.bukkitPlayer.getEnderChest();
    }

    @Override
    public MainHand getMainHand()
    {
        return this.bukkitPlayer.getMainHand();
    }

    @Override
    public boolean setWindowProperty(final InventoryView.Property property, final int i)
    {
        return this.bukkitPlayer.setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory()
    {
        return this.bukkitPlayer.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(final Inventory inventory)
    {
        return this.bukkitPlayer.openInventory(inventory);
    }

    @Override
    public InventoryView openWorkbench(final Location location, final boolean b)
    {
        return this.bukkitPlayer.openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(final Location location, final boolean b)
    {
        return this.bukkitPlayer.openEnchanting(location, b);
    }

    @Override
    public void openInventory(final InventoryView inventoryView)
    {
        this.bukkitPlayer.openInventory(inventoryView);
    }

    @Override
    public InventoryView openMerchant(final Villager villager, final boolean b)
    {
        return this.bukkitPlayer.openMerchant(villager, b);
    }

    @Override
    public InventoryView openMerchant(final Merchant merchant, final boolean b)
    {
        return this.bukkitPlayer.openMerchant(merchant, b);
    }

    @Override
    public void closeInventory()
    {
        this.bukkitPlayer.closeInventory();
    }

    @Override
    @Deprecated
    public ItemStack getItemInHand()
    {
        return this.bukkitPlayer.getItemInHand();
    }

    @Override
    @Deprecated
    public void setItemInHand(final ItemStack itemStack)
    {
        this.bukkitPlayer.setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor()
    {
        return this.bukkitPlayer.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(final ItemStack itemStack)
    {
        this.bukkitPlayer.setItemOnCursor(itemStack);
    }

    @Override
    public boolean hasCooldown(final Material material)
    {
        return this.bukkitPlayer.hasCooldown(material);
    }

    @Override
    public int getCooldown(final Material material)
    {
        return this.bukkitPlayer.getCooldown(material);
    }

    @Override
    public void setCooldown(final Material material, final int i)
    {
        this.bukkitPlayer.setCooldown(material, i);
    }

    @Override
    public boolean isSleeping()
    {
        return this.bukkitPlayer.isSleeping();
    }

    @Override
    public int getSleepTicks()
    {
        return this.bukkitPlayer.getSleepTicks();
    }

    @Override
    public GameMode getGameMode()
    {
        return this.bukkitPlayer.getGameMode();
    }

    @Override
    public void setGameMode(final GameMode gameMode)
    {
        this.bukkitPlayer.setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking()
    {
        return this.bukkitPlayer.isBlocking();
    }

    @Override
    public boolean isHandRaised()
    {
        return this.bukkitPlayer.isHandRaised();
    }

    @Override
    public int getExpToLevel()
    {
        return this.bukkitPlayer.getExpToLevel();
    }

    @Override
    public Entity releaseLeftShoulderEntity()
    {
        return this.bukkitPlayer.releaseLeftShoulderEntity();
    }

    @Override
    public Entity releaseRightShoulderEntity()
    {
        return this.bukkitPlayer.releaseRightShoulderEntity();
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityLeft()
    {
        return this.bukkitPlayer.getShoulderEntityLeft();
    }

    @Override
    @Deprecated
    public void setShoulderEntityLeft(final Entity entity)
    {
        this.bukkitPlayer.setShoulderEntityLeft(entity);
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityRight()
    {
        return this.bukkitPlayer.getShoulderEntityRight();
    }

    @Override
    @Deprecated
    public void setShoulderEntityRight(final Entity entity)
    {
        this.bukkitPlayer.setShoulderEntityRight(entity);
    }

    @Override
    public void openSign(final Sign sign)
    {
        this.bukkitPlayer.openSign(sign);
    }

    @Override
    public double getEyeHeight()
    {
        return this.bukkitPlayer.getEyeHeight();
    }

    @Override
    public double getEyeHeight(final boolean b)
    {
        return this.bukkitPlayer.getEyeHeight(b);
    }

    @Override
    public Location getEyeLocation()
    {
        return this.bukkitPlayer.getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(final Set<Material> set, final int i)
    {
        return this.bukkitPlayer.getLineOfSight(set, i);
    }

    @Override
    public Block getTargetBlock(final Set<Material> set, final int i)
    {
        return this.bukkitPlayer.getTargetBlock(set, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(final Set<Material> set, final int i)
    {
        return this.bukkitPlayer.getLastTwoTargetBlocks(set, i);
    }

    @Override
    public int getRemainingAir()
    {
        return this.bukkitPlayer.getRemainingAir();
    }

    @Override
    public void setRemainingAir(final int i)
    {
        this.bukkitPlayer.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir()
    {
        return this.bukkitPlayer.getMaximumAir();
    }

    @Override
    public void setMaximumAir(final int i)
    {
        this.bukkitPlayer.setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks()
    {
        return this.bukkitPlayer.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(final int i)
    {
        this.bukkitPlayer.setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage()
    {
        return this.bukkitPlayer.getLastDamage();
    }

    @Override
    public void setLastDamage(final double v)
    {
        this.bukkitPlayer.setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks()
    {
        return this.bukkitPlayer.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(final int i)
    {
        this.bukkitPlayer.setNoDamageTicks(i);
    }

    @Override
    public Player getKiller()
    {
        return this.bukkitPlayer.getKiller();
    }

    @Override
    public void setKiller(@Nullable final Player player)
    {
        this.bukkitPlayer.setKiller(player);
    }

    @Override
    public boolean addPotionEffect(final PotionEffect potionEffect)
    {
        return this.bukkitPlayer.addPotionEffect(potionEffect);
    }

    @Override
    public boolean addPotionEffect(final PotionEffect potionEffect, final boolean b)
    {
        return this.bukkitPlayer.addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(final Collection<PotionEffect> collection)
    {
        return this.bukkitPlayer.addPotionEffects(collection);
    }

    @Override
    public boolean hasPotionEffect(final PotionEffectType potionEffectType)
    {
        return this.bukkitPlayer.hasPotionEffect(potionEffectType);
    }

    @Override
    public PotionEffect getPotionEffect(final PotionEffectType potionEffectType)
    {
        return this.bukkitPlayer.getPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(final PotionEffectType potionEffectType)
    {
        this.bukkitPlayer.removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects()
    {
        return this.bukkitPlayer.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(final Entity entity)
    {
        return this.bukkitPlayer.hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway()
    {
        return this.bukkitPlayer.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(final boolean b)
    {
        this.bukkitPlayer.setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment()
    {
        return this.bukkitPlayer.getEquipment();
    }

    @Override
    public void setCanPickupItems(final boolean b)
    {
        this.bukkitPlayer.setCanPickupItems(b);
    }

    @Override
    public boolean getCanPickupItems()
    {
        return this.bukkitPlayer.getCanPickupItems();
    }

    @Override
    public boolean isLeashed()
    {
        return this.bukkitPlayer.isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException
    {
        return this.bukkitPlayer.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(final Entity entity)
    {
        return this.bukkitPlayer.setLeashHolder(entity);
    }

    @Override
    public boolean isGliding()
    {
        return this.bukkitPlayer.isGliding();
    }

    @Override
    public void setGliding(final boolean b)
    {
        this.bukkitPlayer.setGliding(b);
    }

    @Override
    public void setAI(final boolean b)
    {
        this.bukkitPlayer.setAI(b);
    }

    @Override
    public boolean hasAI()
    {
        return this.bukkitPlayer.hasAI();
    }

    @Override
    public void setCollidable(final boolean b)
    {
        this.bukkitPlayer.setCollidable(b);
    }

    @Override
    public boolean isCollidable()
    {
        return this.bukkitPlayer.isCollidable();
    }

    @Override
    public int getArrowsStuck()
    {
        return this.bukkitPlayer.getArrowsStuck();
    }

    @Override
    public void setArrowsStuck(final int i)
    {
        this.bukkitPlayer.setArrowsStuck(i);
    }

    @Override
    public AttributeInstance getAttribute(final Attribute attribute)
    {
        return this.bukkitPlayer.getAttribute(attribute);
    }

    @Override
    public Location getLocation()
    {
        return this.bukkitPlayer.getLocation();
    }

    @Override
    public Location getLocation(final Location location)
    {
        return this.bukkitPlayer.getLocation(location);
    }

    @Override
    public void setVelocity(final Vector vector)
    {
        this.bukkitPlayer.setVelocity(vector);
    }

    @Override
    public Vector getVelocity()
    {
        return this.bukkitPlayer.getVelocity();
    }

    @Override
    public double getHeight()
    {
        return this.bukkitPlayer.getHeight();
    }

    @Override
    public double getWidth()
    {
        return this.bukkitPlayer.getWidth();
    }

    @Override
    public boolean isOnGround()
    {
        return this.bukkitPlayer.isOnGround();
    }

    @Override
    public World getWorld()
    {
        return this.bukkitPlayer.getWorld();
    }

    @Override
    public boolean teleport(final Location location)
    {
        return this.bukkitPlayer.teleport(location);
    }

    @Override
    public boolean teleport(final Location location, final PlayerTeleportEvent.TeleportCause teleportCause)
    {
        return this.bukkitPlayer.teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(final Entity entity)
    {
        return this.bukkitPlayer.teleport(entity);
    }

    @Override
    public boolean teleport(final Entity entity, final PlayerTeleportEvent.TeleportCause teleportCause)
    {
        return this.bukkitPlayer.teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(final double v, final double v1, final double v2)
    {
        return this.bukkitPlayer.getNearbyEntities(v, v1, v2);
    }

    @Override
    public int getEntityId()
    {
        return this.bukkitPlayer.getEntityId();
    }

    @Override
    public int getFireTicks()
    {
        return this.bukkitPlayer.getFireTicks();
    }

    @Override
    public int getMaxFireTicks()
    {
        return this.bukkitPlayer.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(final int i)
    {
        this.bukkitPlayer.setFireTicks(i);
    }

    @Override
    public void remove()
    {
        this.bukkitPlayer.remove();
    }

    @Override
    public boolean isDead()
    {
        return this.bukkitPlayer.isDead();
    }

    @Override
    public boolean isValid()
    {
        return this.bukkitPlayer.isValid();
    }

    @Override
    public Server getServer()
    {
        return this.bukkitPlayer.getServer();
    }

    @Override
    @Deprecated
    public Entity getPassenger()
    {
        return this.bukkitPlayer.getPassenger();
    }

    @Override
    @Deprecated
    public boolean setPassenger(final Entity entity)
    {
        return this.bukkitPlayer.setPassenger(entity);
    }

    @Override
    public List<Entity> getPassengers()
    {
        return this.bukkitPlayer.getPassengers();
    }

    @Override
    public boolean addPassenger(final Entity entity)
    {
        return this.bukkitPlayer.addPassenger(entity);
    }

    @Override
    public boolean removePassenger(final Entity entity)
    {
        return this.bukkitPlayer.removePassenger(entity);
    }

    @Override
    public boolean isEmpty()
    {
        return this.bukkitPlayer.isEmpty();
    }

    @Override
    public boolean eject()
    {
        return this.bukkitPlayer.eject();
    }

    @Override
    public float getFallDistance()
    {
        return this.bukkitPlayer.getFallDistance();
    }

    @Override
    public void setFallDistance(final float v)
    {
        this.bukkitPlayer.setFallDistance(v);
    }

    @Override
    public void setLastDamageCause(final EntityDamageEvent entityDamageEvent)
    {
        this.bukkitPlayer.setLastDamageCause(entityDamageEvent);
    }

    @Override
    public EntityDamageEvent getLastDamageCause()
    {
        return this.bukkitPlayer.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId()
    {
        return this.bukkitPlayer.getUniqueId();
    }

    @Override
    public int getTicksLived()
    {
        return this.bukkitPlayer.getTicksLived();
    }

    @Override
    public void setTicksLived(final int i)
    {
        this.bukkitPlayer.setTicksLived(i);
    }

    @Override
    public void playEffect(final EntityEffect entityEffect)
    {
        this.bukkitPlayer.playEffect(entityEffect);
    }

    @Override
    public EntityType getType()
    {
        return this.bukkitPlayer.getType();
    }

    @Override
    public boolean isInsideVehicle()
    {
        return this.bukkitPlayer.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle()
    {
        return this.bukkitPlayer.leaveVehicle();
    }

    @Override
    public Entity getVehicle()
    {
        return this.bukkitPlayer.getVehicle();
    }

    @Override
    public void setCustomNameVisible(final boolean b)
    {
        this.bukkitPlayer.setCustomNameVisible(b);
    }

    @Override
    public boolean isCustomNameVisible()
    {
        return this.bukkitPlayer.isCustomNameVisible();
    }

    @Override
    public void setGlowing(final boolean b)
    {
        this.bukkitPlayer.setGlowing(b);
    }

    @Override
    public boolean isGlowing()
    {
        return this.bukkitPlayer.isGlowing();
    }

    @Override
    public void setInvulnerable(final boolean b)
    {
        this.bukkitPlayer.setInvulnerable(b);
    }

    @Override
    public boolean isInvulnerable()
    {
        return this.bukkitPlayer.isInvulnerable();
    }

    @Override
    public boolean isSilent()
    {
        return this.bukkitPlayer.isSilent();
    }

    @Override
    public void setSilent(final boolean b)
    {
        this.bukkitPlayer.setSilent(b);
    }

    @Override
    public boolean hasGravity()
    {
        return this.bukkitPlayer.hasGravity();
    }

    @Override
    public void setGravity(final boolean b)
    {
        this.bukkitPlayer.setGravity(b);
    }

    @Override
    public int getPortalCooldown()
    {
        return this.bukkitPlayer.getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(final int i)
    {
        this.bukkitPlayer.setPortalCooldown(i);
    }

    @Override
    public Set<String> getScoreboardTags()
    {
        return this.bukkitPlayer.getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(final String s)
    {
        return this.bukkitPlayer.addScoreboardTag(s);
    }

    @Override
    public boolean removeScoreboardTag(final String s)
    {
        return this.bukkitPlayer.removeScoreboardTag(s);
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction()
    {
        return this.bukkitPlayer.getPistonMoveReaction();
    }

    @Override
    public Location getOrigin()
    {
        return this.bukkitPlayer.getOrigin();
    }

    @Override
    public boolean fromMobSpawner()
    {
        return this.bukkitPlayer.fromMobSpawner();
    }

    @Override
    public void setMetadata(final String s, final MetadataValue metadataValue)
    {
        this.bukkitPlayer.setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(final String s)
    {
        return this.bukkitPlayer.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(final String s)
    {
        return this.bukkitPlayer.hasMetadata(s);
    }

    @Override
    public void removeMetadata(final String s, final Plugin plugin)
    {
        this.bukkitPlayer.removeMetadata(s, plugin);
    }

    @Override
    public void sendMessage(final String s)
    {
        this.sendMessage(s, MessageLayout.DEFAULT);
    }

    @Override
    public void sendMessage(final String[] strings)
    {
        this.bukkitPlayer.sendMessage(strings);
    }

    @Override
    public boolean isPermissionSet(final String s)
    {
        return this.bukkitPlayer.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(final Permission permission)
    {
        return this.bukkitPlayer.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(final String s)
    {
        return this.bukkitPlayer.hasPermission(s);
    }

    @Override
    public boolean hasPermission(final Permission permission)
    {
        return this.bukkitPlayer.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String s, final boolean b)
    {
        return this.bukkitPlayer.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin)
    {
        return this.bukkitPlayer.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String s, final boolean b, final int i)
    {
        return this.bukkitPlayer.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final int i)
    {
        return this.bukkitPlayer.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(final PermissionAttachment permissionAttachment)
    {
        this.bukkitPlayer.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions()
    {
        this.bukkitPlayer.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return this.bukkitPlayer.getEffectivePermissions();
    }

    @Override
    public boolean isOp()
    {
        return this.bukkitPlayer.isOp();
    }

    @Override
    public void setOp(final boolean b)
    {
        this.bukkitPlayer.setOp(b);
    }

    @Override
    public String getCustomName()
    {
        return this.bukkitPlayer.getCustomName();
    }

    @Override
    public void setCustomName(final String s)
    {
        this.bukkitPlayer.setCustomName(s);
    }

    @Override
    public void damage(final double v)
    {
        this.bukkitPlayer.damage(v);
    }

    @Override
    public void damage(final double v, final Entity entity)
    {
        this.bukkitPlayer.damage(v, entity);
    }

    @Override
    public double getHealth()
    {
        return this.bukkitPlayer.getHealth();
    }

    @Override
    public void setHealth(final double v)
    {
        this.bukkitPlayer.setHealth(v);
    }

    @Override
    @Deprecated
    public double getMaxHealth()
    {
        return this.bukkitPlayer.getMaxHealth();
    }

    @Override
    @Deprecated
    public void setMaxHealth(final double v)
    {
        this.bukkitPlayer.setMaxHealth(v);
    }

    @Override
    @Deprecated
    public void resetMaxHealth()
    {
        this.bukkitPlayer.resetMaxHealth();
    }

    @Override
    public <T extends Projectile> T launchProjectile(final Class<? extends T> aClass)
    {
        return this.bukkitPlayer.launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(final Class<? extends T> aClass, final Vector vector)
    {
        return this.bukkitPlayer.launchProjectile(aClass, vector);
    }

    @Override
    public boolean isConversing()
    {
        return this.bukkitPlayer.isConversing();
    }

    @Override
    public void acceptConversationInput(final String s)
    {
        this.bukkitPlayer.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(final Conversation conversation)
    {
        return this.bukkitPlayer.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(final Conversation conversation)
    {
        this.bukkitPlayer.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(final Conversation conversation, final ConversationAbandonedEvent conversationAbandonedEvent)
    {
        this.bukkitPlayer.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public boolean isOnline()
    {
        return this.bukkitPlayer.isOnline();
    }

    @Override
    public boolean isBanned()
    {
        return this.bukkitPlayer.isBanned();
    }

    @Override
    public boolean isWhitelisted()
    {
        return this.bukkitPlayer.isWhitelisted();
    }

    @Override
    public void setWhitelisted(final boolean b)
    {
        this.bukkitPlayer.setWhitelisted(b);
    }

    @Override
    public Player getPlayer()
    {
        return this.bukkitPlayer.getPlayer();
    }

    @Override
    public long getFirstPlayed()
    {
        return this.bukkitPlayer.getFirstPlayed();
    }

    @Override
    public long getLastPlayed()
    {
        return this.bukkitPlayer.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore()
    {
        return this.bukkitPlayer.hasPlayedBefore();
    }

    @Override
    public Map<String, Object> serialize()
    {
        return this.bukkitPlayer.serialize();
    }

    @Override
    public void sendPluginMessage(final Plugin plugin, final String s, final byte[] bytes)
    {
        this.bukkitPlayer.sendPluginMessage(plugin, s, bytes);
    }

    @Override
    public Set<String> getListeningPluginChannels()
    {
        return this.bukkitPlayer.getListeningPluginChannels();
    }

    @Override
    public void setVisible(final boolean b)
    {
        this.bukkitPlayer.setVisible(b);
    }

    @Override
    public int getProtocolVersion()
    {
        return this.bukkitPlayer.getProtocolVersion();
    }

    @Override
    @Nullable
    public InetSocketAddress getVirtualHost()
    {
        return this.bukkitPlayer.getVirtualHost();
    }

    @Override
    public int hashCode()
    {
        return this.bukkitPlayer.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof NorthPlayerImpl)
        {
            final NorthPlayerImpl otherNorthPlayer = (NorthPlayerImpl) obj;
            return this.bukkitPlayer.equals(otherNorthPlayer.bukkitPlayer);
        }
        return this.bukkitPlayer.equals(obj); // prawdopodobnie przyszla instancja CraftPlayer
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("bukkitPlayer", this.bukkitPlayer).toString();
    }
}

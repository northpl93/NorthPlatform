package pl.arieals.minigame.goldhunter;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import com.google.common.base.Preconditions;

import pl.arieals.minigame.goldhunter.utils.TimeStringUtils;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.bukkit.utils.SimpleSyncCallback;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.lang.CatchException;

public abstract class Effect implements ITickable
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    @Inject
    @Messages("GoldHunterEffects")
    private static MessagesBox messages;
    
    private EffectTracker tracker;
    
    private EffectBarColor barColor = EffectBarColor.BLUE;
    
    private int duration;
    private int startDuration;
    
    private SimpleSyncCallback callback;
    private BossBar bossbar;
    
    final void attach(EffectTracker tracker, int duration)
    {
        Preconditions.checkState(this.tracker == null);
        logger.debug("Attached effect {} with duration {} to {}", getClass().getSimpleName(), duration, tracker.getPlayer());
        
        this.tracker = tracker;
        setDuration(duration);
        
        this.callback = new SimpleSyncCallback();
        
        this.bossbar = Bukkit.createBossBar("§0", getBarColor().barColor, BarStyle.SOLID);
        bossbar.addPlayer(tracker.getPlayer().getPlayer());
        
        CatchException.printStackTrace(this::onStart);
    }
    
    final void detach()
    {
        Preconditions.checkState(this.tracker != null);
        logger.debug("Detached effect {} from {}", getClass().getSimpleName(), tracker.getPlayer());
        
        bossbar.removeAll();
        bossbar = null;
        
        callback.callComplete();
        callback = null;
        
        setDuration(-1);
        
        CatchException.printStackTrace(this::onEnd);
        
        tracker = null;
    }
    
    public final GoldHunterPlayer getPlayer()
    {
        return tracker.getPlayer();
    }
    
    public final int getDuration()
    {
        return duration;
    }
    
    public final void setDuration(int duration)
    {
        Preconditions.checkArgument(duration >= -1);
        
        this.duration = duration;
        
        if ( duration == -1 )
        {
            startDuration = -1;
        }
        if ( duration > startDuration )
        {
            startDuration = duration;
        }
    }
    
    public final boolean isInfinite()
    {
        return duration == -1;
    }
    
    public final EffectBarColor getBarColor()
    {
        return barColor;
    }
    
    protected final void setBarColor(EffectBarColor barColor)
    {
        Preconditions.checkNotNull(barColor);
        this.barColor = barColor;
    }
    
    final SimpleSyncCallback getCallback()
    {
        return callback;
    }
    
    final BossBar getBossbar()
    {
        return bossbar;
    }
    
    @Tick
    private void updateDuration()
    {
        if ( !isInfinite() )
        {
            duration--;
            if ( duration == 0 )
            {
                tracker.notifyEffectEnd(this);
            }
        }
    }
    
    @Tick
    private void updateBossbar()
    {
        if ( bossbar != null )
        {
            updateBarProgreess();
            updateBarTitle();
        }
    }
    
    private void updateBarProgreess()
    {
        if ( isInfinite() )
        {
            bossbar.setProgress(1);
        }
        else
        {
            bossbar.setProgress(duration / (float) startDuration);
        }
    }
    
    private void updateBarTitle()
    {
        EffectBarColor barColor = getBarColor();
        
        String time = !isInfinite() ? TimeStringUtils.minutesAndSeconds(duration / 20) : "";
        TranslatableString effectName = TranslatableString.of(messages, "@effect." + getClass().getSimpleName());
        
        String title = tracker.getPlayer().getMessage("effects.bartitle", barColor.nameColor + "§l", effectName, barColor.timeColor + "§l", time);
        bossbar.setTitle(title);
    }
    
    protected void onStart()
    {
    }
    
    protected void onEnd()
    {
    }
    
    public enum EffectBarColor
    {
        BLUE(BarColor.BLUE, ChatColor.AQUA, ChatColor.DARK_AQUA),
        RED(BarColor.RED, ChatColor.RED, ChatColor.DARK_RED),
        GREEN(BarColor.GREEN, ChatColor.GREEN, ChatColor.DARK_GREEN),
        YELLOW(BarColor.YELLOW, ChatColor.YELLOW, ChatColor.GOLD),
        WHITE(BarColor.WHITE, ChatColor.WHITE, ChatColor.GRAY),
        PURPLE(BarColor.PURPLE, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE),
        ;
        
        private final BarColor barColor;
        private final ChatColor nameColor;
        private final ChatColor timeColor;
        
        private EffectBarColor(BarColor barColor, ChatColor nameColor, ChatColor timeColor)
        {
            this.barColor = barColor;
            this.nameColor = nameColor;
            this.timeColor = timeColor;
        }
    }
}

package pl.north93.zgame.api.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.base.Preconditions;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.API;

public class Countdown
{
    private final BukkitApiCore apiCore = (BukkitApiCore) API.getApiCore();

    private int ticksLeft;
    private int ticks;
    
    private Runnable endCallback;
    private Runnable tickCallback;
    
    private BukkitTask task;
    
    public Countdown(int ticks)
    {
        this.ticksLeft = ticks;
        this.ticks = ticks;
    }
    
    public Countdown reset()
    {
        return reset(ticks);
    }
    
    public Countdown reset(int ticks)
    {
        boolean started = this.isStarted();
        
        if ( started )
        {
            this.stop();
        }
        
        this.ticksLeft = ticks;
        this.ticks = ticks;
        
        if ( started )
        {
            this.start();
        }
        
        return this;
    }
    
    public Countdown start()
    {
        Preconditions.checkState(this.task == null, "Countdown already started");
        Preconditions.checkState(this.ticksLeft > 0, "Ticks left is 0");
        
        this.task = Bukkit.getScheduler().runTaskTimer(this.apiCore.getPluginMain(), () -> this.tick(), 1, 1);
        return this;
    }
    
    public Countdown stop()
    {
        Preconditions.checkState(this.task != null, "Countdown already stopped");
        
        this.task.cancel();
        this.task = null;
        return this;
    }
    
    public Countdown tickCallback(Runnable tickCallback)
    {
        this.tickCallback = tickCallback;
        return this;
    }
    
    public Countdown endCallback(Runnable endCallback)
    {
        this.endCallback = endCallback;
        return this;
    }
    
    public boolean isStarted()
    {
        return this.task != null;
    }
    
    public double getProgress()
    {
        return (double) this.ticksLeft / this.ticks;
    }
    
    public int getTicks()
    {
        return this.ticks;
    }
    
    public int getTicksLeft()
    {
        return this.ticksLeft;
    }
    
    public int getSeconds()
    {
        return (int) Math.ceil(this.ticks);
    }
    
    public int getSecondsLeft()
    {
        return (int) Math.ceil(this.ticksLeft);
    }
    
    public String getTimeLeftString()
    {
        int sec = this.getSecondsLeft() % 60;
        int min = this.getSecondsLeft() / 60;
        
        StringBuilder result = new StringBuilder();
        result.append(min).append(':');
        if ( sec < 10 )
        {
            result.append('0');
        }
        
        result.append(sec);
        return result.toString();
    }
    
    private void tick()
    {
        if ( this.ticksLeft > 0 )
        {
            this.ticksLeft--;
            if ( this.tickCallback != null )
            {
                this.tickCallback.run();
            }
            
            if ( this.ticksLeft == 0 && this.endCallback != null )
            {   
                this.stop();
                this.endCallback.run();
            }
        }
    }
}
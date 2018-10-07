package pl.arieals.minigame.goldhunter.player;

import java.util.Set;

import net.minecraft.server.v1_12_R1.MinecraftServer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import org.slf4j.Logger;

import org.diorite.commons.math.DioriteMathUtils;

import pl.arieals.minigame.goldhunter.GoldHunterLogger;
import pl.arieals.minigame.goldhunter.arena.SoundEffect;
import pl.arieals.minigame.goldhunter.classes.SpecialAbilityType;
import pl.north93.zgame.api.bukkit.tick.ITickable;
import pl.north93.zgame.api.bukkit.tick.Tick;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class SpecialAbilityTracker implements ITickable
{
    @Inject
    @GoldHunterLogger
    private static Logger logger;
    
    private final GoldHunterPlayer player;
    
    private SpecialAbilityType currentAbilityType;
    
    private int loadingTicks = -1;
    private int loadingTicksLeft = -1;
    
    private boolean suspended;
    
    private boolean fastAbilityEnabled;
    
    public SpecialAbilityTracker(GoldHunterPlayer player)
    {
        this.player = player;
    }
    
    public GoldHunterPlayer getPlayer()
    {
        return player;
    }
    
    public SpecialAbilityType getCurrentAbilityType()
    {
        return currentAbilityType;
    }
    
    public void setFastAbilityEnabled(boolean fastAbilityEnabled)
    {
        this.fastAbilityEnabled = fastAbilityEnabled;
    }
    
    public boolean isFastAbilityEnabled()
    {
        return fastAbilityEnabled;
    }
    
    public void setNewAbilityType(SpecialAbilityType abilityType)
    {
        logger.debug("set {} ability to {}", player, abilityType);
        
        this.currentAbilityType = abilityType;
        resetAbilityLoading();
    }
    
    public boolean isSuspended()
    {
        return suspended;
    }
    
    public boolean isAbilityReady()
    {
        return !suspended && loadingTicksLeft == 0;
    }
    
    public void resetAbilityLoading()
    {
        logger.debug("reset {} ability ready", player);

        if ( currentAbilityType == null || player.getCurrentClass() == null )
        {
            loadingTicks = -1;
            loadingTicksLeft = -1;
        }
        else
        {
            loadingTicks = player.getCurrentClass().getAbilityLoadingTicks(player);
            loadingTicksLeft = loadingTicks;
        }
        
        suspended = false;
    }
    
    public void suspendAbilityLoading()
    {
        logger.debug("suspend {} ability", player);
        
        suspended = true;
    }
    
    public void useAbility()
    {
        if ( !isAbilityReady() )
        {
            return;
        }
        
        logger.debug("{} use special ability", player);
        
        Block targetBlock = player.getPlayer().getTargetBlock((Set<Material>) null, 5);
        boolean success = currentAbilityType.getHandler().onUse(player, targetBlock != null ? targetBlock.getLocation() : null);
        
        if ( success )
        {
            SoundEffect.ABILITY_USE.play(player);
            player.addReward("ability", player.getCurrentClass().getRewardsInfo().getSpecialAbilityReward());
        }
        
        if ( success && !suspended )
        {
            resetAbilityLoading();
        }
    }
    
    @Tick
    public void updateLoadingTicks()
    {
        if ( currentAbilityType == null || suspended )
        {
            return;
        }
        
        if ( fastAbilityEnabled && loadingTicksLeft > 0 )
        {
            loadingTicksLeft = 0;
            abilityReady();
        }
        
        if ( loadingTicksLeft > 0 )
        {
            loadingTicksLeft--;
            
            if ( loadingTicksLeft == 0 )
            {
                abilityReady();
            }
        }
    }
    
    private void abilityReady()
    {
        logger.debug("{} ability is ready now", player);
        
        SoundEffect.ABILITY_READY.play(player);
        currentAbilityType.getHandler().onReady(player);
    }
    
    @Tick
    private void showActionBarMessage()
    {
        if ( isAbilityReady() && currentAbilityType != null )
        {
            player.sendActionBar(currentAbilityType.getAbilityReadyMessage());
        }
    }
    
    @Tick
    public void updateProgressBar()
    {
        Player p = player.getPlayer();
        
        if ( currentAbilityType == null || suspended )
        {
            p.setExp(0.0f);
            p.setLevel(0);
            return;
        }
        
        float percent = 1 - loadingTicksLeft / (float) loadingTicks;
        int seconds = DioriteMathUtils.ceil(loadingTicksLeft / 20.0);
        
        p.setLevel(seconds);
        
        if ( percent == 1 )
        {
            p.setExp(MinecraftServer.currentTick % 10 < 5 ? 1 : 0);
        }
        else
        {
            p.setExp(percent);
        }
    }
}

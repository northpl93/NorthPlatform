package pl.arieals.globalshops.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import pl.arieals.globalshops.server.BuyResult;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.IPlayerExperienceService;
import pl.arieals.globalshops.server.event.ItemBuyEvent;
import pl.arieals.globalshops.server.event.ItemCheckBuyEvent;
import pl.arieals.globalshops.shared.GroupType;
import pl.arieals.globalshops.shared.Item;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

class PlayerExperienceServiceImpl implements IPlayerExperienceService
{
    @Inject
    private BukkitApiCore apiCore;
    @Inject
    private Logger logger;

    @Bean
    private PlayerExperienceServiceImpl()
    {
    }

    @Override
    public BuyResult checkCanBuy(final IPlayerContainer playerContainer, final Item item, final int level)
    {
        final ItemCheckBuyEvent event = new ItemCheckBuyEvent(playerContainer.getBukkitPlayer(), playerContainer, item, level);

        if ( level > item.getMaxLevel() )
        {
        	logger.log(Level.WARNING, "Player attempt to buy item {0} at level: {1} where max level is {2}", new Object[] { item, level, item.getMaxLevel() });
        	// XXX: should we do something with this?
        }
        
        if (playerContainer.hasMaxLevel(item))
        {
            event.setResult(BuyResult.MAX_LEVEL);
        }

        if ( !checkDependencies(playerContainer, item, level) )
        {
        	event.setResult(BuyResult.NOT_SATISFIED_DEPENDENCIES);
        }
        // TODO: money check

        return this.apiCore.callEvent(event).getResult();
    }

    private boolean checkDependencies(final IPlayerContainer playerContainer, final Item item, final int level)
    {
    	if ( !playerContainer.hasBoughtItemAtLevel(item, level - 1) )
    	{
    		return false;
    	}
    	
    	// TODO:
    	
    	return true;
    }
    
    @Override
    public void processClick(final IPlayerContainer playerContainer, final Item item)
    {
        if (item.getGroup().getGroupType() == GroupType.SINGLE_PICK)
        {
            if (playerContainer.hasBoughtItem(item))
            {
                this.markActive(playerContainer, item);
            }
            else
            {
                this.buyItem(playerContainer, item, 1);
            }
        }
        else
        {
            this.upgradeItem(playerContainer, item);
        }
    }

    @Override
    public void processClick(IPlayerContainer playerContainer, Item item, int level)
    {
    	Preconditions.checkState(item.getGroup().getGroupType() == GroupType.MULTI_BUY);
    	this.buyItem(playerContainer, item, level);
    }
    
    private void markActive(final IPlayerContainer playerContainer, final Item item)
    {
        playerContainer.markAsActive(item);

        final String playerName = playerContainer.getBukkitPlayer().getName();
        this.logger.log(Level.INFO, "Player {0} marked {1} as active in group {2}", new Object[]{playerName, item.getId(), item.getGroup().getId()});
    }

    private void upgradeItem(final IPlayerContainer playerContainer, final Item item)
    {
        int nextLevel = playerContainer.getBoughtItemLevel(item) + 1;
        buyItem(playerContainer, item, nextLevel);
    }
    
    private void buyItem(final IPlayerContainer playerContainer, final Item item, int level)
    {
    	Preconditions.checkArgument(level > 0);
    	Preconditions.checkState(item.getGroup().getGroupType() == GroupType.MULTI_BUY || level == 1);
    	
        final Player bukkitPlayer = playerContainer.getBukkitPlayer();

        final BuyResult buyResult = this.checkCanBuy(playerContainer, item, level);
        if (buyResult != BuyResult.CAN_BUY)
        {
            this.logger.log(Level.INFO, "Upgrade cancelled with reason {0} for player {1} item {2} level {3}", new Object[]{buyResult, bukkitPlayer.getName(), item.getId(), level});
            return;
        }

        ConfirmGui.openConfirmGui(playerContainer.getBukkitPlayer()).onComplete(() ->
        {
        	// sprawdzamy czy gracz może nadal kupić ten przedmiot ( mogło się coś w między czasie zmienić )
        	final BuyResult buyResult2 = this.checkCanBuy(playerContainer, item, level);
            if (buyResult2 != BuyResult.CAN_BUY)
            {
            	// TODO: send message
                this.logger.log(Level.INFO, "Upgrade cancelled with reason {0} for player {1} item {2} level {3}", new Object[]{buyResult2, bukkitPlayer.getName(), item.getId(), level});
                return;
            }
            
	        final ItemBuyEvent buyEvent = this.apiCore.callEvent(new ItemBuyEvent(bukkitPlayer, item, level));
	        if (buyEvent.isCancelled())
	        {
	            return;
	        }
	
	        final boolean success = playerContainer.addItem(item, level);
	        if (! success)
	        {
	            this.logger.log(Level.WARNING, "Player {0} failed to buy {1} level {2} (success false)", new Object[]{bukkitPlayer.getName(), item.getId(), level});
	            return;
	        }
	
	        // todo bierzemy kase
	        this.logger.log(Level.INFO, "Player {0} bought {1} level {2}", new Object[]{bukkitPlayer.getName(), item.getId(), level});
	        
	        if ( item.getGroup().getGroupType() == GroupType.SINGLE_PICK )
	        {
	        	markActive(playerContainer, item);
	        }
        });
    }
}

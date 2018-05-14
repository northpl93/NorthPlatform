package pl.arieals.globalshops.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.IPlayerExperienceService;
import pl.arieals.globalshops.server.domain.BuyResult;
import pl.arieals.globalshops.server.domain.IPrice;
import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.event.ItemBuyEvent;
import pl.arieals.globalshops.server.event.ItemCheckBuyEvent;
import pl.arieals.globalshops.shared.GroupType;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
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

        if ( ! this.checkDependencies(playerContainer, item, level) )
        {
        	event.setResult(BuyResult.NOT_SATISFIED_DEPENDENCIES);
        }

        final IPrice price = item.getPrice(level);
        if (! price.canBuy(playerContainer, item))
        {
            event.setResult(BuyResult.NO_MONEY);
        }

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
                this.processMarkActiveClick(playerContainer, item);
            }
            else
            {
                this.processBuyClick(playerContainer, item, 1);
            }
        }
        else
        {
            this.processUpgradeClick(playerContainer, item);
        }
    }

    @Override
    public void processClick(final IPlayerContainer playerContainer, final Item item, final int level)
    {
    	Preconditions.checkState(item.getGroup().getGroupType() == GroupType.MULTI_BUY);
    	this.processBuyClick(playerContainer, item, level);
    }

    // obsluguje sytuacje gdy gracz chce oznaczyc item jako aktywny
    private void processMarkActiveClick(final IPlayerContainer playerContainer, final Item item)
    {
        playerContainer.markAsActive(item);

        final String playerName = playerContainer.getBukkitPlayer().getName();
        this.logger.log(Level.INFO, "Player {0} marked {1} as active in group {2}", new Object[]{playerName, item.getId(), item.getGroup().getId()});
    }

    // obsluguje sytuacje gdy gracz moze ulepszac item
    private void processUpgradeClick(final IPlayerContainer playerContainer, final Item item)
    {
        final int nextLevel = playerContainer.getBoughtItemLevel(item) + 1;
        this.processBuyClick(playerContainer, item, nextLevel);
    }

    // obsługuje sytuację gdy gracz kupuje przedmiot
    private void processBuyClick(final IPlayerContainer playerContainer, final Item item, final int level)
    {
    	Preconditions.checkArgument(level > 0);
    	Preconditions.checkState(item.getGroup().getGroupType() == GroupType.MULTI_BUY || level == 1);
    	
        final INorthPlayer player = playerContainer.getBukkitPlayer();

        final BuyResult buyResult = this.checkCanBuy(playerContainer, item, level);
        if (buyResult != BuyResult.CAN_BUY)
        {
            this.logger.log(Level.INFO, "Upgrade cancelled with reason {0} for player {1} item {2} level {3}", new Object[]{buyResult, player.getName(), item.getId(), level});
            return;
        }

        ConfirmGui.openConfirmGui(playerContainer.getBukkitPlayer()).onComplete(() ->
        {
        	// sprawdzamy czy gracz może nadal kupić ten przedmiot ( mogło się coś w między czasie zmienić )
        	final BuyResult buyResult2 = this.checkCanBuy(playerContainer, item, level);
            if (buyResult2 != BuyResult.CAN_BUY)
            {
            	// TODO: send message
                this.logger.log(Level.INFO, "Upgrade cancelled with reason {0} for player {1} item {2} level {3}", new Object[]{buyResult2, player.getName(), item.getId(), level});
                return;
            }
            
	        final ItemBuyEvent buyEvent = this.apiCore.callEvent(new ItemBuyEvent(player, item, level));
	        if (buyEvent.isCancelled())
	        {
	            return;
	        }

            final IPrice price = item.getPrice(level);
            if (! price.processBuy(playerContainer, item))
            {
                // nie udalo sie pobrac kasy. Nie powinno byc mozliwe
                return;
            }
	
	        final boolean success = playerContainer.addItem(item, level);
	        if (! success)
	        {
	            this.logger.log(Level.WARNING, "Player {0} failed to buy {1} level {2} (success false)", new Object[]{player.getName(), item.getId(), level});
	            return;
	        }
	
	        this.logger.log(Level.INFO, "Player {0} bought {1} level {2}", new Object[]{player.getName(), item.getId(), level});
	        
	        if ( item.getGroup().getGroupType() == GroupType.SINGLE_PICK )
	        {
	            // po zakupie w grupie typu single pick od razu oznaczamy item jako kupiony
                this.processMarkActiveClick(playerContainer, item);
	        }
        });
    }
}

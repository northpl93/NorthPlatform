package pl.north93.northplatform.globalshops.server.impl;

import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.IPlayerExperienceService;
import pl.north93.northplatform.globalshops.server.domain.BuyResult;
import pl.north93.northplatform.globalshops.server.domain.IPrice;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.event.ItemBuyEvent;
import pl.north93.northplatform.globalshops.server.event.ItemCheckBuyEvent;
import pl.north93.northplatform.globalshops.shared.GroupType;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@Slf4j
class PlayerExperienceServiceImpl implements IPlayerExperienceService
{
    @Inject
    private BukkitApiCore apiCore;

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
        	log.warn("Player attempt to buy item {} at level: {} where max level is {}", item, level, item.getMaxLevel());
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
        log.info("Player {} marked {} as active in group {}", playerName, item.getId(), item.getGroup().getId());
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
            log.info("Upgrade cancelled with reason {} for player {} item {} level {}", buyResult, player.getName(), item.getId(), level);
            return;
        }

        ConfirmGui.openConfirmGui(playerContainer.getBukkitPlayer()).onComplete(() ->
        {
        	// sprawdzamy czy gracz może nadal kupić ten przedmiot ( mogło się coś w między czasie zmienić )
        	final BuyResult buyResult2 = this.checkCanBuy(playerContainer, item, level);
            if (buyResult2 != BuyResult.CAN_BUY)
            {
            	// TODO: send message
                log.info("Upgrade cancelled with reason {} for player {} item {} level {}", buyResult2, player.getName(), item.getId(), level);
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
	            log.warn("Player {} failed to buy {} level {} (success false)", player.getName(), item.getId(), level);
	            return;
	        }
	
	        log.info("Player {} bought {} level {}", player.getName(), item.getId(), level);
	        
	        if ( item.getGroup().getGroupType() == GroupType.SINGLE_PICK )
	        {
	            // po zakupie w grupie typu single pick od razu oznaczamy item jako kupiony
                this.processMarkActiveClick(playerContainer, item);
	        }
        });
    }
}

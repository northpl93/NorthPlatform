package pl.arieals.globalshops.server.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

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
    public BuyResult checkCanBuy(final IPlayerContainer playerContainer, final Item item)
    {
        final ItemCheckBuyEvent event = new ItemCheckBuyEvent(playerContainer.getBukkitPlayer(), playerContainer, item);

        if (playerContainer.hasMaxLevel(item))
        {
            event.setResult(BuyResult.MAX_LEVEL);
        }

        // todo money check

        return this.apiCore.callEvent(event).getResult();
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
                this.buyItem(playerContainer, item);
            }
        }
        else
        {
            // todo implement
        }
    }

    private void markActive(final IPlayerContainer playerContainer, final Item item)
    {
        playerContainer.markAsActive(item);

        final String playerName = playerContainer.getBukkitPlayer().getName();
        this.logger.log(Level.INFO, "Player {0} marked {1} as active in group {2}", new Object[]{playerName, item.getId(), item.getGroup().getId()});
    }

    private void buyItem(final IPlayerContainer playerContainer, final Item item)
    {
        final Player bukkitPlayer = playerContainer.getBukkitPlayer();
        final BuyResult buyResult = this.checkCanBuy(playerContainer, item);

        if (buyResult != BuyResult.CAN_BUY)
        {
            this.logger.log(Level.INFO, "Buy cancelled for player {0} item {1}", new Object[]{bukkitPlayer.getName(), item.getId()});
            return;
        }

        final ItemBuyEvent buyEvent = this.apiCore.callEvent(new ItemBuyEvent(bukkitPlayer, item));
        if (buyEvent.isCancelled())
        {
            return;
        }

        // todo bierzemy kase
        playerContainer.addItem(item);
        this.logger.log(Level.INFO, "Player {0} bought {1}", new Object[]{bukkitPlayer.getName(), item.getId()});

        // oznaczamy dany item jako aktywny
        this.markActive(playerContainer, item);
    }
}

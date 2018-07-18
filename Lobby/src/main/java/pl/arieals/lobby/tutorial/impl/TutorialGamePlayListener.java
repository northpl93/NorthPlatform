package pl.arieals.lobby.tutorial.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.arieals.lobby.tutorial.ITutorialManager;
import pl.arieals.lobby.tutorial.TutorialStatus;
import pl.arieals.lobby.tutorial.event.PlayerEnterTutorialEvent;
import pl.arieals.lobby.tutorial.event.PlayerExitTutorialEvent;
import pl.arieals.lobby.tutorial.event.TutorialStatusChangedEvent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.ITransaction;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class TutorialGamePlayListener implements AutoListener
{
    private final Logger logger = LoggerFactory.getLogger(TutorialGamePlayListener.class);
    @Inject @Messages("UserInterface")
    private MessagesBox      messages;
    @Inject
    private IEconomyManager  economyManager;
    @Inject
    private ITutorialManager tutorialManager;

    @EventHandler
    public void handleTutorialRewards(final TutorialStatusChangedEvent event)
    {
        if (event.getNewStatus() != TutorialStatus.PLAYED_COMPLETED)
        {
            return;
        }

        this.logger.info("Adding tutorial {} complete reward to {}", event.getTutorialId(), event.getIdentity());

        final ICurrency currency = this.economyManager.getCurrency("minigame");
        try (final ITransaction t = this.economyManager.openTransaction(currency, event.getIdentity()))
        {
            t.add(500);
        }
        catch (final Exception e)
        {
            this.logger.error("Failed to add reward for tutorial", e);
        }
    }

    @EventHandler
    public void handleTutorialEnter(final PlayerEnterTutorialEvent event)
    {
        final Player player = event.getPlayer();

        player.setVisible(false);
        new TutorialHotbar().display(player);

        this.logger.info("Player {} entered tutorial {}", player.getName(), event.getTutorialHub().getHubId());
    }

    @EventHandler
    public void handleTutorialExit(final PlayerExitTutorialEvent event)
    {
        final Player player = event.getPlayer();

        // hotbar powinien zostac ustawiony przez listener nowego huba
        player.setVisible(true);

        this.logger.info("Player {} exited tutorial {}", player.getName(), event.getTutorialHub().getHubId());
    }

    @EventHandler
    public void disableChatInTutorial(final AsyncPlayerChatEvent event)
    {
        if (this.tutorialManager.isInTutorial(event.getPlayer()))
        {
            final INorthPlayer player = INorthPlayer.wrap(event.getPlayer());
            player.sendMessage(this.messages, "tutorial.disallowed_in_tutorial");
            event.setCancelled(true);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

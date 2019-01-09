package pl.north93.northplatform.lobby.tutorial.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.lobby.tutorial.ITutorialManager;
import pl.north93.northplatform.lobby.tutorial.TutorialStatus;
import pl.north93.northplatform.lobby.tutorial.event.PlayerEnterTutorialEvent;
import pl.north93.northplatform.lobby.tutorial.event.PlayerExitTutorialEvent;
import pl.north93.northplatform.lobby.tutorial.event.TutorialStatusChangedEvent;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.economy.ICurrency;
import pl.north93.northplatform.api.economy.IEconomyManager;
import pl.north93.northplatform.api.economy.ITransaction;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

@Slf4j
public class TutorialGamePlayListener implements AutoListener
{
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

        log.info("Adding tutorial {} complete reward to {}", event.getTutorialId(), event.getIdentity());

        final ICurrency currency = this.economyManager.getCurrency("minigame");
        try (final ITransaction t = this.economyManager.openTransaction(currency, event.getIdentity()))
        {
            t.add(500);
        }
        catch (final Exception e)
        {
            log.error("Failed to add reward for completing tutorial", e);
        }
    }

    @EventHandler
    public void handleTutorialEnter(final PlayerEnterTutorialEvent event)
    {
        final Player player = event.getPlayer();

        player.setVisible(false);
        new TutorialHotbar().display(player);

        log.info("Player {} entered tutorial {}", player.getName(), event.getTutorialHub().getHubId());
    }

    @EventHandler
    public void handleTutorialExit(final PlayerExitTutorialEvent event)
    {
        final Player player = event.getPlayer();

        // hotbar powinien zostac ustawiony przez listener nowego huba
        player.setVisible(true);

        log.info("Player {} exited tutorial {}", player.getName(), event.getTutorialHub().getHubId());
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

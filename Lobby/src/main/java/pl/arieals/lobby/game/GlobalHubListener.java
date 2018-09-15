package pl.arieals.lobby.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.lobby.chest.opening.event.BeginChestOpeningEvent;
import pl.arieals.lobby.tutorial.ITutorialManager;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.economy.impl.server.event.PlayerCurrencyChangedEvent;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

/**
 * Listenery powiązane z systemem Hubów, ale nie z konkretnym Hubem.
 */
public class GlobalHubListener implements AutoListener
{
    @Inject @Messages("UserInterface")
    private MessagesBox        messages;
    @Inject
    private IScoreboardManager scoreboardManager;
    @Inject
    private ITutorialManager   tutorialManager;

    @EventHandler
    public void updateTabListHeaderFooterOnJoin(final PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        final BaseComponent header = this.messages.getComponent(player.getLocale(), "tablist.header");
        final BaseComponent footer = this.messages.getComponent(player.getLocale(), "tablist.footer");

        player.setPlayerListHeaderFooter(header, footer);
    }

    @EventHandler
    public void updateScoreboardOnCurrencyChange(final PlayerCurrencyChangedEvent event)
    {
        // scoreboardy zwykle mają w sobie zapisany stan waluty
        this.updateScoreboardContext(event.getPlayer());
    }

    @EventHandler
    public void updateScoreboardOnChestOpening(final BeginChestOpeningEvent event)
    {
        // scoreboardy zwykle mają w sobie zapisana ilosc skrzynek
        this.updateScoreboardContext(event.getPlayer());
    }

    private void updateScoreboardContext(final Player player)
    {
        final IScoreboardContext context = this.scoreboardManager.getContext(player);
        if (context == null)
        {
            // gracz nie zawsze musi mieć otwarty scoreboard
            return;
        }

        context.update();
    }

    @EventHandler
    public void enableFlyWhenHavePermissions(final PlayerSwitchedHubEvent event)
    {
        final Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.ADVENTURE)
        {
            // jak mamy inny gamemode niz adventure to pewnie jestesmy adminem
            // i nie chcemy zeby nam wylaczano fly
            return;
        }

        if (this.tutorialManager.isInTutorial(player))
        {
            // w tuturialach nie mozna latac
            player.setAllowFlight(false);
        }
        else
        {
            player.setAllowFlight(player.hasPermission("hub.fly"));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

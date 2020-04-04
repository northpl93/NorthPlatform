package pl.north93.northplatform.lobby.game;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.bukkit.player.event.PlayerPlatformLocaleChangedEvent;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardManager;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.economy.impl.server.event.PlayerCurrencyChangedEvent;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.north93.northplatform.lobby.chest.opening.event.BeginChestOpeningEvent;
import pl.north93.northplatform.lobby.tutorial.ITutorialManager;

/**
 * Listenery powiązane z systemem Hubów, ale nie z konkretnym Hubem.
 */
public class GlobalHubListener implements AutoListener
{
    @Inject @Messages("UserInterface")
    private MessagesBox messages;
    @Inject
    private IScoreboardManager scoreboardManager;
    @Inject
    private ITutorialManager tutorialManager;

    @EventHandler
    public void updateTabListHeaderFooterOnJoin(final PlayerJoinEvent event)
    {
        this.updatePlayerListHeaderFooter(event.getPlayer());
    }

    @EventHandler
    public void updateTabListHeaderFooterOnLanguageChange(final PlayerPlatformLocaleChangedEvent event)
    {
        this.updatePlayerListHeaderFooter(event.getPlayer());
    }

    private void updatePlayerListHeaderFooter(final Player player)
    {
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

    @EventHandler
    public void updateScoreboardOnLanguageChange(final PlayerPlatformLocaleChangedEvent event)
    {
        // todo this event is called asynchronously. Check does it work well.
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

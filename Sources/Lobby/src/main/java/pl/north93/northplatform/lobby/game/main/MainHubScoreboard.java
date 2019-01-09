package pl.north93.northplatform.lobby.game.main;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.lobby.game.HubScoreboardLayout;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class MainHubScoreboard extends HubScoreboardLayout
{
    @Inject @Messages("HubMain")
    private MessagesBox messages;

    @Override
    public String getTitle(final IScoreboardContext context)
    {
        return this.messages.getMessage(context.getLocale(), "scoreboard.title");
    }

    @Override
    public List<String> getContent(final IScoreboardContext context)
    {
        final ContentBuilder builder = IScoreboardLayout.builder();

        builder.box(this.messages).locale(context.getLocale());
        builder.add("");

        builder.translated("scoreboard.money", this.getPlayerCurrency(context.getPlayer()));
        builder.add("");

        builder.translated("scoreboard.multipler", this.getPlayerBooster(context.getPlayer()));
        builder.add("");

        builder.translated("scoreboard.group", this.getGroupName(context.getPlayer()));
        builder.add("");

        builder.translated("scoreboard.www");
        builder.add("");

        builder.translated("scoreboard.teamspeak");
        builder.add("");

        return builder.getContent();
    }

    private String getGroupName(final INorthPlayer player)
    {
        final String groupName = player.getGroup().getName();
        return this.messages.getLegacyMessage(player.getLocale(), "scoreboard.group." + groupName);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

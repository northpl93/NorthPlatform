package pl.arieals.lobby.game.main;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.lobby.game.HubScoreboardLayout;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

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

        builder.translated("scoreboard.multipler", "todo");
        builder.add("");

        builder.translated("scoreboard.group", "todo");
        builder.add("");

        builder.translated("scoreboard.www");
        builder.add("");

        builder.translated("scoreboard.teamspeak");
        builder.add("");

        return builder.getContent();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

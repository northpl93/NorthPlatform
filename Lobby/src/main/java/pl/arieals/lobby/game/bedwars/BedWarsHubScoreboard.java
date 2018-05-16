package pl.arieals.lobby.game.bedwars;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.arieals.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.arieals.lobby.game.HubScoreboardLayout;
import pl.north93.zgame.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.zgame.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class BedWarsHubScoreboard extends HubScoreboardLayout
{
    @Inject @Messages("HubBedWars")
    private MessagesBox messages;
    @Inject
    private IStatisticsManager statisticsManager;

    @Override
    public void initContext(final IScoreboardContext context)
    {
        final IStatisticHolder playerHolder = this.statisticsManager.getPlayerHolder(context.getPlayer().getUniqueId());

        final HigherNumberBetterStatistic killsStat = new HigherNumberBetterStatistic("bedwars/kills");
        context.setCompletableFuture("kills", playerHolder.getBest(killsStat));

        final HigherNumberBetterStatistic winsStat = new HigherNumberBetterStatistic("bedwars/wins");
        context.setCompletableFuture("wins", playerHolder.getBest(winsStat));
    }

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

        builder.translated("scoreboard.kills",  this.parseNumber(context.getCompletableFuture("kills")));
        builder.add("");

        builder.translated("scoreboard.wins", this.parseNumber(context.getCompletableFuture("wins")));
        builder.add("");

        builder.translated("scoreboard.money", this.getPlayerCurrency(context.getPlayer()));
        builder.add("");

        builder.translated("scoreboard.ip");

        return builder.getContent();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
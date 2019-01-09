package pl.north93.northplatform.lobby.game.goldhunter;

import java.util.List;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticHolder;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.north93.northplatform.lobby.game.HubScoreboardLayout;
import pl.north93.northplatform.api.bukkit.scoreboard.ContentBuilder;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardContext;
import pl.north93.northplatform.api.bukkit.scoreboard.IScoreboardLayout;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;

public class GoldHunterHubScoreboard extends HubScoreboardLayout
{
    @Inject @Messages("HubGoldHunter")
    private MessagesBox        messages;
    @Inject
    private IStatisticsManager statisticsManager;

    @Override
    public void initContext(final IScoreboardContext context)
    {
        final IStatisticHolder playerHolder = this.statisticsManager.getPlayerHolder(context.getPlayer().getUniqueId());

        final HigherNumberBetterStatistic killsStat = new HigherNumberBetterStatistic("goldhunter/kills");
        context.setCompletableFuture("kills", playerHolder.getBest(killsStat));

        final HigherNumberBetterStatistic winsStat = new HigherNumberBetterStatistic("goldhunter/wins");
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
}

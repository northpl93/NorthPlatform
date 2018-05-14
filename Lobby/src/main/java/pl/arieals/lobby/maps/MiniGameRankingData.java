package pl.arieals.lobby.maps;

import static java.text.MessageFormat.format;


import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IRanking;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.zgame.api.bukkit.map.loader.xml.RankingMapConfig;
import pl.north93.zgame.api.bukkit.map.renderer.ranking.IRankingRenderer;
import pl.north93.zgame.api.bukkit.map.renderer.ranking.RankingEntry;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;

public class MiniGameRankingData implements RankingMapConfig.IMapRankingData
{
    @Inject
    private static IStatisticsManager statisticsManager;
    @Inject
    private static INetworkManager    networkManager;

    private final IStatistic<?> leftStatistic;
    private final IStatistic<?> rightStatistic;

    public MiniGameRankingData(final IStatistic<?> leftStatistic, final IStatistic<?> rightStatistic)
    {
        this.leftStatistic = leftStatistic;
        this.rightStatistic = rightStatistic;
    }

    @Override
    public void setUp(final IRankingRenderer rankingRenderer)
    {
        final IRanking leftRanking = this.getRanking(this.leftStatistic);
        for (int i = 0; i < leftRanking.fetchedSize(); i++)
        {
            final IRecord place = leftRanking.getPlace(i);

            final UUID playerId = place.getHolder().getIdentity().getUuid();
            final String nick = networkManager.getPlayers().getNickFromUuid(playerId).orElse("");

            rankingRenderer.setLeftPlace(i, new RankingEntry(playerId, nick, place.getValue().getValue().toString()));
        }

        final IRanking rightRanking = this.getRanking(this.rightStatistic);
        for (int i = 0; i < rightRanking.fetchedSize(); i++)
        {
            final IRecord place = rightRanking.getPlace(i);

            final UUID playerId = place.getHolder().getIdentity().getUuid();
            final String nick = networkManager.getPlayers().getNickFromUuid(playerId).orElse("");

            rankingRenderer.setRightPlace(i, new RankingEntry(playerId, nick, place.getValue().getValue().toString()));
        }
    }

    private IRanking getRanking(final IStatistic<?> statistic)
    {
        try
        {
            return statisticsManager.getRanking(statistic, 10).get();
        }
        catch (final InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(format("Failed to get ranking for {0}", statistic.getId()));
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("leftStatistic", this.leftStatistic).append("rightStatistic", this.rightStatistic).toString();
    }
}

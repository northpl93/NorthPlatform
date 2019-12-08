package pl.north93.northplatform.lobby.maps;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import pl.north93.northplatform.api.bukkit.map.loader.xml.RankingMapConfig;
import pl.north93.northplatform.api.bukkit.map.renderer.ranking.IRankingRenderer;
import pl.north93.northplatform.api.bukkit.map.renderer.ranking.RankingEntry;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.INetworkManager;
import pl.north93.northplatform.api.minigame.shared.api.statistics.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.text.MessageFormat.format;

@AllArgsConstructor
public class MiniGameRankingData<L, L_UNIT extends IStatisticUnit<L>, R, R_UNIT extends IStatisticUnit<R>> implements RankingMapConfig.IMapRankingData
{
    @Inject
    private static IStatisticsManager statisticsManager;
    @Inject
    private static INetworkManager    networkManager;

    private final IStatistic<L, L_UNIT> leftStatistic;
    private final IStatistic<R, R_UNIT> rightStatistic;

    @Override
    public void setUp(final IRankingRenderer rankingRenderer)
    {
        final IRanking<L, L_UNIT> leftRanking = this.getRanking(this.leftStatistic);
        for (int i = 0; i < leftRanking.fetchedSize(); i++)
        {
            final IRecord<L, L_UNIT> place = leftRanking.getPlace(i);

            final UUID playerId = place.getHolder().getIdentity().getUuid();
            final String nick = networkManager.getPlayers().getNickFromUuid(playerId).orElse("");

            rankingRenderer.setLeftPlace(i, new RankingEntry(playerId, nick, place.getValue().getValue().toString()));
        }

        final IRanking<R, R_UNIT> rightRanking = this.getRanking(this.rightStatistic);
        for (int i = 0; i < rightRanking.fetchedSize(); i++)
        {
            final IRecord<R, R_UNIT> place = rightRanking.getPlace(i);

            final UUID playerId = place.getHolder().getIdentity().getUuid();
            final String nick = networkManager.getPlayers().getNickFromUuid(playerId).orElse("");

            rankingRenderer.setRightPlace(i, new RankingEntry(playerId, nick, place.getValue().getValue().toString()));
        }
    }

    private <T, UNIT extends IStatisticUnit<T>> IRanking<T, UNIT> getRanking(final IStatistic<T, UNIT> statistic)
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

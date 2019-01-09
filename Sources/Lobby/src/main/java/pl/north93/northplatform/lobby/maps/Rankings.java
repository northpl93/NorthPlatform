package pl.north93.northplatform.lobby.maps;

import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.north93.northplatform.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.north93.northplatform.api.bukkit.map.loader.xml.RankingMapConfig;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;

public final class Rankings
{
    @UriHandler("/lobby/ranking/bedwars")
    public static RankingMapConfig.IMapRankingData bedWarsRanking(final UriInvocationContext context)
    {
        final IStatistic<NumberUnit> winsStat = new HigherNumberBetterStatistic("bedwars/wins");
        final IStatistic<NumberUnit> killsStat = new HigherNumberBetterStatistic("bedwars/kills");

        return new MiniGameRankingData(winsStat, killsStat);
    }

    @UriHandler("/lobby/ranking/elytra")
    public static RankingMapConfig.IMapRankingData elytraRanking(final UriInvocationContext context)
    {
        final IStatistic<NumberUnit> winsStat = new HigherNumberBetterStatistic("elytra/totalWins");
        final IStatistic<NumberUnit> pointsStat = new HigherNumberBetterStatistic("elytra/totalScorePoints");

        return new MiniGameRankingData(winsStat, pointsStat);
    }

    @UriHandler("/lobby/ranking/goldhunter")
    public static RankingMapConfig.IMapRankingData goldHunterRanking(final UriInvocationContext context)
    {
        final IStatistic<NumberUnit> winsStat = new HigherNumberBetterStatistic("goldhunter/wins");
        final IStatistic<NumberUnit> killsStat = new HigherNumberBetterStatistic("goldhunter/kills"); // todo

        return new MiniGameRankingData(winsStat, killsStat);
    }
}

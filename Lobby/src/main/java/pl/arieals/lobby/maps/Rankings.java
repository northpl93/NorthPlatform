package pl.arieals.lobby.maps;

import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;
import pl.arieals.api.minigame.shared.api.statistics.unit.NumberUnit;
import pl.north93.zgame.api.bukkit.map.loader.xml.RankingMapConfig;
import pl.north93.zgame.api.global.uri.UriHandler;
import pl.north93.zgame.api.global.uri.UriInvocationContext;

public final class Rankings
{
    @UriHandler("/lobby/ranking/bedwars")
    public static RankingMapConfig.IMapRankingData bedWarsRanking(final UriInvocationContext context)
    {
        final IStatistic<NumberUnit> winsStat = new HigherNumberBetterStatistic("bedwars/wins");
        final IStatistic<NumberUnit> killsStat = new HigherNumberBetterStatistic("bedwars/kills");

        return new MiniGameRankingData(winsStat, killsStat);
    }

    @UriHandler("/lobby/ranking/goldhunter")
    public static RankingMapConfig.IMapRankingData goldHunterRanking(final UriInvocationContext context)
    {
        final IStatistic<NumberUnit> winsStat = new HigherNumberBetterStatistic("bedwars/wins"); // todo
        final IStatistic<NumberUnit> killsStat = new HigherNumberBetterStatistic("bedwars/kills"); // todo

        return new MiniGameRankingData(winsStat, killsStat);
    }
}

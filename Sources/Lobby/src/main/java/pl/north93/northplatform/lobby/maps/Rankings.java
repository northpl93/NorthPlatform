package pl.north93.northplatform.lobby.maps;

import pl.north93.northplatform.api.bukkit.map.loader.xml.RankingMapConfig;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;
import pl.north93.northplatform.api.minigame.shared.api.statistics.type.HigherNumberBetterStatistic;

public final class Rankings
{
    @UriHandler("/lobby/ranking/bedwars")
    public static RankingMapConfig.IMapRankingData bedWarsRanking(final UriInvocationContext context)
    {
        final var winsStat = new HigherNumberBetterStatistic("bedwars/wins");
        final var killsStat = new HigherNumberBetterStatistic("bedwars/kills");

        return new MiniGameRankingData<>(winsStat, killsStat);
    }

    @UriHandler("/lobby/ranking/elytra")
    public static RankingMapConfig.IMapRankingData elytraRanking(final UriInvocationContext context)
    {
        final var winsStat = new HigherNumberBetterStatistic("elytra/totalWins");
        final var pointsStat = new HigherNumberBetterStatistic("elytra/totalScorePoints");

        return new MiniGameRankingData<>(winsStat, pointsStat);
    }

    @UriHandler("/lobby/ranking/goldhunter")
    public static RankingMapConfig.IMapRankingData goldHunterRanking(final UriInvocationContext context)
    {
        final var winsStat = new HigherNumberBetterStatistic("goldhunter/wins");
        final var killsStat = new HigherNumberBetterStatistic("goldhunter/kills"); // todo

        return new MiniGameRankingData<>(winsStat, killsStat);
    }
}

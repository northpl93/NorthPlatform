package pl.arieals.api.minigame.shared.api.statistics;

public interface IStatisticsManager
{
    /**
     * Tworzy nowy obiekt reprezentujący statystykę.
     */
    <E extends IStatisticEncoder> IStatistic<E> getStatistic(Class<E> encoder, String key, boolean reversed);
}

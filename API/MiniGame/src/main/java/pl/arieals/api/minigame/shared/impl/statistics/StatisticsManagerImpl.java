package pl.arieals.api.minigame.shared.impl.statistics;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IStatistic;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticEncoder;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticsManager;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.data.StorageConnector;

public class StatisticsManagerImpl implements IStatisticsManager
{
    @Inject
    private ApiCore          apiCore;
    @Inject
    private StorageConnector storage;

    @Bean
    private StatisticsManagerImpl()
    {
    }

    @Override
    public <E extends IStatisticEncoder> IStatistic<E> getStatistic(final Class<E> encoder, final String key, final boolean isReversed)
    {
        return new StatisticImpl<>(this, key, isReversed);
    }

    /*default*/ ApiCore getApiCore()
    {
        return this.apiCore;
    }

    /*default*/ StorageConnector getStorage()
    {
        return this.storage;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

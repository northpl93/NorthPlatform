package pl.arieals.api.minigame.shared.impl.statistics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.statistics.IRanking;
import pl.arieals.api.minigame.shared.api.statistics.IRecord;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;

class RankingImpl<UNIT extends IStatisticUnit> implements IRanking<UNIT>
{
    private final int                 size;
    private final List<IRecord<UNIT>> results;

    public RankingImpl(final int size, final List<IRecord<UNIT>> results)
    {
        this.size = size;
        this.results = results;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public int fetchedSize()
    {
        return this.results.size();
    }

    @Override
    public Collection<IRecord<UNIT>> getPlaces()
    {
        return Collections.unmodifiableCollection(this.results);
    }

    @Override
    public IRecord<UNIT> getPlace(final int place)
    {
        return this.results.get(place);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("size", this.size).append("results", this.results).toString();
    }
}

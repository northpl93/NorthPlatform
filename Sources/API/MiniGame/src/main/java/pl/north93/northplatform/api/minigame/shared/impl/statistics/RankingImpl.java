package pl.north93.northplatform.api.minigame.shared.impl.statistics;

import lombok.AllArgsConstructor;
import lombok.ToString;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRanking;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IRecord;
import pl.north93.northplatform.api.minigame.shared.api.statistics.IStatisticUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@ToString
@AllArgsConstructor
class RankingImpl<T, UNIT extends IStatisticUnit<T>> implements IRanking<T, UNIT>
{
    private final int size;
    private final List<IRecord<T, UNIT>> results;

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
    public Collection<IRecord<T, UNIT>> getPlaces()
    {
        return Collections.unmodifiableCollection(this.results);
    }

    @Override
    public IRecord<T, UNIT> getPlace(final int place)
    {
        return this.results.get(place);
    }
}

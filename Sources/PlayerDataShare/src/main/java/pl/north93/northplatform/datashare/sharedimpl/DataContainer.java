package pl.north93.northplatform.datashare.sharedimpl;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.component.annotations.SkipInjections;
import pl.north93.northplatform.datashare.api.data.IDataUnit;

@SkipInjections
public final class DataContainer
{
    private UUID                   playerId;
    private Map<String, IDataUnit> data;

    public DataContainer()
    {
    }

    public DataContainer(final UUID playerId, final Map<String, IDataUnit> data)
    {
        this.playerId = playerId;
        this.data = data;
    }

    public UUID getPlayerId()
    {
        return this.playerId;
    }

    public Map<String, IDataUnit> getData()
    {
        return this.data;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("playerId", this.playerId).append("data", this.data).toString();
    }
}

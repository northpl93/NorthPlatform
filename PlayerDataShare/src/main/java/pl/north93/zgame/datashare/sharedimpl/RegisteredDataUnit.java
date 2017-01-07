package pl.north93.zgame.datashare.sharedimpl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.datashare.api.data.IDataUnit;
import pl.north93.zgame.datashare.api.data.IDataUnitPersistence;
import pl.north93.zgame.datashare.api.data.IDataUnitSerialization;

public class RegisteredDataUnit
{
    private final String                            name;
    private final IDataUnitSerialization<IDataUnit> serialization;
    private final IDataUnitPersistence<IDataUnit>   persistence;

    public RegisteredDataUnit(final String name, final IDataUnitSerialization<IDataUnit> serialization, final IDataUnitPersistence<IDataUnit> persistence)
    {
        this.name = name;
        this.serialization = serialization;
        this.persistence = persistence;
    }

    public String getName()
    {
        return this.name;
    }

    public IDataUnitSerialization<IDataUnit> getSerialization()
    {
        return this.serialization;
    }

    public IDataUnitPersistence<IDataUnit> getPersistence()
    {
        return this.persistence;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("serialization", this.serialization).append("persistence", this.persistence).toString();
    }
}

package pl.arieals.api.minigame.shared.api.statistics.unit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;

public class NumberUnit implements IStatisticUnit<Long>
{
    private Long value;

    public NumberUnit(final Long value)
    {
        this.value = value;
    }

    @Override
    public Long getValue()
    {
        return this.value;
    }

    @Override
    public void setValue(final Long newValue)
    {
        this.value = newValue;
    }

    @Override
    public void toDocument(final Document document)
    {
        document.put("value", this.value);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("value", this.value).toString();
    }
}

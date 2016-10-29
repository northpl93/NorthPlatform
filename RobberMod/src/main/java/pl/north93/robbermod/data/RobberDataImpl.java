package pl.north93.robbermod.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RobberDataImpl implements IRobberData
{
    private int robberCount;

    @Override
    public int getRobberCount()
    {
        return this.robberCount;
    }

    @Override
    public void setRobberCount(final int number)
    {
        this.robberCount = number;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("robberCount", this.robberCount).toString();
    }
}

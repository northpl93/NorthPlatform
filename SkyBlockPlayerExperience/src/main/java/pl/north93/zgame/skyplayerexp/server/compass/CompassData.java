package pl.north93.zgame.skyplayerexp.server.compass;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class CompassData
{
    private boolean isEnabled;
    private boolean isShow;
    private int     currentSlot;

    public boolean isEnabled()
    {
        return this.isEnabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.isEnabled = enabled;
    }

    public boolean isShow()
    {
        return this.isShow;
    }

    public void setShow(final boolean show)
    {
        this.isShow = show;
    }

    public int getCurrentSlot()
    {
        return this.currentSlot;
    }

    public void setCurrentSlot(final int currentSlot)
    {
        this.currentSlot = currentSlot;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("isEnabled", this.isEnabled).append("isShow", this.isShow).append("currentSlot", this.currentSlot).toString();
    }
}

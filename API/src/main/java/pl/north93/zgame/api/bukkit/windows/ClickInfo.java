package pl.north93.zgame.api.bukkit.windows;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class ClickInfo
{
    private final Window  window;
    private final int     slotId;
    private final boolean isRightClick;

    public ClickInfo(final Window window, final int slotId, final boolean isRightClick)
    {
        this.window = window;
        this.slotId = slotId;
        this.isRightClick = isRightClick;
    }

    public Window getWindow()
    {
        return this.window;
    }

    public int getSlotId()
    {
        return this.slotId;
    }

    public boolean isLeftClick()
    {
        return !this.isRightClick;
    }

    public boolean isRightClick()
    {
        return this.isRightClick;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("window", this.window).append("slotId", this.slotId).append("isRightClick", this.isRightClick).toString();
    }
}

package pl.north93.zgame.skyplayerexp.bungee.tablist;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StaticProvider implements ICellProvider
{
    private String text;

    public StaticProvider(final String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(final String text)
    {
        this.text = text;
    }

    @Override
    public String process(final TablistDrawingContext ctx)
    {
        return this.text;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("text", this.text).toString();
    }
}

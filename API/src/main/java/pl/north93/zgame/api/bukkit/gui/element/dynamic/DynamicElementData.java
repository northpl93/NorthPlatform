package pl.north93.zgame.api.bukkit.gui.element.dynamic;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.gui.impl.click.IClickHandler;
import pl.north93.zgame.api.global.utils.Vars;

public class DynamicElementData
{
    private final Vars<Object>  vars;
    private final IClickHandler clickHandler;

    public DynamicElementData(final Vars<Object> vars, final IClickHandler clickHandler)
    {
        this.vars = vars;
        this.clickHandler = clickHandler;
    }

    public Vars<Object> getVars()
    {
        return this.vars;
    }

    public IClickHandler getClickHandler()
    {
        return this.clickHandler;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("vars", this.vars).append("clickHandler", this.clickHandler).toString();
    }
}

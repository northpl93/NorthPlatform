package pl.north93.northplatform.api.bukkit.gui.element.dynamic;

import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.base.Preconditions;

import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.bukkit.gui.IGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.event.ClickEvent;
import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickHandler;

public class DynamicElementData
{
    private final String        iconCase;
    private final Vars<Object>  vars;
    private final IClickHandler clickHandler;
    private final IGuiIcon      icon;

    private DynamicElementData(final String iconCase, final Vars<Object> vars, final IClickHandler clickHandler, final IGuiIcon icon)
    {
        this.iconCase = iconCase;
        this.vars = vars;
        this.clickHandler = clickHandler;
        this.icon = icon;
    }
    
    public String getIconCase()
    {
        return iconCase;
    }

    public Vars<Object> getVars()
    {
        return this.vars;
    }

    public IClickHandler getClickHandler()
    {
        return this.clickHandler;
    }
    
    public IGuiIcon getIcon()
    {
        return icon;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("iconCase", this.iconCase)
                .append("vars", this.vars).append("clickHandler", this.clickHandler).append("icon", icon).toString();
    }
    
    public static DynamicElementDataBuilder builder()
    {
        return new DynamicElementDataBuilder();
    }
    
    public static class DynamicElementDataBuilder
    {
        private String iconCase = "";
        private Vars<Object> vars = Vars.empty();
        private IClickHandler clickHandler;
        private IGuiIcon icon;
        
        private DynamicElementDataBuilder()
        {
        }
        
        public DynamicElementDataBuilder icon(IGuiIcon icon)
        {
            Preconditions.checkArgument(icon != null);
            this.icon = icon;
            return this;
        }
        
        public DynamicElementDataBuilder iconCase(String iconCase)
        {
            Preconditions.checkArgument(iconCase != null);
            this.iconCase = iconCase;
            return this;
        }
        
        public DynamicElementDataBuilder vars(Vars<Object> vars)
        {
            Preconditions.checkArgument(vars != null);
            this.vars = vars;
            return this;
        }
        
        public DynamicElementDataBuilder clickHandler(IClickHandler clickHandler)
        {
            Preconditions.checkArgument(clickHandler != null);
            this.clickHandler = clickHandler;
            return this;
        }
        
        public DynamicElementDataBuilder clickHandler(Consumer<ClickEvent> clickHandler)
        {
            Preconditions.checkArgument(clickHandler != null);
            this.clickHandler = (source, event) -> clickHandler.accept(event);
            return this;
        }
        
        public DynamicElementData build()
        {
            return new DynamicElementData(iconCase, vars, clickHandler, icon);
        }
    }
}

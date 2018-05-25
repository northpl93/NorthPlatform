package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.gui.impl.XmlReaderContext;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XmlConditionalGuiElement extends XmlGuiElement
{
    @XmlAttribute(required = true, name = "if")
    private String ifVar;
    @XmlAttribute
    private boolean negated = false;

    protected final boolean shouldShow(final XmlReaderContext context)
    {
        if (this.ifVar == null)
        {
            return true;
        }

        boolean evaluatedVariable = this.parseVariable(context.getVars().getValue(this.ifVar));
        if (this.negated)
        {
            evaluatedVariable = !evaluatedVariable;
        }
        return evaluatedVariable;
    }

    private boolean parseVariable(final Object object)
    {
        if (object instanceof Boolean)
        {
            return ((Boolean) object);
        }
        return Boolean.valueOf(object.toString());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("ifVar", this.ifVar).append("negated", this.negated).toString();
    }
}

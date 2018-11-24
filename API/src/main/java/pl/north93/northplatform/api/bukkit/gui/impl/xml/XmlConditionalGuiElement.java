package pl.north93.northplatform.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XmlConditionalGuiElement extends XmlGuiElement
{
    @XmlAttribute(required = true, name = "if")
    private String ifVar;
    @XmlAttribute
    private boolean negated = false;

    public String getIfVar()
    {
        return ifVar;
    }
    
    public boolean isNegated()
    {
        return negated;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("ifVar", this.ifVar).append("negated", this.negated).toString();
    }
}

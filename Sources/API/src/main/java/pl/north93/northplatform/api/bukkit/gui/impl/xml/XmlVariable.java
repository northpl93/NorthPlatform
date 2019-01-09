package pl.north93.northplatform.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.bukkit.gui.impl.NorthUriUtils;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlVariable
{
    @XmlAttribute
    private String name;
    @XmlAttribute(required = true)
    private String value;

    @SuppressWarnings("unchecked")
    public Vars<Object> process(final MessagesBox messages, final Vars<Object> vars)
    {
        if (this.value.startsWith("northplatform:"))
        {
            if (this.name == null)
            {
                return (Vars) NorthUriUtils.getInstance().call(this.value, vars);
            }
            else
            {
                return Vars.of(this.name, NorthUriUtils.getInstance().call(this.value, vars));
            }
        }

        if (this.name == null)
        {
            throw new IllegalStateException("Variable must have name!");
        }

        if (this.value.startsWith("@"))
        {
            return Vars.of(this.name, TranslatableString.of(messages, this.value));
        }
        return Vars.of(this.name, this.value);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("value", this.value).toString();
    }
}

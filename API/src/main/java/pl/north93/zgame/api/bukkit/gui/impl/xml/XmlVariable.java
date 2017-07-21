package pl.north93.zgame.api.bukkit.gui.impl.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.gui.Gui;
import pl.north93.zgame.api.bukkit.gui.impl.NorthUriUtils;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlVariable
{
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String value;

    public Vars<Object> process(final Gui gui, final Vars<Object> vars)
    {
        if (this.value.startsWith("northplatform:"))
        {
            return Vars.of(this.name, NorthUriUtils.getInstance().call(this.value, vars));
        }
        else if (this.value.startsWith("@"))
        {
            return Vars.of(this.name, TranslatableString.of(gui.getMessagesBox(), this.value));
        }
        return Vars.of(this.name, this.value);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("value", this.value).toString();
    }
}

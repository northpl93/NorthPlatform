package pl.north93.northplatform.api.bukkit.gui.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.gui.impl.click.IClickSource;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.utils.Vars;

/**
 * Przedstawia kontekst dla ktorego generowany jest GuiElement.
 */
public final class XmlReaderContext
{
    private final IClickSource clickSource;
    private final MessagesBox  messagesBox;
    private final Vars<Object> vars;

    public XmlReaderContext(final IClickSource clickSource, final MessagesBox messagesBox, final Vars<Object> vars)
    {
        this.clickSource = clickSource;
        this.messagesBox = messagesBox;
        this.vars = vars;
    }

    public IClickSource getClickSource()
    {
        return this.clickSource;
    }

    public MessagesBox getMessagesBox()
    {
        return this.messagesBox;
    }

    public Vars<Object> getVars()
    {
        return this.vars;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("clickSource", this.clickSource).append("messagesBox", this.messagesBox).append("vars", this.vars).toString();
    }
}

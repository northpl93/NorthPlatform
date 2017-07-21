package pl.north93.zgame.api.bukkit.gui.impl;

import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.utils.Vars;

/**
 * Przedstawia kontekst dla ktorego renderowany jest element ekwipunku.
 */
public final class RenderContext
{
    private final MessagesBox  messagesBox;
    private final Vars<Object> vars;

    public RenderContext(final MessagesBox messagesBox, final Vars<Object> vars)
    {
        this.messagesBox = messagesBox;
        this.vars = vars;
    }

    public MessagesBox getMessagesBox()
    {
        return this.messagesBox;
    }

    public Vars<Object> getVars()
    {
        return this.vars;
    }
}

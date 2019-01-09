package pl.north93.northplatform.api.bukkit.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.messages.MessagesBox;

public final class ContentBuilder
{
    private final List<String> content = new LinkedList<>();
    private       MessagesBox  messagesBox;
    private       Locale       locale;

    /*default*/ ContentBuilder()
    {
    }

    public ContentBuilder box(final MessagesBox messagesBox)
    {
        this.messagesBox = messagesBox;
        return this;
    }

    public ContentBuilder locale(final String locale)
    {
        this.locale = Locale.forLanguageTag(locale);
        return this;
    }

    public ContentBuilder translated(final MessagesBox messagesBox, final Locale locale, final String key, final Object... args)
    {
        final String message = messagesBox.getLegacyMessage(locale, key, args);
        this.content.addAll(Arrays.asList(StringUtils.split(message, '\n')));
        return this;
    }

    public ContentBuilder translated(final String key, final Object... args)
    {
        Preconditions.checkNotNull(this.messagesBox, "You must use box()!");
        Preconditions.checkNotNull(this.locale, "You must use locale()!");
        this.translated(this.messagesBox, this.locale, key, args);
        return this;
    }

    public ContentBuilder add(final String message)
    {
        this.content.add(message);
        return this;
    }

    public ContentBuilder add(final Collection<String> messages)
    {
        this.content.addAll(messages);
        return this;
    }

    public ContentBuilder add(final String... messages)
    {
        this.content.addAll(Arrays.asList(messages));
        return this;
    }

    public List<String> getContent()
    {
        return this.content;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("content", this.content).toString();
    }
}

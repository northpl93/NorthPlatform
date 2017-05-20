package pl.north93.zgame.api.bukkit.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final class ContentBuilder
{
    private final List<String> content = new LinkedList<>();

    /*default*/ ContentBuilder()
    {
    }

    public void add(final String message)
    {
        this.content.add(message);
    }

    public void add(final Collection<String> messages)
    {
        this.content.addAll(messages);
    }

    public void add(final String... messages)
    {
        this.content.addAll(Arrays.asList(messages));
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

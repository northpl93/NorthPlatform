package pl.north93.zgame.api.global.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class NorthCommand
{
    private final String       name;
    private final List<String> aliases;
    private       boolean      isAsync;
    private       String       permission;

    public NorthCommand(final String name, final String... aliases)
    {
        this.name = name;
        this.aliases = Arrays.asList(aliases);
    }

    public final String getName()
    {
        return this.name;
    }

    public final List<String> getAliases()
    {
        return Collections.unmodifiableList(this.aliases);
    }

    protected final void setPermission(final String permission)
    {
        this.permission = permission;
    }

    public final String getPermission()
    {
        return this.permission;
    }

    public final boolean isAsync()
    {
        return this.isAsync;
    }

    public final void setAsync(final boolean async)
    {
        this.isAsync = async;
    }

    public abstract void execute(final NorthCommandSender sender, final Arguments args, final String label);

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("aliases", this.aliases).append("permission", this.permission).toString();
    }
}

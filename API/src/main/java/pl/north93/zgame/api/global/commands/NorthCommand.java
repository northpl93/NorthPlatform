package pl.north93.zgame.api.global.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class NorthCommand
{
    private final String       name;
    private final List<String> aliases;
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

    public abstract void execute(final NorthCommandSender sender, final Arguments args, final String label);
}

package pl.north93.zgame.api.global.commands.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.commands.annotation.QuickCommand;
import pl.north93.zgame.api.global.component.annotations.IgnoreExtensionPoint;

@IgnoreExtensionPoint // this class shouldn't be registered as extension point
class QuickNorthCommand extends NorthCommand
{
    private final MethodHandle commandBody;

    public QuickNorthCommand(final Method method, final QuickCommand info)
    {
        super(info.name(), info.aliases());
        this.setPermission(info.permission());
        this.setAsync(info.async());
        try
        {
            this.commandBody = MethodHandles.lookup().unreflect(method);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect method in QuickNorthCommand. CmdName:" + this.getName(), e);
        }
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        try
        {
            this.commandBody.invokeExact(sender, args, label);
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException("An exception has been thrown while invoking QuickNorthCommand", throwable);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("commandBody", this.commandBody).toString();
    }
}

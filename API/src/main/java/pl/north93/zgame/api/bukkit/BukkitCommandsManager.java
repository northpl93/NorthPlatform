package pl.north93.zgame.api.bukkit;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.I18n;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class BukkitCommandsManager implements ICommandsManager
{
    private CommandMap commandMap;

    public BukkitCommandsManager()
    {
        final Class<?> craftServer = Bukkit.getServer().getClass();
        try
        {
            this.commandMap = (CommandMap) craftServer.getMethod("getCommandMap").invoke(Bukkit.getServer());
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void registerCommand(final NorthCommand northCommand)
    {
        this.commandMap.register(northCommand.getName(), "np-bukkit", new WrappedNorthCommand(northCommand));
    }

    private class WrappedSender implements NorthCommandSender
    {
        private final CommandSender wrappedSender;

        private WrappedSender(final CommandSender wrappedSender)
        {
            this.wrappedSender = wrappedSender;
        }

        @Override
        public void sendMessage(final String message)
        {
            this.wrappedSender.sendMessage(message);
        }

        @Override
        public boolean isPlayer()
        {
            return this.wrappedSender instanceof Player;
        }

        @Override
        public boolean isConsole()
        {
            return this.wrappedSender instanceof ConsoleCommandSender;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("wrappedSender", this.wrappedSender).toString();
        }
    }

    private class WrappedNorthCommand extends Command
    {
        private final NorthCommand wrapped;

        public WrappedNorthCommand(final NorthCommand wrapped)
        {
            super(wrapped.getName());
            this.wrapped = wrapped;
            this.setAliases(wrapped.getAliases());
        }

        @Override
        public boolean execute(final CommandSender commandSender, final String s, final String[] strings)
        {
            final String permission = this.wrapped.getPermission();
            if (!StringUtils.isEmpty(permission) && !commandSender.hasPermission(permission))
            {
                commandSender.sendMessage(I18n.getBukkitMessage("command.no_permissions"));
                return true;
            }
            this.wrapped.execute(new WrappedSender(commandSender), new Arguments(strings), s);
            return true;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("wrapped", this.wrapped).toString();
        }
    }
}

package pl.north93.zgame.api.bukkit;

import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.translateAlternateColorCodes;


import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.UTF8Control;

public class BukkitCommandsManager implements ICommandsManager
{
    private final ResourceBundle apiMessages = ResourceBundle.getBundle("Messages", new UTF8Control());
    private final CommandMap     commandMap;

    public BukkitCommandsManager()
    {
        this.commandMap = Bukkit.getCommandMap();
    }

    @Override
    public void registerCommand(final NorthCommand northCommand)
    {
        this.commandMap.register(northCommand.getName(), "np-bukkit", new WrappedNorthCommand(northCommand));
    }

    @Override
    public void stop()
    {
        // todo unregister commands?
    }

    private class WrappedSender implements NorthCommandSender
    {
        private final CommandSender wrappedSender;

        private WrappedSender(final CommandSender wrappedSender)
        {
            this.wrappedSender = wrappedSender;
        }

        @Override
        public String getName()
        {
            return this.wrappedSender.getName();
        }

        @Override
        public Locale getMyLocale()
        {
            if (this.wrappedSender instanceof Player)
            {
                final Player player = (Player) this.wrappedSender;
                return Locale.forLanguageTag(player.getLocale());
            }
            return Locale.getDefault();
        }

        @Override
        public void sendMessage(final String message, final MessageLayout layout)
        {
            final BaseComponent component = ChatUtils.fromLegacyText(message);
            this.wrappedSender.sendMessage(layout.processMessage(component));
        }

        @Override
        public void sendMessage(final BaseComponent component, final MessageLayout layout)
        {
            this.wrappedSender.sendMessage(layout.processMessage(component));
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
        public Object unwrapped()
        {
            return this.wrappedSender;
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

            this.setPermission(wrapped.getPermission()); // dodajemy, aby dzialalo wsparcie dla auto-uzupelniania
            this.setPermissionMessage(translateAlternateColorCodes(BukkitCommandsManager.this.apiMessages.getString("command.no_permissions")));
        }

        @Override
        public boolean execute(final CommandSender commandSender, final String label, final String[] args)
        {
            final String permission = this.wrapped.getPermission();
            if (! StringUtils.isEmpty(permission) && ! commandSender.hasPermission(permission))
            {
                commandSender.sendMessage(translateAlternateColorCodes(BukkitCommandsManager.this.apiMessages.getString("command.no_permissions")));
                return true;
            }
            if (this.wrapped.isAsync())
            {
                API.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
                {
                    this.wrapped.execute(new WrappedSender(commandSender), new Arguments(args), label);
                });
            }
            else
            {
                this.wrapped.execute(new WrappedSender(commandSender), new Arguments(args), label);
            }

            return true;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("wrapped", this.wrapped).toString();
        }
    }
}

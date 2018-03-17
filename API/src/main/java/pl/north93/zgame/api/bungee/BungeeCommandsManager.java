package pl.north93.zgame.api.bungee;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.ICommandsManager;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.messages.MessageLayout;

public class BungeeCommandsManager implements ICommandsManager
{
    @Override
    public void registerCommand(final NorthCommand northCommand)
    {
        ProxyServer.getInstance().getPluginManager().registerCommand(((BungeeApiCore) API.getApiCore()).getBungeePlugin(), new WrappedCommand(northCommand));
    }

    @Override
    public void stop()
    {
        // todo unregister commands?
    }

    private class WrappedCommand extends Command
    {
        private final NorthCommand northCommand;

        public WrappedCommand(final NorthCommand northCommand)
        {
            super(northCommand.getName(), northCommand.getPermission(), northCommand.getAliases().toArray(new String[]{}));
            this.northCommand = northCommand;
        }

        @Override
        public void execute(final CommandSender commandSender, final String[] strings)
        {
            if (this.northCommand.isAsync())
            {
                API.getApiCore().getPlatformConnector().runTaskAsynchronously(() ->
                {
                    this.northCommand.execute(new WrappedSender(commandSender), new Arguments(strings), ""); // TODO
                });
            }
            else
            {
                this.northCommand.execute(new WrappedSender(commandSender), new Arguments(strings), ""); // TODO
            }
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("northCommand", this.northCommand).toString();
        }
    }

    private class WrappedSender implements NorthCommandSender
    {
        private final CommandSender sender;

        public WrappedSender(final CommandSender sender)
        {
            this.sender = sender;
        }

        @Override
        public String getName()
        {
            return this.sender.getName();
        }

        @Override
        public Locale getMyLocale()
        {
            if (this.sender instanceof ProxiedPlayer)
            {
                return ((ProxiedPlayer) this.sender).getLocale();
            }
            return Locale.getDefault();
        }

        @Override
        public void sendMessage(final String message, final MessageLayout layout)
        {
            final BaseComponent component = ChatUtils.fromLegacyText(message);
            this.sender.sendMessage(layout.processMessage(component));
        }

        @Override
        public void sendMessage(final BaseComponent component, final MessageLayout layout)
        {
            this.sender.sendMessage(layout.processMessage(component));
        }

        @Override
        public boolean isPlayer()
        {
            return this.sender instanceof ProxiedPlayer;
        }

        @Override
        public boolean isConsole()
        {
            return ! this.isPlayer();
        }

        @Override
        public Object unwrapped()
        {
            return this.sender;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("sender", this.sender).toString();
        }
    }
}

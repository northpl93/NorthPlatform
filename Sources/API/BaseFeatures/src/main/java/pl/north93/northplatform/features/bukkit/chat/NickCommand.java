package pl.north93.northplatform.features.bukkit.chat;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.IOnlinePlayer;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;

public class NickCommand extends NorthCommand
{
    @Inject @Messages("BaseFeatures")
    private MessagesBox messages;
    @Inject
    private IPlayersManager playersManager;

    public NickCommand()
    {
        super("nick");
        this.setPermission("basefeatures.cmd.nick");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            this.printNickInfo(sender, sender.getName());
        }
        else if (args.length() == 1)
        {
            this.printNickInfo(sender, args.asString(0));
        }
        else
        {
            try (final IPlayerTransaction t = this.playersManager.transaction(args.asString(0)))
            {
                if (args.asString(1).equalsIgnoreCase("reset"))
                {
                    t.getPlayer().setDisplayName(null);
                    sender.sendMessage("&eZresetowano nazwe wyswietlana dla: " + t.getPlayer().getLatestNick());
                }
                else
                {
                    final String newName = args.asText(1);
                    t.getPlayer().setDisplayName(newName);
                    sender.sendMessage("&eNiestandardowa nazwa " + t.getPlayer().getLatestNick() + " zmieniona na: " + newName);
                }
            }
            catch (final PlayerNotFoundException e)
            {
                sender.sendMessage(this.messages, "command.no_player");
            }
        }
    }

    private void printNickInfo(final NorthCommandSender sender, final String nick)
    {
        try (final IPlayerTransaction t = this.playersManager.transaction(nick))
        {
            final IPlayer player = t.getPlayer();
            if (t.isOnline())
            {
                final IOnlinePlayer onlinePlayer = (IOnlinePlayer) player;
                sender.sendMessage("&ePrawdziwy nick: " +  onlinePlayer.getNick() + " (" +  onlinePlayer.getLatestNick() + "&e)");
            }
            else
            {
                sender.sendMessage("&ePrawdziwy (ostatni znany) nick: " +  player.getLatestNick());
            }

            if (player.hasDisplayName())
            {
                sender.sendMessage("&eNiestandardowa nazwa: " + player.getDisplayName());
            }
            else
            {
                sender.sendMessage("&eBrak niestandardowej nazwy.");
            }
        }
        catch (final PlayerNotFoundException e)
        {
            sender.sendMessage(this.messages, "command.no_player");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

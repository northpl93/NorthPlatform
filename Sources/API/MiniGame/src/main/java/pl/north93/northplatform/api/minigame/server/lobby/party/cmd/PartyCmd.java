package pl.north93.northplatform.api.minigame.server.lobby.party.cmd;

import java.util.Locale;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.MessageLayout;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.minigame.server.shared.party.ClientResponse;
import pl.north93.northplatform.api.minigame.server.shared.party.PartyClient;
import pl.north93.northplatform.api.minigame.shared.api.party.IParty;

/**
 * Komenda do obsługi party w komponencie lobby
 */
public class PartyCmd extends NorthCommand
{
    @Inject
    private PartyClient partyClient;
    @Inject
    private IPlayersManager playersManager;
    @Inject @Messages("Party")
    private MessagesBox messages;

    public PartyCmd()
    {
        super("party", "grupa");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer player = INorthPlayer.wrap(sender);

        if (args.isEmpty())
        {
            this.help(player);
            return;
        }

        final String arg = args.asString(0).toLowerCase(Locale.ROOT);

        if (args.length() == 1)
        {
            switch (arg)
            {
                case "list":
                case "lista":
                    this.list(player);
                    break;
                case "accept":
                case "akceptuj":
                    this.accept(player);
                    break;
                case "leave":
                case "opusc":
                    this.leave(player);
                    break;
                default:
                    this.help(player);
            }
        }
        else if (args.length() == 2)
        {
            final String parameter = args.asString(1);
            switch (arg)
            {
                case "invite":
                case "dodaj":
                    this.invite(player, parameter);
                    break;
                case "kick":
                case "wyrzuc":
                    this.kick(player, parameter);
                    break;
                default:
                    this.help(player);
            }
        }
        else
        {
            this.help(player);
        }
    }

    private void accept(final INorthPlayer player)
    {
        final ClientResponse response = this.partyClient.accept(player);
        switch (response)
        {
            case NO_INVITE:
                player.sendMessage(this.messages, "join.failed.no_invite");
                break;

            case ALREADY_IN_PARTY:
                player.sendMessage(this.messages, "join.failed.in_other");
                break;

            case OK:
                // komunikat obsluzony w evencie w bungee
                break;
        }
    }

    private void leave(final INorthPlayer player)
    {
        final ClientResponse response = this.partyClient.leave(player);
        switch (response)
        {
            case NO_PARTY:
                player.sendMessage(this.messages, "error.no_party");
                break;

            case OK:
                player.sendMessage(this.messages, "leave.success");
                break;
        }
    }

    private void list(final INorthPlayer player)
    {
        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null)
        {
            // nie ma party
            player.sendMessage(this.messages, "error.no_party");
            return;
        }

        player.sendMessage(this.messages, "separator");
        player.sendMessage(this.messages, "header", MessageLayout.CENTER);
        player.sendMessage();

        for (final Identity playerId : party.getPlayers())
        {
            final String messageKey = party.isOwner(playerId.getUuid()) ? "list.leader" : "list.player";
            player.sendMessage(this.messages, messageKey, MessageLayout.CENTER, this.identityToNick(playerId));
        }

        player.sendMessage(this.messages, "list.leave", MessageLayout.SEPARATED_CENTER);
        player.sendMessage(this.messages, "separator");
    }

    private void invite(final INorthPlayer player, final String nick)
    {
        final ClientResponse response = this.partyClient.invite(player, nick);
        switch (response)
        {
            case NO_OWNER:
                player.sendMessage(this.messages, "error.no_owner");
                break;
            case NO_PLAYER:
                player.sendMessage(this.messages, "error.no_player");
                break;
            case ALREADY_IN_PARTY:
                break;
            case OK:
                player.sendMessage(this.messages, "invite.send_success");
                break;
        }
    }

    private void kick(final INorthPlayer player, final String nick)
    {
        if (nick.equalsIgnoreCase(player.getName()))
        {
            return;
        }

        final ClientResponse response = this.partyClient.kick(player, nick);
        switch (response)
        {
            case NO_PARTY:
                player.sendMessage(this.messages, "error.no_party");
                break;
            case NO_OWNER:
                player.sendMessage(this.messages, "error.no_owner");
                break;
            case NO_PLAYER:
                player.sendMessage(this.messages, "error.no_player");
                break;
            case ERROR:
                break;
            case OK:
                break;
        }
    }

    private void help(final INorthPlayer player)
    {
        player.sendMessage(this.messages, "separator");
        player.sendMessage(this.messages, "help.header", MessageLayout.CENTER);
        player.sendMessage(this.messages, "help.content");
        player.sendMessage(this.messages, "separator");
    }

    private String identityToNick(final Identity identity)
    {
        return this.playersManager.unsafe().get(identity).map(IPlayer::getDisplayName).orElse(identity.getNick());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

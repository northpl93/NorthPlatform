package pl.arieals.lobby.party;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.party.ClientResponse;
import pl.arieals.api.minigame.server.party.PartyClient;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.MessageLayout;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;

public class PartyCmd extends NorthCommand
{
    @Inject
    private PartyClient     partyClient;
    @Inject
    private INetworkManager networkManager;
    @Inject @Messages("Party")
    private MessagesBox     messages;

    public PartyCmd()
    {
        super("party", "grupa");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

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

    private void accept(final Player player)
    {
        final ClientResponse response = this.partyClient.accept(player);
        switch (response)
        {
            case NO_INVITE:
                this.messages.sendMessage(player, "join.failed.no_invite");
                break;

            case ALREADY_IN_PARTY:
                this.messages.sendMessage(player, "join.failed.in_other");
                break;

            case OK:
                // komunikat obsluzony w evencie w bungee
                break;
        }
    }

    private void leave(final Player player)
    {
        final ClientResponse response = this.partyClient.leave(player);
        switch (response)
        {
            case NO_PARTY:
                this.messages.sendMessage(player, "error.no_party");
                break;

            case OK:
                this.messages.sendMessage(player, "leave.success");
                break;
        }
    }

    private void list(final Player player)
    {
        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null)
        {
            // nie ma party
            this.messages.sendMessage(player, "error.no_party");
            return;
        }

        this.messages.sendMessage(player, "separator");
        this.messages.sendMessage(player, "header", MessageLayout.CENTER);
        player.sendMessage();

        for (final UUID playerId : party.getPlayers())
        {
            final String messageKey = party.isOwner(playerId) ? "list.leader" : "list.player";
            this.messages.sendMessage(player, messageKey, MessageLayout.CENTER, this.uuidToNick(playerId));
        }

        this.messages.sendMessage(player, "list.leave", MessageLayout.SEPARATED_CENTER);
        this.messages.sendMessage(player, "separator");
    }

    private void invite(final Player player, final String nick)
    {
        final ClientResponse response = this.partyClient.invite(player, nick);
        switch (response)
        {
            case NO_OWNER:
                this.messages.sendMessage(player, "error.no_owner");
                break;
            case NO_PLAYER:
                this.messages.sendMessage(player, "error.no_player");
                break;
            case ALREADY_IN_PARTY:
                break;
            case OK:
                this.messages.sendMessage(player, "invite.send_success");
                break;
        }
    }

    private void kick(final Player player, final String nick)
    {
        if (nick.equalsIgnoreCase(player.getName()))
        {
            return;
        }

        final ClientResponse response = this.partyClient.kick(player, nick);
        switch (response)
        {
            case NO_PARTY:
                break;
            case NO_OWNER:
                this.messages.sendMessage(player, "error.no_owner");
                break;
            case NO_PLAYER:
                this.messages.sendMessage(player, "error.no_player");
                break;
            case ERROR:
                break;
            case OK:
                break;
        }
    }

    private void help(final Player player)
    {
        this.messages.sendMessage(player, "separator");
        this.messages.sendMessage(player, "help.header", MessageLayout.CENTER);
        this.messages.sendMessage(player, "help.content");
        this.messages.sendMessage(player, "separator");
    }

    private String uuidToNick(final UUID uuid)
    {
        return this.networkManager.getPlayers().getNickFromUuid(uuid).orElse(uuid.toString());
    }
}

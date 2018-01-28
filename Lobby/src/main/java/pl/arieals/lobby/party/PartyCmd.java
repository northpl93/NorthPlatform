package pl.arieals.lobby.party;

import java.util.Locale;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.party.ClientResponse;
import pl.arieals.api.minigame.server.party.PartyClient;
import pl.arieals.api.minigame.shared.api.party.IParty;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class PartyCmd extends NorthCommand
{
    @Inject
    private PartyClient partyClient;
    @Inject @Messages("Party")
    private MessagesBox messages;

    public PartyCmd()
    {
        super("party", "grupa");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        if (args.isEmpty())
        {
            // todo
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
            }
        }
        else
        {

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

                break;
        }

        player.sendMessage(response.name());
    }

    private void leave(final Player player)
    {
        final ClientResponse response = this.partyClient.leave(player);
        switch (response)
        {
            case NO_PARTY:
                break;

            case OK:
                break;
        }
        player.sendMessage(response.name());
    }

    private void list(final Player player)
    {
        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null)
        {
            player.sendMessage("no party");
            // nie ma party
            return;
        }

        player.sendMessage(party.getPlayers().toString());
    }

    private void invite(final Player player, final String nick)
    {
        final ClientResponse response = this.partyClient.invite(player, nick);
        player.sendMessage(response.name());
    }

    private void kick(final Player player, final String nick)
    {
        if (nick.equalsIgnoreCase(player.getName()))
        {
            return;
        }


    }
}

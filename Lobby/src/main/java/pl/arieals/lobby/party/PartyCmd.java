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

public class PartyCmd extends NorthCommand
{
    @Inject
    private PartyClient    partyClient;

    public PartyCmd()
    {
        super("party", "grupa");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.isEmpty())
        {
            // todo
            return;
        }

        final Player player = (Player) sender.unwrapped();
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
            }
        }
        else if (args.length() == 2)
        {
            switch (arg)
            {
                case "invite":
                case "dodaj":
                    this.invite(player, args.asString(1));
                    break;

            }
        }
        else
        {

        }
    }

    private void invite(final Player player, final String nick)
    {
        final ClientResponse response = this.partyClient.invite(player, nick);
        player.sendMessage(response.name());
    }

    private void accept(final Player player)
    {
        final ClientResponse response = this.partyClient.accept(player);
        player.sendMessage(response.name());
    }

    private void list(final Player player)
    {
        final IParty party = this.partyClient.getPlayerParty(player);
        if (party == null)
        {
            // nie ma party
            return;
        }

        player.sendMessage(party.getPlayers().toString());
    }


}

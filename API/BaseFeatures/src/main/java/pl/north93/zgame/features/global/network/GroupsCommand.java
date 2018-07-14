package pl.north93.zgame.features.global.network;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.permissions.Group;
import pl.north93.zgame.api.global.permissions.PermissionsManager;
import pl.north93.zgame.api.global.utils.DateUtil;

public class GroupsCommand extends NorthCommand
{
    @Inject
    private ApiCore            apiCore;
    @Inject
    private PermissionsManager permissionsManager;
    @Inject
    private INetworkManager    networkManager;
    @Inject @Messages("BaseFeatures")
    private MessagesBox        messages;

    public GroupsCommand()
    {
        super("groups", "group");
        this.setPermission("basefeatures.cmd.groups");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 0)
        {
            sender.sendMessage("&e/" + label + " <gracz> - wyswietla grupe gracza");
            sender.sendMessage("&e/" + label + " <gracz> <grupa> [czas] - zmienia grupe gracza");
        }
        else if (args.length() == 1)
        {
            final String username = args.asString(0);

            final boolean result = this.networkManager.getPlayers().access(username, online ->
            {
                sender.sendMessage("&aGrupa &e{0} &ato &e{1}", online.getNick(), online.getGroup().getName());
                this.sendExpirationInfo(sender, online.getGroupExpireAt());
            }, offline ->
            {
                sender.sendMessage("&aGrupa &e{0} &a(&e{1}&a) to &e{2}", offline.getLatestNick(), offline.getUuid(), offline.getGroup().getName());
                this.sendExpirationInfo(sender, offline.getGroupExpireAt());
            });

            if (! result)
            {
                sender.sendMessage(this.messages, "command.no_player");
            }
        }
        else if (args.length() == 2)
        {
            final String username = args.asString(0);
            final String newGroup = args.asString(1);

            this.changeGroup(sender, username, newGroup, 0);
        }
        else if (args.length() == 3)
        {
            final String username = args.asString(0);
            final String newGroup = args.asString(1);
            final long groupExpireAt;

            try
            {
                groupExpireAt = DateUtil.parseDateDiff(args.asString(2), true);
            }
            catch (final Exception e)
            {
                sender.sendMessage("&cNiepoprawny format czasu. Przyklad: 30d5h59m59s");
                return;
            }

            this.changeGroup(sender, username, newGroup, groupExpireAt);
        }
        else
        {
            sender.sendMessage("&cZla ilosc argumentów!");
        }
    }

    private void changeGroup(final NorthCommandSender sender, final String username, final String groupName, final long groupExpireAt)
    {
        final Group newGroup = this.permissionsManager.getGroupByName(groupName);
        if (newGroup == null)
        {
            sender.sendMessage("&cNie ma takiej grupy!");
            return;
        }
        if (this.networkManager.getPlayers().access(username, player ->
        {
            player.setGroup(newGroup);
            player.setGroupExpireAt(groupExpireAt);
        }))
        {
            sender.sendMessage("&aPomyslnie zmieniono grupe na " + newGroup.getName());
        }
        else
        {
            sender.sendMessage(this.messages, "command.no_player");
        }
    }

    private void sendExpirationInfo(final NorthCommandSender sender, final long groupExpireAt)
    {
        if (groupExpireAt == 0)
        {
            sender.sendMessage("&eNigdy &anie wygasa");
        }
        else
        {
            final DateTimeFormatter formatter = ISO_LOCAL_DATE_TIME.withZone(ZoneId.of("Europe/Warsaw"));
            final Instant instant = Instant.ofEpochMilli(groupExpireAt);

            final String timeIso = formatter.format(instant);
            sender.sendMessage("&aWygasnie &e{0}", timeIso);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

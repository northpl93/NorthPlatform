package pl.north93.zgame.features.global.network;

import static java.time.format.DateTimeFormatter.ofPattern;


import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.LoginHistoryEntry;
import pl.north93.zgame.api.global.storage.StorageConnector;

public class LoginHistoryCommand extends NorthCommand
{
    private static final DateTimeFormatter FORMATTER = ofPattern("dd MM HH:mm:ss").withZone(ZoneId.of("Europe/Warsaw"));
    @Inject
    private StorageConnector storage;

    public LoginHistoryCommand()
    {
        super("loginhistory", "lh");
        this.setAsync(true);
        this.setPermission("basefeatures.cmd.loginhistory");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() == 2)
        {
            switch (args.asString(0).toLowerCase(Locale.ROOT))
            {
                case "ip":
                    this.ipHistory(sender, args.asString(1));
                    break;
                case "nick":
                    this.nickHistory(sender, args.asString(1));
                    break;
                default:
                    sender.sendMessage("&c/loginhistory ip/nick <ip/nick> np. /loginhistory nick NorthPL93");
            }
        }
        else
        {
            sender.sendMessage("&c/loginhistory ip/nick <ip/nick> np. /loginhistory nick NorthPL93");
        }
    }

    private void ipHistory(final NorthCommandSender sender, final String ip)
    {
        sender.sendMessage("&cHistoria logowania sie adresu IP {0} (max 17 wyników)", ip);

        final MongoCollection<LoginHistoryEntry> history = this.getCollection();

        final FindIterable<LoginHistoryEntry> results = history.find(new Document("ip", ip)).sort(new Document("at", -1)).limit(17);
        for (final LoginHistoryEntry result : results)
        {
            final String loggedAt = FORMATTER.format(result.getAt());
            final String nick = result.getNick();
            final String bungee = result.getBungee();

            sender.sendMessage("&8[&7{0}&8] &7{1} &8@ &7{2}", loggedAt, nick, bungee);
        }
        sender.sendMessage("&cU góry najnowsze");
    }

    private void nickHistory(final NorthCommandSender sender, final String nick)
    {
        sender.sendMessage("&cHistoria logowania sie nicku {0} (max 17 wyników)", nick);

        final MongoCollection<LoginHistoryEntry> history = this.getCollection();

        final FindIterable<LoginHistoryEntry> results = history.find(new Document("nick", nick)).sort(new Document("at", -1)).limit(17);
        for (final LoginHistoryEntry result : results)
        {
            final String loggedAt = FORMATTER.format(result.getAt());
            final String ip = result.getIp();
            final String bungee = result.getBungee();

            sender.sendMessage("&8[&7{0}&8] &7{1} &8@ &7{2} &8@ &7{3}", loggedAt, ip, nick, bungee);
        }
        sender.sendMessage("&cU góry najnowsze");
    }

    private MongoCollection<LoginHistoryEntry> getCollection()
    {
        final MongoDatabase mainDatabase = this.storage.getMainDatabase();
        return mainDatabase.getCollection("join_history")
                           .withDocumentClass(LoginHistoryEntry.class);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

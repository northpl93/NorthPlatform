package pl.north93.zgame.features.global.network;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.storage.StorageConnector;

public class LoginHistoryCommand extends NorthCommand
{
    private static final SimpleDateFormat dt = new SimpleDateFormat("dd-MM HH:mm:ss");;
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
        final MongoCollection<Document> history = this.storage.getMainDatabase().getCollection("join_history");
        final FindIterable<Document> results = history.find(new Document("ip", ip)).sort(new Document("at", -1)).limit(17);
        for (final Document result : results)
        {
            final String nick = result.getString("nick");
            final String bungee = result.getString("bungee");
            final Long at = result.getLong("at");
            sender.sendMessage("&8[&7{0}&8] &7{1} &8@ &7{2}", dt.format(new Date(at)), nick, bungee);
        }
        sender.sendMessage("&cU góry najnowsze");
    }

    private void nickHistory(final NorthCommandSender sender, final String nick)
    {
        sender.sendMessage("&cHistoria logowania sie nicku {0} (max 17 wyników)", nick);
        final MongoCollection<Document> history = this.storage.getMainDatabase().getCollection("join_history");
        final FindIterable<Document> results = history.find(new Document("nick", nick)).sort(new Document("at", -1)).limit(17);
        for (final Document result : results)
        {
            final String ip = result.getString("ip");
            final String bungee = result.getString("bungee");
            final Long at = result.getLong("at");
            sender.sendMessage("&8[&7{0}&8] &7{1} &8@ &7{2} &8@ &7{3}", dt.format(new Date(at)), ip, nick, bungee);
        }
        sender.sendMessage("&cU góry najnowsze");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

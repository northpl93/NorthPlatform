package pl.north93.northplatform.features.global.punishment.cmd;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.storage.StorageConnector;

public class UnbanAllCommand extends NorthCommand
{
    @Inject
    private StorageConnector storage;

    public UnbanAllCommand()
    {
        super("unbanall");
        this.setPermission("basefeatures.cmd.unbanall");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1 || !args.asString(0).equals("potwierdz"))
        {
            sender.sendMessage("&cTa komenda odbanuje WSZYSTKICH zbanowanych graczy. Wpisz /unbanall potwierdz zeby kontynuowac.");
            return;
        }

        final MongoCollection<Document> players = this.storage.getMainDatabase().getCollection("players");
        final UpdateResult result = players.updateMany(new Document(), new Document("$set", new Document("banned", false)));
        sender.sendMessage("&cOdbanowano " + result.getModifiedCount() + " graczy! (cache moze ich nie wpuszczac jeszcze przez jakis czas)");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

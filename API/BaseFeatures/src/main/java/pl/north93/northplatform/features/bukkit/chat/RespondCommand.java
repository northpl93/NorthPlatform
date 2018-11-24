package pl.north93.northplatform.features.bukkit.chat;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.StringUtils;

import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.network.INetworkManager;

/**
 * Created by Konrad on 2017-02-15.
 */
public class RespondCommand extends NorthCommand
{
    @Inject
    private IBukkitExecutor executor;
    @Inject
    private INetworkManager networkManager;

    private static final MetaKey LAST_SENDER = MetaKey.get("lastMessageSender");

    public RespondCommand()
    {
        super("respond", "response", "r", "odp", "odpowiedz");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() < 1)
        {
            return;
        }

        // we can't execute commands asynchronously due to bukkit stupidity
        this.executor.mixed(() -> // async part
        {
            final String lastSender = this.networkManager.getPlayers().unsafe().getOnlineValue(sender.getName()).get().getMetaStore().get(LAST_SENDER);

            if (StringUtils.isEmpty(lastSender))
            {
                return null;
            }
            return lastSender;
        }, lastSender -> // sync part invoked when async completes
        {
            ((Player) sender.unwrapped()).performCommand("msg " + lastSender + " " + args.asText(0));
        });
    }
}

package pl.north93.zgame.api.bukkitcommands;

import org.bukkit.entity.Player;

import org.apache.commons.lang.StringUtils;

import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.network.INetworkManager;

/**
 * Created by Konrad on 2017-02-15.
 */
public class RespondCommand extends NorthCommand {
    private BukkitApiCore  apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;

    private static final MetaKey LAST_SENDER = MetaKey.get("lastMessageSender");

    public RespondCommand()
    {
        super("respond", "response", "r", "odp", "odpowiedz");
    }
    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label) {
        if(args.length() < 1)
        {
            return;
        }

        // we can't execute commands asynchronously due to bukkit stupidity
        this.apiCore.sync(() -> // async part
        {
            final String lastSender = this.networkManager.getOnlinePlayer(sender.getName()).get().getMetaStore().getString(LAST_SENDER);

            if(StringUtils.isEmpty(lastSender))
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

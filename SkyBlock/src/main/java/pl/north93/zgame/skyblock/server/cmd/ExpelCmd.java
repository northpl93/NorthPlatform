package pl.north93.zgame.skyblock.server.cmd;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectMessages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.skyblock.server.SkyBlockServer;
import pl.north93.zgame.skyblock.shared.api.IslandRole;
import pl.north93.zgame.skyblock.shared.api.player.SkyPlayer;

public class ExpelCmd extends NorthCommand
{
    private ApiCore         apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;
    @InjectMessages("SkyBlock")
    private MessagesBox     messages;

    public ExpelCmd()
    {
        super("expel", "expell", "wyrzuc", "wypros");
        this.setAsync(true);
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Value<IOnlinePlayer> onlineSender = this.networkManager.getOnlinePlayer(sender.getName());
        final SkyPlayer skyPlayer = SkyPlayer.get(onlineSender);
        if (! skyPlayer.hasIsland())
        {
            sender.sendMessage(this.messages, "error.you_must_have_island");
            return;
        }

        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.expel.help", label);
        }
        else
        {
            if (skyPlayer.getIslandRole().equals(IslandRole.MEMBER))
            {
                sender.sendMessage(this.messages, "error.you_must_be_owner");
                return;
            }
            this.server.getSkyBlockManager().leaveIsland(skyPlayer.getIslandId(), sender.getName(), args.asString(0), false);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

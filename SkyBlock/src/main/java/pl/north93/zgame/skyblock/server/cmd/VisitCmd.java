package pl.north93.zgame.skyblock.server.cmd;

import java.util.ResourceBundle;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.component.annotations.InjectResource;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class VisitCmd extends NorthCommand
{
    private ApiCore             apiCore;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private     INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private     SkyBlockServer  server;
    @InjectResource(bundleName = "SkyBlock")
    private     ResourceBundle  messages;

    public VisitCmd()
    {
        super("visit", "odwiedz");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if (args.length() != 1)
        {
            sender.sendMessage(this.messages, "cmd.visit.args");
            return;
        }
    }
}

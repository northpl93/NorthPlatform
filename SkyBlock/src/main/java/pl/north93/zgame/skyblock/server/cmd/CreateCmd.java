package pl.north93.zgame.skyblock.server.cmd;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.skyblock.api.SkyPlayer;
import pl.north93.zgame.skyblock.server.SkyBlockServer;

public class CreateCmd extends NorthCommand
{
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private INetworkManager networkManager;
    @InjectComponent("SkyBlock.Server")
    private SkyBlockServer  server;

    public CreateCmd()
    {
        super("create", "stworz");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final SkyPlayer skyPlayer = new SkyPlayer(this.networkManager.getNetworkPlayer(sender.getName()));
        if (skyPlayer.hasIsland())
        {
            sender.sendMessage("Juz masz wyspe");
            return;
        }

        final Boolean creationResult = this.server.getSkyBlockManager().createIsland("Testowa", sender.getName());
        if (creationResult)
        {
            sender.sendMessage("Utworzono wyspe");
        }
        else
        {
            sender.sendMessage("Nie udalo sie utworzyc wyspy!");
        }
    }
}

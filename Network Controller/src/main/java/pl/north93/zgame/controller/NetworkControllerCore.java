package pl.north93.zgame.controller;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.Platform;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.controller.reposerver.RepoServer;

public class NetworkControllerCore extends Component
{
    private RepoServer            repoServer             = new RepoServer(this);

    @Override
    protected void enableComponent()
    {
        API.getLogger().info("Starting NetworkController...");
        if (API.getPlatform() == Platform.BUNGEE) // on standalone platform context will be added automatically from getId()
        {
            API.getRpcManager().addListeningContext("controller");
        }
        API.getRpcManager().addRpcImplementation(NetworkControllerRpc.class, new NetworkControllerRpcImpl());
        this.repoServer.start();
    }

    @Override
    protected void disableComponent()
    {
        this.repoServer.stop();
        // TODO Servers Manager safe stop
        API.getLogger().info("Network Controller stopped!");
    }
}

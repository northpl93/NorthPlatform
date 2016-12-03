package pl.north93.zgame.controller.reposerver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.repo.RepoServerRpc;

public class RepoServer extends Component
{
    private final HttpServer  server;
    private       RepoManager repoManager;

    public RepoServer()
    {
        try
        {
            this.server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8000), 0);
            this.server.createContext("/", new HttpRepoServerContext(this));
            this.server.setExecutor(null);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to create repo http server.", e);
        }
    }

    @Override
    protected void enableComponent()
    {
        API.getLogger().info("Starting repo server");
        this.repoManager = new RepoManager(API.getFile("repo"));
        API.getRpcManager().addRpcImplementation(RepoServerRpc.class, new RepoServerRpcImpl());
        this.server.start();
    }

    @Override
    protected void disableComponent()
    {
        API.getLogger().info("Stopping repo server");
        this.server.stop(0);
    }
}

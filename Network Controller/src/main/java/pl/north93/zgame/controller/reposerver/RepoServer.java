package pl.north93.zgame.controller.reposerver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.repo.RepoServerRpc;
import pl.north93.zgame.controller.NetworkControllerCore;

public class RepoServer
{
    private final NetworkControllerCore controllerCore;
    private final HttpServer            server;
    private       RepoManager           repoManager;

    public RepoServer(final NetworkControllerCore controllerCore)
    {
        this.controllerCore = controllerCore;
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

    public void start()
    {
        API.getLogger().info("Starting repo server");
        this.repoManager = new RepoManager(API.getFile("repo"));
        API.getRpcManager().addRpcImplementation(RepoServerRpc.class, new RepoServerRpcImpl());
        this.server.start();
    }

    public void stop()
    {
        API.getLogger().info("Stopping repo server");
        this.server.stop(0);
    }
}

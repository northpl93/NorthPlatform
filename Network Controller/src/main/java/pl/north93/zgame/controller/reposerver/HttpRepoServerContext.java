package pl.north93.zgame.controller.reposerver;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpRepoServerContext implements HttpHandler
{
    private final RepoServer repoServer;

    public HttpRepoServerContext(final RepoServer repoServer)
    {
        this.repoServer = repoServer;
    }

    @Override
    public void handle(final HttpExchange httpExchange) throws IOException
    {

    }
}

package pl.north93.zgame.daemon.servers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.core.LogEvent;

import pl.north93.zgame.api.global.API;

public class LoggerServer extends Thread
{
    private static final int LOGGER_PORT = 2137;
    private boolean working = true;
    private ServerSocketChannel server;
    private Selector selector;

    @Override
    public void run()
    {
        this.setup();
        while (this.working)
        {
            try
            {
                this.doServerLoop();
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            this.selector.close();
            this.server.close();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    private void doServerLoop() throws Exception
    {
        this.selector.select(); // czekamy az cos sie stanie
        final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();

        while (selectedKeys.hasNext())
        {
            final SelectionKey myKey = selectedKeys.next();

            // Tests whether this key's channel is ready to accept a new socket connection
            if (myKey.isAcceptable())
            {
                final SocketChannel client = this.server.accept();

                client.configureBlocking(false);
                client.register(this.selector, SelectionKey.OP_READ);

                API.getLogger().info("New client connected to logger: " + client);
            }
            // Tests whether this key's channel is ready for reading
            else if (myKey.isReadable())
            {
                final SocketChannel client = (SocketChannel) myKey.channel();
                final ByteBuffer buffer = ByteBuffer.allocate(256);
                client.read(buffer);
                final LogEvent event = (LogEvent) new ObjectInputStream(new ByteArrayInputStream(buffer.array()));

                API.getLogger().info("event: "+event);
            }
            selectedKeys.remove();
        }
    }

    private void setup()
    {
        try
        {
            this.server = ServerSocketChannel.open();
            this.server.bind(new InetSocketAddress("127.0.0.1", LOGGER_PORT));
            this.server.configureBlocking(false);

            this.selector = Selector.open();
            this.server.register(this.selector, this.server.validOps(), null);

            API.getLogger().info("Started LoggerListener on " + LOGGER_PORT);
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Failed to start LoggerListener!", e);
        }
    }

    public void safelyStop()
    {
        this.working = false;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("working", this.working).append("server", this.server).append("selector", this.selector).toString();
    }
}

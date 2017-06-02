package pl.north93.zgame.daemon.servers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import org.diorite.utils.DioriteUtils;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.PostInject;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.utils.JavaArguments;

/**
 * Klasa demona zarządzająca uruchomionymi serwerami
 * i odpowiadająca za uruchamianie nowych.
 */
public class ServersManager
{
    private final ApiCore                   apiCore    = null;
    private final NetworkControllerRpc      controller = API.getNetworkManager().getNetworkController();
    private final File                      workspace  = API.getFile("workspace");
    private final File                      engines    = API.getFile("engines");
    private final File                      patterns   = API.getFile("patterns");
    private final ServersWatchdog           watchdog   = new ServersWatchdog();
    private final Logger                    outputLog  = Logger.getLogger("Servers");
    private final Map<UUID, ServerInstance> servers    = new HashMap<>();

    @PostInject
    private void postInject()
    {

    }

    public void startServerManager()
    {
        DioriteUtils.createDirectory(this.workspace);
        DioriteUtils.createDirectory(this.engines);
        DioriteUtils.createDirectory(this.patterns);
        this.outputLog.setParent(API.getLogger());
        this.watchdog.start();
    }

    public void stopServerManager()
    {
        this.servers.values().forEach(serverInstance -> serverInstance.getServerConsole().executeCommand("stop"));
        this.watchdog.safeStop();
    }

    public Logger getServersLogger()
    {
        return this.outputLog;
    }

    public ServersWatchdog getWatchdog()
    {
        return this.watchdog;
    }

    public ServerInstance getServer(final UUID serverUuid)
    {
        return this.servers.get(serverUuid);
    }

    public void removeServer(final UUID serverUuid)
    {
        final ServerInstance instance = this.getServer(serverUuid);
        if (instance.getServerConsole() != null)
        {
            throw new IllegalStateException("Tried to remove server with connected console!");
        }
        try
        {
            FileUtils.deleteDirectory(instance.getWorkspace()); // remove server workspace
        }
        catch (final IOException e)
        {
            e.printStackTrace(); // TODO ?
        }
        this.servers.remove(serverUuid); // remove server from internal daemon list
        this.controller.removeServer(serverUuid); // inform network controller about removing server
        API.getLogger().info("Removed server with UUID: " + serverUuid);
    }

    public void deployNewServer(final UUID serverId, final String serverTemplate)
    {
        API.getLogger().info("Deploying new server with id " + serverId + " and template " + serverTemplate);
        this.controller.updateServerState(serverId, ServerState.INSTALLING);
        final ServerPattern pattern = API.getNetworkManager().getServers().getServerPattern(serverTemplate);

        final File serverWorkspace = new File(this.workspace, serverId.toString());
        this.setupWorkspace(serverWorkspace, pattern);

        final JavaArguments java = new JavaArguments();
        java.setJar(this.getEngineFile(pattern.getEngineName()));
        java.addJavaArg("XX:+UnlockExperimentalVMOptions"); // aikars
        java.addJavaArg("XX:+AlwaysPreTouch");
        java.addJavaArg("XX:+UseG1GC");
        java.addJavaArg("XX:+UseStringDeduplication");
        java.addJavaArg("XX:MaxGCPauseMillis=40"); // tick time=50ms
        java.addJavaArg("XX:TargetSurvivorRatio=90"); // aikars
        java.addJavaArg("XX:G1NewSizePercent=50"); // aikars=50
        java.addJavaArg("XX:+AggressiveOpts");
        java.addJavaArg("XX:InlineSmallCode=2048"); // increase max code size to inline. Default=1000
        java.addJavaArg("XX:MaxInlineSize=70"); // Default=35
        java.addJavaArg("XX:MaxTrivialSize=12"); // Default=6
        java.addEnvVar("jline.terminal", "jline.UnsupportedTerminal"); // Disable fancy terminal
        java.addEnvVar("northplatform.serverid", serverId.toString());
        java.setStartHeapSize(pattern.getStartMemory());
        java.setMaxHeapSize(pattern.getMaxMemory());

        final ServerInstance serverInstance = new ServerInstance(this, serverId, serverWorkspace, java);
        this.servers.put(serverId, serverInstance);
        try
        {
            ServerConsole.createServerProcess(this, serverInstance);
        }
        catch (final Exception e)
        {
            API.getLogger().log(Level.SEVERE, "Failed to createServerProcess", e);
            this.servers.remove(serverId); // remove server from list
            this.controller.updateServerState(serverId, ServerState.ERROR);
            return;
        }
        this.controller.updateServerState(serverId, ServerState.STARTING);
    }

    public String getEngineFile(final String engineName)
    {
        return new File(this.engines, engineName).getAbsolutePath();
    }

    private void setupWorkspace(final File workspace, final ServerPattern pattern)
    {
        API.getLogger().info("Setting up workspace: " + workspace);
        workspace.mkdir();
        for (final String component : pattern.getComponents())
        {
            try
            {
                FileUtils.copyDirectory(new File(this.patterns, component), workspace);
            }
            catch (final IOException e)
            {
                API.getLogger().log(Level.SEVERE, "Exception while setting up workspace", e);
            }
        }
    }
}

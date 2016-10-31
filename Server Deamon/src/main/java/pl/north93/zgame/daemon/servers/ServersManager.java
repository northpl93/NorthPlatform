package pl.north93.zgame.daemon.servers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pl.north93.zgame.api.global.API;
import pl.north93.zgame.api.global.deployment.ServerPattern;
import pl.north93.zgame.api.global.network.NetworkControllerRpc;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.utils.JavaArguments;

public class ServersManager
{
    private final NetworkControllerRpc      controller = API.getNetworkManager().getNetworkController();
    private final File                      workspace  = API.getFile("workspace");
    private final File                      engines    = API.getFile("engines");
    private final File                      patterns   = API.getFile("patterns");
    private final LoggerServer              logger     = new LoggerServer();
    private final Map<UUID, ServerInstance> servers    = new HashMap<>();

    public void startServerManager()
    {
        this.logger.start(); // Start logger server
    }

    public void stopServerManager()
    {
        this.logger.safelyStop(); // Safely stop logger server
    }

    public void deployNewServer(final UUID serverId, final String serverTemplate)
    {
        API.getLogger().info("Deploying new server with id " + serverId + " and template " + serverTemplate);
        this.controller.updateServerState(serverId, ServerState.INSTALLING);
        final ServerPattern pattern = API.getNetworkManager().getServerPattern(serverTemplate);

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
        java.addEnvVar("northplatform.serverid", serverId.toString());

        final ServerInstance serverInstance = new ServerInstance(serverId, serverWorkspace, java);
        this.servers.put(serverId, serverInstance);
        ServerConsole.createServerProcess(serverInstance);
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
    }

    private void startServerProcess(final ServerInstance serverInstance)
    {

    }
}

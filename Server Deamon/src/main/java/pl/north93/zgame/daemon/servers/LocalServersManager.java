package pl.north93.zgame.daemon.servers;

import static pl.north93.zgame.api.global.utils.CollectionUtils.findInCollection;


import javax.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.config.AutoScalingConfig;
import pl.north93.zgame.api.global.network.daemon.config.ServerPatternConfig;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.utils.JavaArguments;
import pl.north93.zgame.daemon.event.ServerCreatingEvent;
import pl.north93.zgame.daemon.event.ServerExitedEvent;

/**
 * Menadzer lokalnie uruchomionych serwerow.
 */
public class LocalServersManager
{
    @Inject
    private Logger                     logger;
    @Inject
    private FilesManager               filesManager;
    @Inject
    private PortManagement             portManagement;
    @Inject
    private INetworkManager            networkManager;
    private EventBus                   eventBus;
    @Inject @NetConfig(type = AutoScalingConfig.class, id = "autoscaler")
    private IConfig<AutoScalingConfig> config;
    private final Map<UUID, LocalServerInstance> instances = new HashMap<>();

    @Bean
    private LocalServersManager(final @Named("daemon") EventBus eventBus)
    {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public @Nullable LocalServerInstance getInstance(final UUID serverId)
    {
        synchronized (this.instances)
        {
            return this.instances.get(serverId);
        }
    }

    public ServerPatternConfig getPattern(final String patternId)
    {
        final AutoScalingConfig autoScalingConfig = this.config.get();
        return findInCollection(autoScalingConfig.getPatterns(), ServerPatternConfig::getPatternName, patternId);
    }

    /**
     * Zwraca skopiowana liste aktualnie uruchomionych instancji.
     *
     * @return lista uruchomionych instancji.
     */
    public Collection<LocalServerInstance> getInstances()
    {
        return new ArrayList<>(this.instances.values());
    }

    @Subscribe
    public void onServerExited(final ServerExitedEvent event)
    {
        final Value<ServerDto> serverValue = event.getInstance().getServerDto();
        final ServerDto server = serverValue.get();

        this.logger.log(Level.INFO, "Server {0} exited", server.getUuid());

        synchronized (this.instances)
        {
            this.instances.remove(server.getUuid());
        }
        serverValue.delete();

        // zwracamy port uzywany przez serwer do puli
        this.portManagement.returnPort(server.getConnectPort());

        // todo notify bungeecords about deleted server
    }

    public void deployServer(final UUID serverId, final String patternId)
    {
        this.logger.log(Level.INFO, "Deploying server {0} with pattern {1}", new Object[]{serverId, patternId});

        final ServerPatternConfig pattern = this.getPattern(patternId);
        final File workspace = this.filesManager.getWorkspace(serverId);

        final JavaArguments java = new JavaArguments();
        java.setJar(this.filesManager.getEngineFile(pattern.getEngineName()).getAbsolutePath());
        java.addJavaArg("XX:+UnlockExperimentalVMOptions"); // aikars
        java.addJavaArg("XX:+AlwaysPreTouch"); // na starcie alokuje pamiec w systemie, zapobiega wpadkom
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
        java.addProgramVar("--server-name " + serverId);
        java.setStartHeapSize(pattern.getStartMemory());
        java.setMaxHeapSize(pattern.getMaxMemory());

        final Value<ServerDto> serverDto = this.networkManager.getServers().unsafe().getServerDto(serverId);
        this.portManagement.setupNetwork(serverDto, java);

        this.eventBus.post(new ServerCreatingEvent(workspace, pattern, java, serverDto));

        // aktualizacja stanu serwera
        serverDto.update((Consumer<ServerDto>) dto -> dto.setServerState(ServerState.STARTING));

        final LocalServerInstance instance = new LocalServerInstance(serverDto, workspace, java);
        synchronized (this.instances)
        {
            this.instances.put(serverId, instance);
        }

        this.logger.log(Level.INFO, "Deployment operation of {0} completed.", serverId);
    }
}
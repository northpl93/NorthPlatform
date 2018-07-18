package pl.north93.zgame.daemon.servers;

import static pl.north93.zgame.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.config.AutoScalingConfig;
import pl.north93.zgame.api.global.network.daemon.config.ServerPatternConfig;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.api.global.utils.JavaArguments;
import pl.north93.zgame.daemon.event.ServerCreatingEvent;
import pl.north93.zgame.daemon.event.ServerDeathEvent;
import pl.north93.zgame.daemon.event.ServerExitedEvent;

/**
 * Menadzer lokalnie uruchomionych serwerow.
 */
public class LocalServersManager
{
    private final Logger logger = LoggerFactory.getLogger(LocalServersManager.class);
    @Inject
    private ApiCore                    apiCore;
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

    public @Nullable ServerPatternConfig getPattern(final String patternId)
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

    /**
     * Planuje asynchroniczny deployment serwera o podanym ID.
     *
     * @param serverId UUID serwera do deploymentu.
     * @param patternId Identyfikator wzoru serwera.
     * @return CompletableFuture ktre zakończy się gdy serwer zaczniac
     */
    public CompletableFuture<Server> scheduleServerDeployment(final UUID serverId, final String patternId)
    {
        this.logger.info("Deploying server {} with pattern {}", serverId, patternId);

        final CompletableFuture<Server> future = new CompletableFuture<>();
        this.apiCore.getPlatformConnector().runTaskAsynchronously(() ->
        {
            future.complete(this.deployServer(serverId, patternId));
            this.logger.info("Deployment operation of {} completed.", serverId);
        });

        return future;
    }

    @Subscribe
    public void onServerExited(final ServerExitedEvent event)
    {
        final Value<ServerDto> serverValue = event.getInstance().getServerDto();

        final ServerDto server = serverValue.get();
        if (server.getServerState() != ServerState.STOPPING)
        {
            this.eventBus.post(new ServerDeathEvent(server));
        }

        this.logger.info("Server {} exited", server.getUuid());
        synchronized (this.instances)
        {
            this.instances.remove(server.getUuid());
        }
        serverValue.delete();

        // informujemy bungeecordy o usunietym serwerze
        // to powinno byc bezpieczne bo serwer juz nie dziala wiec sila rzeczy graczy na nim nie ma
        this.networkManager.getProxies().removeServer(server);

        // zwracamy port uzywany przez serwer do puli
        this.portManagement.returnPort(server.getConnectPort());
    }

    private Server deployServer(final UUID serverId, final String patternId)
    {
        final ServerPatternConfig pattern = this.getPattern(patternId);
        final File workspace = this.filesManager.getWorkspace(serverId);

        final JavaArguments java = new JavaArguments();
        this.setupJavaOptimizations(java);

        java.setJar(this.filesManager.getEngineFile(pattern.getEngineName()).getAbsolutePath());
        java.addJavaArg("XX:+AlwaysPreTouch"); // na starcie alokuje pamiec w systemie, zapobiega wpadkom
        java.addEnvVar("jline.terminal", "jline.UnsupportedTerminal"); // Disable fancy terminal
        java.addEnvVar("northplatform.serverid", serverId.toString());
        java.addProgramVar("--server-name " + serverId);
        java.setStartHeapSize(pattern.getStartMemory());
        java.setMaxHeapSize(pattern.getMaxMemory());

        final Value<ServerDto> serverDto = this.networkManager.getServers().unsafe().getServerDto(serverId);
        this.portManagement.setupNetwork(serverDto, java);

        // wywolujemy event tworzenia serwera i cala pozostala konfiguracje
        this.eventBus.post(new ServerCreatingEvent(workspace, pattern, java, serverDto));

        // aktualizacja stanu serwera
        serverDto.update((Consumer<ServerDto>) dto -> dto.setServerState(ServerState.STARTING));

        final LocalServerInstance instance = new LocalServerInstance(serverDto, workspace, java, pattern);
        synchronized (this.instances)
        {
            this.instances.put(serverId, instance);
        }

        // wysylamy do wszystkich bungeecordow info o nowym serwerze,
        // bo juz nic nie rzuci wyjatku i juz mamy skonfigurowany networking
        this.networkManager.getProxies().addServer(serverDto.get());

        return serverDto.get();
    }

    private void setupJavaOptimizations(final JavaArguments java)
    {
        java.addJavaArg("XX:+UnlockExperimentalVMOptions"); // aikars

        // = = = GARBAGE COLLECTOR = = = //
        java.addJavaArg("XX:+UseG1GC");
        java.addJavaArg("XX:+UseStringDeduplication");
        java.addJavaArg("XX:MaxGCPauseMillis=50"); // tick time=50ms, default is 200
        java.addJavaArg("XX:G1NewSizePercent=30"); // default 5, minecraft alokuje bardzo dużo obiektów
        java.addJavaArg("XX:G1MaxNewSizePercent=60"); // default 60

        // raczej nigdy nie zaalokujemy wystarczająco dużego heapu (32GB?)
        java.addJavaArg("XX:+UseCompressedClassPointers");
        java.addJavaArg("XX:+UseCompressedOops");

        java.addJavaArg("XX:MaxMetaspaceSize=128m");

        // = = = OGÓLNE = = = //
        java.addJavaArg("XX:+AggressiveOpts");
        java.addJavaArg("XX:InlineSmallCode=2048"); // increase max code size to inline. Default=1000
        java.addJavaArg("XX:MaxInlineSize=70"); // Default=35
        java.addJavaArg("XX:MaxTrivialSize=12"); // Default=6
    }
}

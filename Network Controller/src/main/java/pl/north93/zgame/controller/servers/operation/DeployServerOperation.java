package pl.north93.zgame.controller.servers.operation;

import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.daemon.DaemonDto;
import pl.north93.zgame.api.global.network.daemon.DaemonRpc;
import pl.north93.zgame.api.global.network.daemon.config.ServerPatternConfig;
import pl.north93.zgame.api.global.network.impl.ServerDto;
import pl.north93.zgame.api.global.network.server.Server;
import pl.north93.zgame.api.global.network.server.ServerState;
import pl.north93.zgame.api.global.network.server.group.ServersGroupDto;
import pl.north93.zgame.api.global.redis.observable.Value;
import pl.north93.zgame.controller.servers.groups.LocalGroupsManager;
import pl.north93.zgame.controller.servers.groups.LocalManagedServersGroup;

public class DeployServerOperation extends AutoScalerOperation
{
    private final LocalManagedServersGroup serversGroup;
    private Value<ServerDto>   ourServer;
    @Inject
    private INetworkManager    networkManager;
    @Inject
    private LocalGroupsManager localGroupsManager;

    public DeployServerOperation(final LocalManagedServersGroup serversGroup)
    {
        this.serversGroup = serversGroup;
    }

    @Override
    protected boolean startOperation()
    {
        final UUID serverId = UUID.randomUUID();
        final ServersGroupDto group = this.serversGroup.getAsDto();

        final String pattern = this.serversGroup.getConfig().getPattern();
        final ServerPatternConfig patternConfig = this.localGroupsManager.getServerPatternConfig(pattern);
        if (patternConfig == null)
        {
            // brak patternu o takim ID, prawdopodobnie blad w konfiguracji.
            // anulujemy tworzenie serwera zeby w daemonie nie walilo bledami
            return false;
        }

        final DaemonDto bestDaemon = this.getBestDaemon(patternConfig);
        if (bestDaemon == null)
        {
            // brak demona do deploymentu
            return false;
        }

        final ServerDto serverDto = new ServerDto(serverId, true, group.getServersType(), ServerState.CREATING, group.getJoiningPolicy(), "", 0, group);
        // uploadujemy dane serwera do redisa i przypisujemy wartosc serwera
        this.ourServer = this.uploadServer(serverDto);

        final DaemonRpc daemonRpc = this.networkManager.getDaemons().getRpc(bestDaemon);
        // wysylamy do demona polecenie, daemon zadba o to zeby dodac serwer do bungeecordów
        daemonRpc.deployServer(serverId, pattern);

        return true;
    }

    @Override
    protected boolean cancel()
    {
        // na razie nie implementujemy mozliwosci anulowania operacji deployu
        return false;
    }

    @Override
    protected boolean isOpposite(final AutoScalerOperation operation)
    {
        return ! (operation instanceof DeployServerOperation);
    }

    @Override
    protected ScalerOperationState checkState()
    {
        final Server server = this.ourServer.get();
        if (server == null)
        {
            return ScalerOperationState.FAILED;
        }

        if (this.isServerFullyStarted(server))
        {
            return ScalerOperationState.COMPLETED;
        }

        return ScalerOperationState.IN_PROGRESS;
    }

    // pobiera najmniej obciazony daemon który moze uruchomic nowa instancje danego patternu,
    // lub null jak nie znajdzie takiego
    private DaemonDto getBestDaemon(final ServerPatternConfig patternConfig)
    {
        final Set<DaemonDto> daemons = this.networkManager.getDaemons().all();
        return daemons.stream()
                      .filter(daemonDto -> this.isDaemonCapable(daemonDto, patternConfig))
                      .min(new DaemonComparator())
                      .orElse(null);
    }

    // sprawdza czy podany daemon moze uruchomic instancje o podanym patternie
    private boolean isDaemonCapable(final DaemonDto daemon, final ServerPatternConfig patternConfig)
    {
        final int freeRam = daemon.getMaxRam() - daemon.getRamUsed();
        return daemon.isAcceptingServers() && freeRam >= patternConfig.getMaxMemory();
    }

    private Value<ServerDto> uploadServer(final ServerDto serverDto)
    {
        final Value<ServerDto> value = this.networkManager.getServers().unsafe().getServerDto(serverDto.getUuid());
        value.set(serverDto);
        return value;
    }

    private boolean isServerFullyStarted(final Server server)
    {
        final ServerState state = server.getServerState();
        return state == ServerState.WORKING || state == ServerState.STOPPING || state == ServerState.STOPPED;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("serversGroup", this.serversGroup.getName()).append("ourServer", this.ourServer).toString();
    }
}

class DaemonComparator implements Comparator<DaemonDto>
{
    @Override
    public int compare(final DaemonDto o1, final DaemonDto o2)
    {
        final double d1RamPct = (double) o1.getRamUsed() / o1.getMaxRam() * 100D;
        final double d2RamPct = (double) o2.getRamUsed() / o2.getMaxRam() * 100D;

        return (int) (d1RamPct - d2RamPct);
    }
}
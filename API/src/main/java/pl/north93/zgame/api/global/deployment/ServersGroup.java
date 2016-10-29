package pl.north93.zgame.api.global.deployment;

import org.diorite.cfg.annotations.CfgComment;

import pl.north93.zgame.api.global.network.JoiningPolicy;

/**
 * Reprezentuje grupę serwerów mogących pracować na różnych demonach.
 */
public class ServersGroup
{
    @CfgComment("Nazwa tej grupy serwerów")
    private String               name;
    @CfgComment("Nazwa wzoru według którego mają być tworzone instancje serwerów dla tej grupy")
    private String               serverPattern;
    @CfgComment("Strategia alokacji serwerów. Dostępne: PLAYER_COUNT, JOINING_POLICY")
    private ServersAllocatorType allocatorType;
    @CfgComment("Uprawnienia dostępu do tej grupy serwerów")
    private JoiningPolicy        joiningPolicy;
    @CfgComment("Minimalna ilość serwerów")
    private Integer              minServers;
    @CfgComment("Maksymalna ilość serwerów")
    private Integer              maxServers;

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getServerPattern()
    {
        return this.serverPattern;
    }

    public void setServerPattern(final String serverPattern)
    {
        this.serverPattern = serverPattern;
    }

    public ServersAllocatorType getAllocatorType()
    {
        return this.allocatorType;
    }

    public void setAllocatorType(final ServersAllocatorType allocatorType)
    {
        this.allocatorType = allocatorType;
    }

    public JoiningPolicy getJoiningPolicy()
    {
        return this.joiningPolicy;
    }

    public void setJoiningPolicy(final JoiningPolicy joiningPolicy)
    {
        this.joiningPolicy = joiningPolicy;
    }

    public Integer getMinServers()
    {
        return this.minServers;
    }

    public void setMinServers(final Integer minServers)
    {
        this.minServers = minServers;
    }

    public Integer getMaxServers()
    {
        return this.maxServers;
    }

    public void setMaxServers(final Integer maxServers)
    {
        this.maxServers = maxServers;
    }
}

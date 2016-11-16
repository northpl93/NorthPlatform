package pl.north93.zgame.api.global.deployment;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

@CfgComment("Konfiguracja alokatora serwerów")
public class AllocationConfiguration
{
    @CfgComment("Strategia alokacji serwerów. Dostępne: STATIC, PLAYER_COUNT, JOINING_POLICY")
    private ServersAllocatorType allocatorType;

    @CfgComment("Czy zezwalać alokatorowi na usuwania serwerów")
    private Boolean allowDeallocate;

    @CfgComment("Minimalna ilość serwerów utrzymywana w sieci")
    private Integer minServers;

    @CfgComment("Maksymalna ilość serwerów utrzymywanych w sieci")
    private Integer maxServers;

    public ServersAllocatorType getAllocatorType()
    {
        return this.allocatorType;
    }

    public void setAllocatorType(final ServersAllocatorType allocatorType)
    {
        this.allocatorType = allocatorType;
    }

    public Boolean getAllowDeallocate()
    {
        return this.allowDeallocate;
    }

    public void setAllowDeallocate(final Boolean allowDeallocate)
    {
        this.allowDeallocate = allowDeallocate;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("allocatorType", this.allocatorType).append("allowDeallocate", this.allowDeallocate).append("minServers", this.minServers).append("maxServers", this.maxServers).toString();
    }
}

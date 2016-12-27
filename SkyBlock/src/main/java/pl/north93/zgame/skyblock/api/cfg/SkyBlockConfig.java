package pl.north93.zgame.skyblock.api.cfg;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.cfg.annotations.CfgComment;

@CfgComment("Konfiguracja SkyBlocka")
public class SkyBlockConfig
{
    @CfgComment("Unikalne identyfikatory serwerów używanych jako hosty SkyBlocka")
    private List<UUID>         skyBlockServers;

    private List<IslandConfig> islandTypes;

    public List<UUID> getSkyBlockServers()
    {
        return this.skyBlockServers;
    }

    public List<IslandConfig> getIslandTypes()
    {
        return this.islandTypes;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("skyBlockServers", this.skyBlockServers).append("islandTypes", this.islandTypes).toString();
    }
}

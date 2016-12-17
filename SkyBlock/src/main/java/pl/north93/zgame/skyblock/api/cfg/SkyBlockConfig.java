package pl.north93.zgame.skyblock.api.cfg;

import java.util.List;
import java.util.UUID;

import org.diorite.cfg.annotations.CfgComment;

@CfgComment("Konfiguracja SkyBlocka")
public class SkyBlockConfig
{
    @CfgComment("Unikalne identyfikatory serwerów używanych jako hosty SkyBlocka")
    private List<UUID>         skyBlockServers;

    private List<IslandConfig> islandTypes;
}

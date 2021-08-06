package pl.north93.northplatform.minigame.bedwars;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.bukkit.utils.dmgtracker.DamageTracker;
import pl.north93.northplatform.api.global.ApiCore;
import pl.north93.northplatform.api.global.component.Component;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.component.annotations.bean.Named;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.minigame.bedwars.cfg.BwConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwShopConfig;

public class BedWarsComponent extends Component
{
    @Inject
    private IBukkitServerManager serverManager;

    @Override
    protected void enableComponent()
    {
        DamageTracker.get(); // make sure that DamageTracker is ready.
    }

    @Override
    protected void disableComponent()
    {
    }

    @Bean
    private BwConfig bedWarsConfig(final ApiCore api)
    {
        return JaxbUtils.unmarshal(api.getFile("MiniGame.BedWars.xml"), BwConfig.class);
    }

    @Bean
    private BwShopConfig bedWarsShopConfig(final ApiCore api)
    {
        return JaxbUtils.unmarshal(api.getFile("MiniGame.BedWars.Shop.xml"), BwShopConfig.class);
    }

    @Bean @Named("BedWarsNpcRegistry")
    private NPCRegistry bedWarsNpcRegistry()
    {
        return CitizensAPI.createNamedNPCRegistry("bedwars", new MemoryNPCDataStore());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

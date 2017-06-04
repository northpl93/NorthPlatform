package pl.north93.zgame.skyblock.bungee;

import static pl.north93.zgame.api.global.redis.rpc.Targets.networkController;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.IncludeInScanning;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.redis.rpc.IRpcManager;
import pl.north93.zgame.skyblock.shared.api.IIslandsRanking;
import pl.north93.zgame.skyblock.shared.api.ISkyBlockManager;
import pl.north93.zgame.skyblock.shared.api.cfg.SkyBlockConfig;
import pl.north93.zgame.skyblock.shared.impl.IslandDao;
import pl.north93.zgame.skyblock.shared.impl.IslandsRankingImpl;

@IncludeInScanning("pl.north93.zgame.skyblock.shared")
public class SkyBlockBungee extends Component
{
    @Inject
    private IRpcManager     rpcManager;
    private SkyBlockConfig  config;
    private IIslandsRanking ranking;
    private IslandDao       islandDao;

    @Override
    protected void enableComponent()
    {
        this.config = this.rpcManager.createRpcProxy(ISkyBlockManager.class, networkController()).getConfig();
        this.islandDao = new IslandDao();
        this.ranking = new IslandsRankingImpl();
    }

    @Override
    protected void disableComponent()
    {
    }

    public SkyBlockConfig getConfig()
    {
        return this.config;
    }

    public IslandDao getIslandDao()
    {
        return this.islandDao;
    }

    public IIslandsRanking getRanking()
    {
        return this.ranking;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

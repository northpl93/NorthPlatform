package pl.north93.northplatform.api.minigame.shared.impl.booster;

import static pl.north93.northplatform.api.global.utils.lang.CollectionUtils.findInCollection;


import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.minigame.shared.api.booster.IBooster;
import pl.north93.northplatform.api.minigame.shared.api.booster.IBoosterManager;
import pl.north93.northplatform.api.minigame.shared.impl.booster.cfg.BoosterEntryCfg;
import pl.north93.northplatform.api.minigame.shared.impl.booster.cfg.BoostersCfg;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.config.IConfig;
import pl.north93.northplatform.api.global.config.NetConfig;
import pl.north93.northplatform.api.global.network.players.IPlayer;

/*default*/ class BoosterManagerImpl implements IBoosterManager
{
    private final List<IBooster> boosters = new ArrayList<>();
    @Inject @NetConfig(type = BoostersCfg.class, id="boosters")
    private IConfig<BoostersCfg> boostersCfg;

    @Bean
    private BoosterManagerImpl()
    {
    }

    @Aggregator(IBooster.class)
    private void registerBooster(final IBooster booster)
    {
        this.boosters.add(booster);
    }

    @Override
    public Collection<IBooster> getBoosters()
    {
        return Collections.unmodifiableCollection(this.boosters);
    }

    @Override
    public Collection<IBooster> getEnabledBoosters()
    {
        return this.boosters.stream().filter(this::isBoosterEnabled).collect(Collectors.toList());
    }

    @Override
    public boolean isBoosterEnabled(final IBooster booster)
    {
        final BoosterEntryCfg config = this.getConfig(booster);
        if (config == null)
        {
            return true;
        }
        return config.getEnabled();
    }

    @Override
    public Collection<IBooster> getValidBoosters(final IPlayer player)
    {
        final Predicate<IBooster> predicate = booster -> booster.isBoosterValid(player);
        return this.boosters.stream().filter(this::isBoosterEnabled).filter(predicate).collect(Collectors.toList());
    }

    @Nullable
    private BoosterEntryCfg getConfig(final IBooster booster)
    {
        final BoostersCfg boostersCfg = this.boostersCfg.get();
        if ( boostersCfg == null )
        {
            return null;
        }
        
        return findInCollection(boostersCfg.getBoosters(), BoosterEntryCfg::getId, booster.getId());
    }

    @Override
    public double calculateFinalMultiplier(final IPlayer player)
    {
        final Collection<IBooster> boosters = this.getValidBoosters(player);
        return boosters.stream().mapToDouble(booster -> booster.getMultiplier(player)).sum();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("boosters", this.boosters).append("boostersCfg", this.boostersCfg).toString();
    }
}

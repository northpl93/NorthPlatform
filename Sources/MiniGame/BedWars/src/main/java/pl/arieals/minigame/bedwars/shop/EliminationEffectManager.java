package pl.arieals.minigame.bedwars.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.IPlayerContainer;
import pl.arieals.globalshops.server.domain.Item;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.arieals.minigame.bedwars.shop.elimination.IEliminationEffect;
import pl.north93.zgame.api.global.component.annotations.bean.Aggregator;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class EliminationEffectManager
{
    @Inject
    private IGlobalShops globalShops;
    private Map<String, IEliminationEffect> effectMap = new HashMap<>();

    @Bean
    private EliminationEffectManager()
    {
    }

    @Aggregator(IEliminationEffect.class)
    private void collectEliminationEffects(final IEliminationEffect effect)
    {
        this.effectMap.put(effect.getName(), effect);
    }

    public IEliminationEffect getEffect(final String name)
    {
        return this.effectMap.get(name);
    }

    public IEliminationEffect getEffectOf(final Player player)
    {
        final ItemsGroup group = this.globalShops.getGroup("bedwars_elimination");
        final IPlayerContainer container = this.globalShops.getPlayer(player);

        final Item activeItem = container.getActiveItem(group);
        if (activeItem == null)
        {
            return null;
        }

        return this.getEffect(activeItem.getId());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("effectMap", this.effectMap).toString();
    }
}

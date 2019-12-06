package pl.arieals.minigame.elytrarace.shop;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.entity.Player;
import pl.arieals.minigame.elytrarace.shop.effects.IElytraEffect;
import pl.north93.northplatform.api.global.component.annotations.bean.Aggregator;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.globalshops.server.IGlobalShops;
import pl.north93.northplatform.globalshops.server.IPlayerContainer;
import pl.north93.northplatform.globalshops.server.domain.Item;
import pl.north93.northplatform.globalshops.server.domain.ItemsGroup;

import java.util.HashMap;
import java.util.Map;

public class ElytraEffectsManager
{
    @Inject
    private IGlobalShops globalShops;
    private final Map<String, IElytraEffect> effectsMap = new HashMap<>();

    @Bean
    private ElytraEffectsManager()
    {
    }

    @Aggregator(IElytraEffect.class)
    private void collectElytraEffects(final IElytraEffect effect)
    {
        this.effectsMap.put(effect.name(), effect);
    }

    public IElytraEffect getEffect(final String name)
    {
        return this.effectsMap.get(name);
    }

    public IElytraEffect getEffect(final Player player)
    {
        final ItemsGroup group = this.globalShops.getGroup("elytra_effects");
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
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("effectsMap", this.effectsMap).toString();
    }
}

package pl.arieals.api.minigame.server.gamehost.reward.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.reward.IArenaRewards;
import pl.arieals.api.minigame.server.gamehost.reward.IReward;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.players.Identity;

public class ArenaRewardsImpl implements IArenaRewards
{
    private final LocalArena arena;
    private final Multimap<Identity, IReward> rewards = ArrayListMultimap.create();

    public ArenaRewardsImpl(final LocalArena arena)
    {
        this.arena = arena;
    }

    @Override
    public LocalArena getArena()
    {
        return this.arena;
    }

    @Override
    public void addReward(final Identity identity, final IReward reward)
    {
        // todo track reward in database
        this.rewards.put(identity, reward);
        reward.apply(identity);
    }

    @Override
    public Collection<IReward> getRewardsOf(final Identity player)
    {
        return this.rewards.get(player);
    }

    @Override
    public Map<String, List<IReward>> groupRewardsOf(final Identity player)
    {
        return this.getRewardsOf(player).stream().collect(Collectors.groupingBy(IReward::getId));
    }

    @Override
    public void renderRewards(final MessagesBox messagesBox, final Player player)
    {
        final Map<String, List<IReward>> groupedRewards = this.groupRewardsOf(Identity.of(player));
        for (final Map.Entry<String, List<IReward>> entry : groupedRewards.entrySet())
        {
            final List<IReward> values = entry.getValue();
            final IReward.RewardMessageRenderer renderer = values.get(0).getRenderer();

            final BaseComponent component = renderer.composeMessage(messagesBox, Locale.forLanguageTag(player.getLocale()), values);
            player.sendMessage(component);
        }
    }

    @Override
    public void reset()
    {
        this.rewards.clear();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("rewards", this.rewards).toString();
    }
}

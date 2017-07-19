package pl.arieals.api.minigame.server.gamehost.reward.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.reward.IArenaRewards;
import pl.arieals.api.minigame.server.gamehost.reward.IReward;
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
    public Collection<IReward> getRewardsOf(final Player player)
    {
        return this.rewards.get(Identity.of(player));
    }

    @Override
    public Map<String, List<IReward>> groupRewardsOf(final Player player)
    {
        return this.getRewardsOf(player).stream().collect(Collectors.groupingBy(IReward::getId));
    }
}

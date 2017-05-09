package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.entity.Player;

import pl.arieals.api.minigame.shared.api.MapTemplate;

public class MapVote
{
    private final Map<Player, MapTemplate> votes = new WeakHashMap<>();
    private MapTemplate[] options;

    public void startVote(final List<MapTemplate> options)
    {
        this.votes.clear();
        this.options = options.toArray(new MapTemplate[options.size()]);
    }

    public void removeVote(final Player player)
    {
        this.votes.remove(player);
    }

    public boolean vote(final Player player, final int option)
    {
        if (this.options == null || option > this.options.length)
        {
            return false;
        }
        final MapTemplate selectedMap = this.options[option];

        this.votes.put(player, selectedMap);
        return true;
    }

    public MapTemplate[] getOptions()
    {
        return this.options;
    }

    public void resetVoting()
    {
        this.options = null;
        this.votes.clear();
    }

    public Map<MapTemplate, Integer> getResults()
    {
        final Map<MapTemplate, Integer> results = new HashMap<>();
        for (final Map.Entry<Player, MapTemplate> entry : this.votes.entrySet())
        {
            final MapTemplate map = entry.getValue();
            results.put(map, results.getOrDefault(map, 0) + 1);
        }
        return results;
    }

    public MapTemplate getWinner()
    {
        MapTemplate winner = null;
        int winnerPoints = 0;
        for (final Map.Entry<MapTemplate, Integer> entry : this.getResults().entrySet())
        {
            if (winner == null || winnerPoints < entry.getValue())
            {
                winner = entry.getKey();
                winnerPoints = entry.getValue();
            }
        }
        return winner;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("votes", this.votes).append("options", this.options).toString();
    }
}

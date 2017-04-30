package pl.arieals.api.minigame.server.gamehost.arena;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.GameMap;

public class MapVote
{
    private final Map<Player, GameMap> votes = new WeakHashMap<>();
    private GameMap[] options;

    public void startVote(final List<GameMap> options)
    {
        this.votes.clear();
        this.options = options.toArray(new GameMap[options.size()]);
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
        final GameMap selectedMap = this.options[option];

        this.votes.put(player, selectedMap);
        return true;
    }

    public GameMap[] getOptions()
    {
        return this.options;
    }

    public void resetVoting()
    {
        this.options = null;
        this.votes.clear();
    }

    public Map<GameMap, Integer> getResults()
    {
        final Map<GameMap, Integer> results = new HashMap<>();
        for (final Map.Entry<Player, GameMap> entry : this.votes.entrySet())
        {
            final GameMap map = entry.getValue();
            results.put(map, results.getOrDefault(map, 0) + 1);
        }
        return results;
    }

    public GameMap getWinner()
    {
        GameMap winner = null;
        int winnerPoints = 0;
        for (final Map.Entry<GameMap, Integer> entry : this.getResults().entrySet())
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

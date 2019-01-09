package pl.north93.northplatform.api.bukkit.scoreboard;

import org.bukkit.entity.Player;

public interface IScoreboardManager
{
    IScoreboardContext setLayout(Player player, IScoreboardLayout layout);

    IScoreboardContext getContext(Player player);

    void removeScoreboard(Player player);
}

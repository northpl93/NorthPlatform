package pl.north93.zgame.antycheat.analysis.reaction;

import org.bukkit.entity.Player;

public interface ITriggerListener
{
    void onTriggered(Player player);

    void onUnTriggered(Player player);
}

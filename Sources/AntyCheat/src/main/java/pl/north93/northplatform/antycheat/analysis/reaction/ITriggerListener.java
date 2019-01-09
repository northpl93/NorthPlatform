package pl.north93.northplatform.antycheat.analysis.reaction;

import org.bukkit.entity.Player;

public interface ITriggerListener
{
    void onTriggered(Player player);

    void onUnTriggered(Player player);
}

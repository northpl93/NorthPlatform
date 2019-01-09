package pl.north93.northplatform.antycheat.client.monitor.action;

import org.bukkit.entity.Player;

import pl.north93.northplatform.antycheat.analysis.Violation;

public interface IAntyCheatAction
{
    void handle(Player player, Violation violation);
}

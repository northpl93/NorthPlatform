package pl.north93.zgame.antycheat.client.monitor;

import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.Violation;

public interface IAntyCheatAction
{
    void handle(Player player, Violation violation);
}

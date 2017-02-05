package pl.north93.zgame.api.bukkitcommands;


import org.bukkit.Particle;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;

public class HeartCommand extends NorthCommand
{
    public HeartCommand()
    {
        super("heart", "hearts", "love", "sex", "seks", "serce", "serca", "serduszko");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label) {
        final Player player = (Player) sender.unwrapped();
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 2.0, 0), 10, 0, 1.5, 1.5);
    }
}

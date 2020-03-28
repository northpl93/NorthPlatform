package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import org.bukkit.Location;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

public class LightingEffect implements IEliminationEffect
{
    @Override
    public String getName()
    {
        return "lighting";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Location location = player.getLocation();
        location.getWorld().strikeLightningEffect(location);
    }
}

package pl.arieals.minigame.bedwars.shop.elimination;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LightingEffect implements IEliminationEffect
{
    @Override
    public String getName()
    {
        return "lighting";
    }

    @Override
    public void playerEliminated(final Player player, final Player by)
    {
        final Location location = player.getLocation();
        location.getWorld().strikeLightningEffect(location);
    }
}

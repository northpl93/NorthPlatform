package pl.north93.zgame.api.bukkit.map;

import org.bukkit.entity.Player;

public interface IMapRenderer
{
    void render(IMapCanvas canvas, Player player);
}

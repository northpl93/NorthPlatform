package pl.north93.zgame.api.bukkit.map;

import pl.north93.zgame.api.bukkit.player.INorthPlayer;

public interface IMapRenderer
{
    void render(IMapCanvas canvas, INorthPlayer player) throws Exception;
}

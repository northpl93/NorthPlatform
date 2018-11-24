package pl.north93.northplatform.api.bukkit.map;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;

public interface IMapRenderer
{
    void render(IMapCanvas canvas, INorthPlayer player) throws Exception;
}

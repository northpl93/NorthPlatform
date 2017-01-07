package pl.north93.zgame.skyblock.server.gui;

import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.windows.Window;
import pl.north93.zgame.skyblock.server.world.Island;

public class IslandGui extends Window
{
    private final Player player;
    private final Island island;

    public IslandGui(final Player player, final Island island)
    {
        super("&bZarzadzanie wyspa", 45);
        this.player = player;
        this.island = island;
    }

    @Override
    protected void onShow()
    {

    }
}

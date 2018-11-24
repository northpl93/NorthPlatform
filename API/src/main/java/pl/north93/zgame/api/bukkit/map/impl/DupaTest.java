package pl.north93.zgame.api.bukkit.map.impl;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.north93.zgame.api.bukkit.map.IBoard;
import pl.north93.zgame.api.bukkit.map.IMapManager;
import pl.north93.zgame.api.bukkit.map.renderer.SimpleTranslatedRenderer;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;

public class DupaTest extends NorthCommand
{
    @Inject
    private IMapManager mapManager;

    public DupaTest()
    {
        super("testboard");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final Selection selection = worldEditPlugin.getSelection(player);

        if (selection == null)
        {
            sender.sendMessage(ChatColor.RED + "Zaznacz teren mapy worldeditem.");
            return;
        }

        final CuboidRegion cuboidRegion;
        try
        {
            cuboidRegion = (CuboidRegion) selection.getRegionSelector().getRegion();
        }
        catch (IncompleteRegionException e)
        {
            e.printStackTrace();
            return;
        }

        final Location leftCorner = BukkitUtil.toLocation(player.getWorld(), cuboidRegion.getPos1());
        final Location rightCorner = BukkitUtil.toLocation(player.getWorld(), cuboidRegion.getPos2());

        final IBoard board = this.mapManager.createBoard(leftCorner, rightCorner);
        board.setRenderer(new SimpleTranslatedRenderer());
    }
}

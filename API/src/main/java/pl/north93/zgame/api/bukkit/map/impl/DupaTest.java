package pl.north93.zgame.api.bukkit.map.impl;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
import pl.north93.zgame.api.bukkit.map.IMapCanvas;
import pl.north93.zgame.api.bukkit.map.IMapManager;
import pl.north93.zgame.api.bukkit.map.IMapRenderer;
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
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final Selection selection = worldEditPlugin.getSelection(player);

        if (selection == null)
        {
            sender.sendRawMessage(ChatColor.RED + "Zaznacz teren boostera worldeditem.");
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
        board.setRenderer(new IMapRenderer()
        {
            @Override
            public void render(final IMapCanvas canvas, final Player player)
            {
                try
                {
                    Bukkit.broadcastMessage("renderer started");
                    final BufferedImage read = ImageIO.read(new File("C:\\Users\\Michał\\Desktop\\Ranking_demo2.png"));
                    canvas.putImage(0, 0, read);
                    canvas.writeDebugImage(new File("C:\\Users\\Michał\\Desktop\\test.png"));
                    Bukkit.broadcastMessage("renderer ended");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}

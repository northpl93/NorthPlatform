package pl.north93.zgame.skyblock.server.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.RunnableVal2;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.world.World;

import org.bukkit.Location;

public class SchematicUtil
{
    public static void pasteSchematic(final Location location, final File schematic, final Runnable onComplete)
    {
        final World world = new BukkitWorld(location.getWorld());
        final Vector to = new Vector(location.getX(), location.getY(), location.getZ());

        final EditSession paste;
        try
        {
            paste = FaweAPI.load(schematic).paste(world, to, false, false, null);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }

        final AtomicBoolean onCompleteCalled = new AtomicBoolean(false);
        final FaweQueue queue = FaweAPI.createQueue(world, true);
        queue.setProgressTracker(new RunnableVal2<FaweQueue.ProgressType, Integer>()
        {
            @Override
            public void run(final FaweQueue.ProgressType progressType, final Integer integer)
            {
                if (progressType == FaweQueue.ProgressType.DONE && !onCompleteCalled.getAndSet(true))
                {
                    onComplete.run();
                }
            }
        });
        queue.addEditSession(paste);
    }

    public static void createSchematic(final Location l1, final Location l2, final File schematic)
    {
        final Selection selection = new CuboidSelection(l1.getWorld(), l1, l2);

        final ClipboardFormat format = ClipboardFormat.findByAlias("schematic");
        assert format != null;

        try (final FileOutputStream fos = new FileOutputStream(schematic); final ClipboardWriter writer = format.getWriter(fos))
        {
            final Clipboard clipboard = new BlockArrayClipboard(selection.getRegionSelector().getRegion());
            writer.write(clipboard, new BukkitWorld(l1.getWorld()).getWorldData());
        }
        catch (final IOException | IncompleteRegionException e)
        {
            e.printStackTrace();
        }
    }
}

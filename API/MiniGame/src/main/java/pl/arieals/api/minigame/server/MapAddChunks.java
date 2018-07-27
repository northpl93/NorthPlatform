package pl.arieals.api.minigame.server;

import javax.xml.bind.JAXB;

import java.io.File;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.shared.api.cfg.GameMapConfig;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;
import pl.north93.zgame.api.bukkit.utils.xml.XmlChunk;
import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

@Deprecated
public class MapAddChunks extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;
    @Inject
    private MiniGameServer server;

    public MapAddChunks()
    {
        super("mapaddchunks");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();

        File configFile = new File(player.getWorld().getWorldFolder(), "mapconfig.xml");
        
        final GameMapConfig mapConfig = JAXB.unmarshal(configFile, GameMapConfig.class);

        final WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        final Selection selection = worldEditPlugin.getSelection(player);

        if (selection == null)
        {
            player.sendMessage(ChatColor.RED + "Zaznacz teren boostera worldeditem.");
            return;
        }


        final Cuboid cuboid = new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint());
        for (final Chunk chunk : cuboid.getChunks())
        {
            mapConfig.getChunks().add(new XmlChunk(chunk.getX(), chunk.getZ()));
        }

        JAXB.marshal(mapConfig, configFile);
        player.sendMessage(ChatColor.GREEN + "Gotowe! Aktualnie na liscie jest " + mapConfig.getChunks().size() + " chunków!");
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.zgame.skyblock.shop.cmd;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.global.commands.Arguments;
import pl.north93.zgame.api.global.commands.NorthCommand;
import pl.north93.zgame.api.global.commands.NorthCommandSender;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.skyblock.shop.SpawnerComponent;

public class SpawnerCmd extends NorthCommand
{
    @Inject
    private SpawnerComponent spawnerComponent;

    public SpawnerCmd()
    {
        super("spawner");
        this.setPermission("skyblock.spawner");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player unwrapped = (Player) sender.unwrapped();
        final Block block = unwrapped.getTargetBlock((Set<Material>)null, 10);
        if(block.getType() != Material.MOB_SPAWNER)
        {
            sender.sendMessage("&f&l> &7Nie znaleziono spawnera, spojrz sie na niego celownikiem.");
            return;
        }

        this.spawnerComponent.getSpawnerManager().openMobPicker(unwrapped, block);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

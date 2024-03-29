package pl.north93.northplatform.minigame.bedwars.cmd;

import static pl.north93.northplatform.api.minigame.server.gamehost.MiniGameApi.getArena;


import java.io.File;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import pl.north93.northplatform.api.bukkit.utils.xml.XmlLocation;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.utils.JaxbUtils;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsArena;
import pl.north93.northplatform.minigame.bedwars.cfg.BwArenaConfig;
import pl.north93.northplatform.minigame.bedwars.cfg.BwGenerator;

public class BwConfigGenerator extends NorthCommand
{
    public BwConfigGenerator()
    {
        super("bwgenerator");
        this.setPermission("dev");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final Player player = (Player) sender.unwrapped();
        final LocalArena arena = getArena(player);

        final BedWarsArena arenaData = arena.getArenaData();
        final BwArenaConfig config = arenaData.getConfig();

        if (args.isEmpty())
        {
            sender.sendMessage("&aKonfiguracja generatorow. Aktualnie: " + config.getGenerators().size());
            sender.sendMessage("&a/bwgenerator add <type> - dodaje generator w miejscu patrzenia");
            sender.sendMessage("&a/bwgenerator fix - poprawia lokacje generatora (miejsce patrzenia)");
            return;
        }

        if (args.length() == 1 && "fix".equalsIgnoreCase(args.asString(0)))
        {
            final Block targetBlock = player.getTargetBlock(null, 100);
            final Location fixLocation = targetBlock.getLocation().add(0.5, 0, 0.5);

            for (final BwGenerator bwGenerator : config.getGenerators())
            {
                final Location generatorLocation = bwGenerator.getLocation().toBukkit(arena.getWorld().getCurrentWorld());

                if (fixLocation.distanceSquared(generatorLocation) > 3)
                {
                    continue;
                }

                bwGenerator.setLocation(new XmlLocation(fixLocation.getX(), fixLocation.getY(), fixLocation.getZ(), 0, 0));
                sender.sendMessage("&aok, poprawiono generator typu " + bwGenerator.getType());
                this.save(arena, config);
                return;
            }

            sender.sendMessage("&cBrak generatora w poblizu");
            return;
        }

        if (args.length() == 2 && "add".equalsIgnoreCase(args.asString(0)))
        {
            final Block targetBlock = player.getTargetBlock(null, 100);
            final String type = args.asString(1);

            final BwGenerator newGen = new BwGenerator(type, new XmlLocation(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5, 0, 0));
            config.getGenerators().add(newGen);
            sender.sendMessage("&aok, dodano generator typu " + type);
            this.save(arena, config);
            return;
        }

        sender.sendMessage("&cSprawdz argumenty");
    }

    private void save(final LocalArena arena, final BwArenaConfig bwConfig)
    {
        final File mapDirectory = arena.getWorld().getCurrentMapTemplate().getMapDirectory();
        JaxbUtils.marshal(bwConfig, new File(mapDirectory, "BedWarsArena.xml"));
    }
}

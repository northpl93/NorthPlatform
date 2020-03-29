package pl.north93.northplatform.minigame.bedwars.shop.elimination;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;
import pl.north93.northplatform.minigame.bedwars.arena.BedWarsPlayer;
import pl.north93.northplatform.minigame.bedwars.utils.TeamArmorUtils;

public class FlagEffect implements IEliminationEffect
{
    private static final int BANNER_REMOVE = 20 * 5;

    @Override
    public String getName()
    {
        return "flag";
    }

    @Override
    public void playerEliminated(final LocalArena arena, final INorthPlayer player, final INorthPlayer by)
    {
        final Block block = player.getLocation().getBlock();
        block.setType(Material.STANDING_BANNER);

        final BedWarsPlayer playerData = player.getPlayerData(BedWarsPlayer.class);
        if (playerData == null)
        {
            return;
        }

        final Location particle = block.getLocation().add(0.5, 0, 0.5);
        block.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, particle, 5);

        final DyeColor dyeColor = TeamArmorUtils.chatColorToDyeColor(playerData.getTeam().getColor());

        final Banner banner = (Banner) block.getState();
        banner.setBaseColor(DyeColor.BLACK);
        banner.addPattern(new Pattern(dyeColor, PatternType.SKULL));

        banner.update();

        arena.getScheduler().runTaskLater(() ->
        {
            if (block.getType() == Material.STANDING_BANNER)
            {
                block.setType(Material.AIR);
            }
        }, BANNER_REMOVE);
    }
}

package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.bedwars.arena.Team;

public class ToolsHaste implements IUpgrade
{
    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        for (final Player player : team.getPlayers())
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        }
    }

    @Override
    public int maxLevel()
    {
        return 1;
    }
}

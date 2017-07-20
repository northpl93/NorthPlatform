package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.bedwars.arena.Team;

public class Healbot implements IUpgrade
{
    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        final ITrackedRegion healArena = arena.getRegionManager().create(team.getHealArena());

        healArena.whenEnter(player ->
        {
            if (team.getPlayers().equals(player))
            {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
            }
        });

        healArena.whenLeave(player ->
        {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        });
    }

    @Override
    public int maxLevel()
    {
        return 1;
    }
}

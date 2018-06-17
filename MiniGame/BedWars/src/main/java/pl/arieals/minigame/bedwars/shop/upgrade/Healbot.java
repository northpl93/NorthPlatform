package pl.arieals.minigame.bedwars.shop.upgrade;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.region.ITrackedRegion;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.north93.zgame.api.bukkit.utils.region.Cuboid;

public class Healbot implements IUpgrade
{
    @Override
    public void apply(final LocalArena arena, final Team team, final int level)
    {
        // dodajemy efekt graczom którzy juz sa na terenie ulepszenia
        this.addEffectToPlayersInRegion(team);

        // konfigurujemy automatyczne nadawanie ulepszenia
        final ITrackedRegion healArena = arena.getRegionManager().create(team.getHealArena());
        healArena.whenEnter(player ->
        {
            if (team.getPlayers().contains(player))
            {
                this.applyEffect(player);
            }
        });
        healArena.whenLeave(player -> player.removePotionEffect(PotionEffectType.REGENERATION));
    }

    private void addEffectToPlayersInRegion(final Team team)
    {
        final Cuboid healArena = team.getHealArena();
        for (final Player player : team.getPlayers())
        {
            if (healArena.contains(player.getLocation()))
            {
                this.applyEffect(player);
            }
        }
    }

    private void applyEffect(final Player player)
    {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, true, false));
    }

    @Override
    public int maxLevel()
    {
        return 1;
    }
}

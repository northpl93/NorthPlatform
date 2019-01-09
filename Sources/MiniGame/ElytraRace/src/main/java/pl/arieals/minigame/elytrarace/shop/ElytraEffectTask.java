package pl.arieals.minigame.elytrarace.shop;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerData;


import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.arieals.minigame.elytrarace.shop.effects.IElytraEffect;

public class ElytraEffectTask implements Runnable
{
    private final LocalArena arena;

    public ElytraEffectTask(final LocalArena arena)
    {
        this.arena = arena;
    }

    @Override
    public void run()
    {
        for (final Player player : this.arena.getPlayersManager().getPlayers())
        {
            final ElytraRacePlayer playerData = getPlayerData(player, ElytraRacePlayer.class);
            if (playerData == null || !player.isGliding())
            {
                continue;
            }

            final IElytraEffect effect = playerData.getEffect();
            if (effect != null)
            {
                effect.play(player);
            }
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

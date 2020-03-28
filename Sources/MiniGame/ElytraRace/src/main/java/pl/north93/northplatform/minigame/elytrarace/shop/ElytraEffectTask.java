package pl.north93.northplatform.minigame.elytrarace.shop;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.minigame.elytrarace.arena.ElytraRacePlayer;
import pl.north93.northplatform.minigame.elytrarace.shop.effects.IElytraEffect;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.minigame.server.gamehost.arena.LocalArena;

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
        for (final INorthPlayer player : this.arena.getPlayersManager().getPlayers())
        {
            final ElytraRacePlayer playerData = player.getPlayerData(ElytraRacePlayer.class);
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

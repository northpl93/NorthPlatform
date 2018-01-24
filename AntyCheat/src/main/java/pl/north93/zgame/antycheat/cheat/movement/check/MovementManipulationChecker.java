package pl.north93.zgame.antycheat.cheat.movement.check;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.cheat.movement.JumpController;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.utils.block.BlockFlag;

public class MovementManipulationChecker implements EventAnalyser<ClientMoveTimelineEvent>
{

    @Override
    public void configure(final EventAnalyserConfig config)
    {
        config.whitelistEvent(ClientMoveTimelineEvent.class);
    }

    @Override
    public SingleAnalysisResult analyse(final PlayerData data, final PlayerTickInfo tickInfo, final ClientMoveTimelineEvent event)
    {
        // jak trzeba to pomijamy wszystkie checki
        if (shouldSkip(data))
        {
            return null;
        }

        final long flags = event.getTo().getFlags();
        //this.debugFlags(flags);

        final JumpController jumpController = JumpController.get(data);

        if (tickInfo.getProperties().isGliding())
        {
            // lot na elytrze
            jumpController.forceReset();
        }
        else if (BlockFlag.isFlagSet(flags, BlockFlag.COBWEB))
        {
            // pajeczyna
            jumpController.forceReset();
        }
        else if (BlockFlag.isFlagSet(flags, BlockFlag.LIQUID))
        {
            // check movement in liquid
            jumpController.forceReset();
        }
        else if (BlockFlag.isFlagSet(flags, BlockFlag.CLIMBABLE))
        {
            // drabinka/winorosla
            jumpController.forceReset();
        }
        else
        {
            // powietrze
            return jumpController.handleMovement(tickInfo, event);
        }

        return null;
    }

    public static boolean shouldSkip(final PlayerData data)
    {
        final Player player = data.getPlayer();

        final GameMode gameMode = player.getGameMode();
        if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR)
        {
            return true;
        }

        // gdy gracz ma włączone legitne latanie to nic tu nie weryfikujemy
        return player.isFlying();
    }

    private void debugFlags(final long flags)
    {
        if (flags == 0)
        {
            return;
        }

        final String collect = Arrays.stream(BlockFlag.values())
                                     .filter(blockFlag -> BlockFlag.isFlagSet(flags, blockFlag))
                                     .map(Enum::name)
                                     .collect(Collectors.joining(","));
        Bukkit.broadcastMessage(collect);
    }
}

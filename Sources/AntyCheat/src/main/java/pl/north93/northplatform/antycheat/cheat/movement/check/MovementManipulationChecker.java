package pl.north93.northplatform.antycheat.cheat.movement.check;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import pl.north93.northplatform.antycheat.analysis.event.EventAnalyser;
import pl.north93.northplatform.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.northplatform.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.cheat.movement.JumpController;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.utils.block.BlockFlag;

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
        final JumpController jumpController = JumpController.get(data);

        // jak trzeba to pomijamy wszystkie checki
        if (shouldSkip(data))
        {
            // resetujemy kontroler skoku, aby upewnic sie, ze po zejsciu ewentualnych blokad bedzie mial czyste srodowisko pracy
            jumpController.forceReset();

            // nie odpalamy reszty checków
            return null;
        }

        final long flags = event.getTo().getFlags();
        //this.debugFlags(flags);

        if (tickInfo.isGliding())
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

        // gdy gracz może włączyć legitne latanie to nic nie weryfikujemy
        // kiedyś trzeba to przerobić żeby wyłączało weryfikację tylko gdy gracz ma włączone fly
        return player.getAllowFlight();
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

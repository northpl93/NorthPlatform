package pl.north93.zgame.antycheat.cheat.movement.check;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import pl.north93.zgame.antycheat.analysis.SingleAnalysisResult;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyser;
import pl.north93.zgame.antycheat.analysis.event.EventAnalyserConfig;
import pl.north93.zgame.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.zgame.antycheat.timeline.PlayerData;
import pl.north93.zgame.antycheat.timeline.PlayerTickInfo;
import pl.north93.zgame.antycheat.utils.AABB;
import pl.north93.zgame.antycheat.utils.BlockUtils;
import pl.north93.zgame.antycheat.utils.EntityUtils;

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
        final AABB grow = EntityUtils.getAABBOfEntityInLocation(data.getPlayer(), event.getTo()).grow(1, 1, 1);
        Bukkit.broadcastMessage("" + BlockUtils.exactCollides(data.getPlayer().getWorld(), grow, Material.GRASS));

        if (event.isFromOnGround() && ! event.isToOnGround())
        {
            return this.checkTearOffGround(data, event);
            //Bukkit.broadcastMessage("wystartowano z ziemi");
        }

        return null;
    }

    private SingleAnalysisResult checkTearOffGround(final PlayerData playerData, final ClientMoveTimelineEvent event)
    {
        return null;
    }
}

package pl.arieals.skyblock.quests.server.impl.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.skyblock.quests.server.api.IServerQuestsComponent;
import pl.arieals.skyblock.quests.server.api.IServerQuestsManager;
import pl.arieals.skyblock.quests.shared.api.ITrackedStatistic;
import pl.arieals.skyblock.quests.shared.impl.statistics.MobKillStatistic;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;

public class MobKillListener implements Listener
{
    @Inject
    private IServerQuestsComponent serverQuests;

    @EventHandler(ignoreCancelled = true)
    public void bumpMobKillStatistic(final EntityDeathEvent event)
    {
        final LivingEntity entity = event.getEntity();

        final Player killer = entity.getKiller();
        if (killer == null)
        {
            return; // przezorny zawsze ubezpieczony
        }

        final IServerQuestsManager manager = this.serverQuests.getServerQuestsManager();
        manager.bumpStatisticIf(killer.getUniqueId(), statistic -> this.isMeets(statistic, entity.getType()));
    }

    private boolean isMeets(final ITrackedStatistic statistic, final EntityType deathEntity)
    {
        if (! (statistic instanceof MobKillStatistic))
        {
            return false;
        }
        final MobKillStatistic mobKillStat = (MobKillStatistic) statistic;

        final String entityName = deathEntity.toString();
        return mobKillStat.getMobType().equals(entityName);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

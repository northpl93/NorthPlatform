package pl.north93.northplatform.api.bukkit.hologui;

import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.northplatform.api.bukkit.server.IBukkitExecutor;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.messages.LegacyMessage;
import pl.north93.northplatform.api.global.messages.TranslatableString;

public class ActionBarKeeper
{
    private final Map<Player, TranslatableString> actionBars = new WeakHashMap<>();
    private final IBukkitExecutor                 bukkitExecutor;
    private boolean taskLaunched;

    @Bean
    private ActionBarKeeper(final IBukkitExecutor bukkitExecutor)
    {
        this.bukkitExecutor = bukkitExecutor;
    }

    public void setActionBar(final Player player, final TranslatableString translatableString)
    {
        this.actionBars.put(player, translatableString);
        this.ensureTaskLaunched();
    }

    public void reset(final Player player)
    {
        if (this.actionBars.remove(player) != null)
        {
            // jesli gracz istnial to wysylamy mu pusty action bar
            this.sendTo(player, TranslatableString.empty());
        }
    }

    private void ensureTaskLaunched()
    {
        if (this.taskLaunched)
        {
            return;
        }
        this.taskLaunched = true;
        this.bukkitExecutor.syncTimer(20, this::task);
    }

    private void task()
    {
        for (final Map.Entry<Player, TranslatableString> entry : this.actionBars.entrySet())
        {
            final Player player = entry.getKey();
            if (! player.isOnline())
            {
                continue;
            }

            this.sendTo(player, entry.getValue());
        }
    }

    private void sendTo(final Player player, final TranslatableString translatableString)
    {
        final LegacyMessage value = translatableString.getLegacy(player.getLocale());
        player.sendActionBar(value.asString());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("actionBars", this.actionBars).append("taskLaunched", this.taskLaunched).toString();
    }
}

package pl.north93.northplatform.antycheat.client.monitor.action;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.antycheat.analysis.Violation;
import pl.north93.northplatform.api.bukkit.utils.chat.ChatUtils;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.features.bukkit.chat.admin.AdminChatService;

public class AlertAdminAction implements IAntyCheatAction
{
    @Inject
    private AdminChatService adminChatService;
    private final AlertLevel level;
    private final String     cheatName;

    public AlertAdminAction(final AlertLevel level, final String cheatName)
    {
        this.level = level;
        this.cheatName = cheatName;
    }

    @Override
    public void handle(final Player player, final Violation violation)
    {
        if (player.hasPermission("antycheat.ignore.alert"))
        {
            return;
        }

        final BaseComponent message = this.level.composeMessage(player, this.cheatName);
        this.adminChatService.broadcast(message);
    }

    public enum AlertLevel
    {
        WARNING
                {
                    @Override
                    public BaseComponent composeMessage(final Player player, final String cheat)
                    {
                        return ChatUtils.parseLegacyText("&c[PAC] &7{0} &c&l&m-&c&l> &6&l{1} &cI", player.getName(), cheat);
                    }
                },
        ALERT
        {
            @Override
            public BaseComponent composeMessage(final Player player, final String cheat)
            {
                return ChatUtils.parseLegacyText("&c[PAC] &7{0} &c&l&m-&c&l> &6&l{1} &c&lII", player.getName(), cheat);
            }
        };

        public abstract BaseComponent composeMessage(Player player, String cheat);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("level", this.level).append("cheatName", this.cheatName).toString();
    }
}

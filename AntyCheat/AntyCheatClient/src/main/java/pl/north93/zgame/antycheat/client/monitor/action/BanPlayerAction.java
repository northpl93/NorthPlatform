package pl.north93.zgame.antycheat.client.monitor.action;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.zgame.antycheat.analysis.Violation;
import pl.north93.zgame.api.bukkit.utils.chat.ChatUtils;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.features.bukkit.chat.admin.AdminChatService;
import pl.north93.zgame.features.global.punishment.BanService;
import pl.north93.zgame.features.global.punishment.cfg.PredefinedBanCfg;

@Slf4j
public class BanPlayerAction implements IAntyCheatAction
{
    @Inject
    private AdminChatService adminChatService;
    @Inject
    private BanService       banService;

    @Override
    public void handle(final Player player, final Violation violation)
    {
        log.info("[AutoBan] Player: {}, Violation:{} banned for cheats!", player.getName(), violation.name());

        final BaseComponent component = ChatUtils.parseLegacyText("&c[PAC] &7{0} &c&l&m-&c&l> &4&lban automatyczny", player.getName());
        this.adminChatService.broadcast(component);

        final PredefinedBanCfg banReason = this.banService.getConfigByName("cheats");
        //this.banService.createBan(Identity.of(player), null, banReason);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

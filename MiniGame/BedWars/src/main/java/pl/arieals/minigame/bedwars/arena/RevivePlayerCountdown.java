package pl.arieals.minigame.bedwars.arena;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getArena;
import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.setPlayerStatus;
import static pl.north93.zgame.api.bukkit.utils.chat.ChatUtils.fromLegacyText;


import com.destroystokyo.paper.Title;

import org.bukkit.Bukkit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.minigame.bedwars.event.PlayerRevivedEvent;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.bukkit.utils.AbstractCountdown;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class RevivePlayerCountdown extends AbstractCountdown
{
    private final Logger logger = LoggerFactory.getLogger(RevivePlayerCountdown.class);
    @Inject @Messages("BedWars")
    private MessagesBox messages;
    private final INorthPlayer  player;
    private final BedWarsPlayer bedWarsPlayer;

    public RevivePlayerCountdown(final INorthPlayer player, final BedWarsPlayer bedWarsPlayer)
    {
        super(5);
        this.player = player;
        this.bedWarsPlayer = bedWarsPlayer;
    }

    @Override
    protected void loop(final int time)
    {
        final String locale = this.player.getLocale();

        final BaseComponent title = fromLegacyText(this.messages.getMessage(locale, "die.respawn.title"));
        final BaseComponent subtitle = this.messages.getMessage(locale, "die.respawn.subtitle", time);

        this.player.sendTitle(new Title(title, subtitle, 0, 20, 0));
    }

    @Override
    protected void end()
    {
        if (this.bedWarsPlayer.isOffline())
        {
            return;
        }

        this.logger.info("Player {} from team {} revived", this.player.getName(), this.bedWarsPlayer.getTeam().getName());

        this.player.teleport(this.bedWarsPlayer.getTeam().getSpawn());
        this.player.setFallDistance(0); // disable fall damage

        setPlayerStatus(this.player, PlayerStatus.PLAYING);

        Bukkit.getPluginManager().callEvent(new PlayerRevivedEvent(getArena(this.player), this.bedWarsPlayer));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("player", this.player).append("bedWarsPlayer", this.bedWarsPlayer).toString();
    }
}

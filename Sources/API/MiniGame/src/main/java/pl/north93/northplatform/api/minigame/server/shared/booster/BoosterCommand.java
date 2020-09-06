package pl.north93.northplatform.api.minigame.server.shared.booster;

import static java.util.Comparator.comparing;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;


import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import lombok.Getter;
import lombok.ToString;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommand;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.IPlayer;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.minigame.shared.api.booster.IBooster;
import pl.north93.northplatform.api.minigame.shared.api.booster.IBoosterManager;

@ToString(onlyExplicitlyIncluded = true)
public class BoosterCommand extends NorthCommand
{
    @Inject @Messages("MiniGameApi")
    private MessagesBox messages;
    @Inject
    private IPlayersManager playersManager;
    @Inject
    private IBoosterManager boosterManager;

    public BoosterCommand()
    {
        super("booster", "mnoznik");
    }

    @Override
    public void execute(final NorthCommandSender sender, final Arguments args, final String label)
    {
        final INorthPlayer northPlayer = INorthPlayer.wrap(sender);

        final IPlayer player = this.playersManager.unsafe().getNullable(northPlayer.getIdentity());
        final Collection<IBooster> boosters = this.boosterManager.getValidBoosters(player);

        final ToDoubleFunction<IBooster> multiplierFunc = booster -> booster.getMultiplier(player);
        final double total = boosters.stream().mapToDouble(multiplierFunc).sum();

        sender.sendMessage(this.messages, "booster.cmd.header", total);

        final Function<IBooster, BoosterEntry> mapper = booster -> new BoosterEntry(booster, player);
        final Comparator<BoosterEntry> comparator = comparing(BoosterEntry::getValue).reversed();
        boosters.stream().map(mapper).sorted(comparator).forEach(booster ->
        {
            final String name = this.messages.getString(sender.getMyLocale(), "booster.id." + booster.getId());
            final String expiration = this.getExpirationString(sender.getMyLocale(), booster.getExpires());

            sender.sendMessage(this.messages, "booster.cmd.line", booster.getValue(), name, expiration);
        });
    }

    private String getExpirationString(final Locale locale, final long expires)
    {
        if (expires == -1)
        {
            return "";
        }

        final Duration duration = Duration.between(Instant.now(), Instant.ofEpochMilli(expires));
        final String formatDuration = formatDuration(duration.toMillis(), "H 'godzin' m 'minut'");

        return this.messages.getString(locale, "booster.cmd.expire", formatDuration);
    }

    @Getter
    @ToString
    static class BoosterEntry
    {
        private final String id;
        private final double value;
        private final long expires;

        public BoosterEntry(final IBooster booster, final IPlayer player)
        {
            this.id = booster.getId();
            this.value = booster.getMultiplier(player);
            this.expires = booster.getExpiration(player);
        }
    }
}
package pl.north93.zgame.features.global.punishment;

import javax.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.config.IConfig;
import pl.north93.zgame.api.global.config.NetConfig;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.network.players.IPlayer;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;
import pl.north93.zgame.api.global.utils.DateUtil;
import pl.north93.zgame.api.global.utils.Vars;
import pl.north93.zgame.features.global.punishment.cfg.PredefinedBanCfg;
import pl.north93.zgame.features.global.punishment.cfg.PunishmentCfg;

public class BanService
{
    @Inject @NetConfig(type = PunishmentCfg.class, id="punishment")
    private IConfig<PunishmentCfg> config;
    @Inject @Messages("Commands")
    private MessagesBox            messages;
    @Inject
    private INetworkManager        networkManager;

    @Bean
    private BanService()
    {
    }

    /**
     * Zwraca obiekt reprezentujący ostatniego bana u danego gracza.
     *
     * @param identity Identyfikator gracza dla którego robimy zapytanie.
     * @return Obiekt reprezentujący bana lub null.
     */
    public AbstractBan getBan(final Identity identity)
    {
        final Optional<IPlayer> optional = this.networkManager.getPlayers().unsafe().get(identity);
        return optional.map(player -> this.getBan0(player.getMetaStore())).orElse(null);
    }

    /**
     * Banuje gracza o podanym identyfikatorze.
     * Wyrzuca go z sieci jeśli trzeba.
     *
     * @param identity Identyfikator gracza którego banujemy.
     * @param adminId UUID admina lub null.
     * @param config Predefiniowana konfiguracja bana.
     */
    public void createBan(final Identity identity, final UUID adminId, final PredefinedBanCfg config)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final Duration duration = Optional.ofNullable(config.getDuration()).map(Duration::ofMillis).orElse(null);
            final PredefinedBan ban = new PredefinedBan(adminId, Instant.now(), duration, config.getId());

            ban.save(t.getPlayer().getMetaStore());

            if (t.isOnline())
            {
                final IOnlinePlayer onlinePlayer = (IOnlinePlayer) t.getPlayer();
                onlinePlayer.kick(this.getBanMessage(ban, onlinePlayer.getMyLocale()));
            }
        }
    }

    /**
     * Usuwa informacje o banie u tego gracza.
     *
     * @param identity Identyfikator gracza któremu kasujemy bana.
     */
    public void removeBan(final Identity identity)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final MetaStore metaStore = t.getPlayer().getMetaStore();

            metaStore.remove(AbstractBan.BAN_TYPE);
            metaStore.remove(AbstractBan.BAN_GIVEN_AT);
            metaStore.remove(AbstractBan.BAN_ADMIN_ID);
            metaStore.remove(AbstractBan.BAN_DURATION);
        }
    }

    /**
     * Generuje wiadomość bana dla podanego bana i języka.
     *
     * @param ban Instancja bana dla której generujemy wiadomość.
     * @param locale Język dla którego generujemy wiadomość.
     * @return Wiadomość bana dla podanego bana i języka.
     */
    public BaseComponent getBanMessage(final AbstractBan ban, final Locale locale)
    {
        final BaseComponent reason = this.getBanReason(ban, locale);

        final BaseComponent expiration;
        if (ban.getDuration() == null)
        {
            expiration = this.messages.getMessage(locale, "join.banned.never_expire", (Object) null);
        }
        else
        {
            final Instant expireTime = ban.getGivenAt().plus(ban.getDuration());
            final String formattedTime = DateUtil.formatDateDiff(expireTime.toEpochMilli());

            expiration = this.messages.getMessage(locale, "join.banned.expire_at", formattedTime);
        }

        return this.messages.getMessage(locale, "join.banned", reason, expiration);
    }

    @Nullable
    public PredefinedBanCfg getConfigById(final int id)
    {
        final List<PredefinedBanCfg> bans = this.config.get().getBans();
        for (final PredefinedBanCfg ban : bans)
        {
            if (ban.getId() == id)
            {
                return ban;
            }
        }

        return null;
    }

    @Nullable
    public PredefinedBanCfg getConfigByName(final String name)
    {
        final List<PredefinedBanCfg> bans = this.config.get().getBans();
        for (final PredefinedBanCfg ban : bans)
        {
            if (ban.getName().equals(name))
            {
                return ban;
            }
        }

        return null;
    }

    @Nullable
    private AbstractBan getBan0(final MetaStore store)
    {
        final String type = store.get(AbstractBan.BAN_TYPE);
        if (type == null)
        {
            return null;
        }
        else if (type.equals("predefined"))
        {
            return new PredefinedBan(store);
        }
        else
        {
            throw new IllegalArgumentException(type);
        }
    }

    private BaseComponent getBanReason(final AbstractBan ban, final Locale locale)
    {
        if (ban instanceof PredefinedBan)
        {
            final PredefinedBan predefinedBan = (PredefinedBan) ban;

            final PredefinedBanCfg predefinedBanCfg = this.getConfigById(predefinedBan.getBanReason());
            if (predefinedBanCfg == null)
            {
                return new TextComponent();
            }

            return predefinedBanCfg.getMessage().getValue(locale, Vars.empty());
        }

        return null;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

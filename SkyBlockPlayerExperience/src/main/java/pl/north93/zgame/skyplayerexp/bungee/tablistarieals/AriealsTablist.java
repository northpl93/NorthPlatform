package pl.north93.zgame.skyplayerexp.bungee.tablistarieals;

import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.north93.zgame.api.economy.ICurrency;
import pl.north93.zgame.api.economy.IEconomyManager;
import pl.north93.zgame.api.economy.impl.client.EconomyComponent;
import pl.north93.zgame.api.global.component.annotations.InjectComponent;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IOnlinePlayer;
import pl.north93.zgame.api.global.redis.messaging.TemplateManager;
import pl.north93.zgame.api.global.redis.subscriber.RedisSubscriber;
import pl.north93.zgame.skyblock.api.IslandData;
import pl.north93.zgame.skyblock.bungee.SkyBlockBungee;
import pl.north93.zgame.skyplayerexp.bungee.tablist.StaticProvider;
import pl.north93.zgame.skyplayerexp.bungee.tablist.TablistManager;

public class AriealsTablist implements Listener
{
    @InjectComponent("API.Economy")
    private       EconomyComponent    economy;
    @InjectComponent("SkyBlock.Proxy")
    private       SkyBlockBungee      skyblock;
    @InjectComponent("API.MinecraftNetwork.NetworkManager")
    private       INetworkManager     networkManager;
    @InjectComponent("API.Database.Redis.Subscriber")
    private       RedisSubscriber     subscriber;
    @InjectComponent("API.Database.Redis.MessagePackSerializer")
    private       TemplateManager     msgPackTemplates;
    private final TablistManager      manager;
    private final Map<UUID, UserInfo> userInfos;
    private final StaticProvider[]    skyRanking;
    private final StaticProvider[]    moneyRanking;
    private final StaticProvider[]    onlineAdmins;
    private final StaticProvider[]    onlineYoutubers;

    public AriealsTablist(final TablistManager manager)
    {
        this.manager = manager;
        this.skyRanking = new StaticProvider[15];
        this.moneyRanking = new StaticProvider[15];
        this.onlineAdmins = new StaticProvider[10];
        this.onlineYoutubers = new StaticProvider[10];
        this.userInfos = new ConcurrentHashMap<>();
    }

    public void setup()
    {
        this.subscriber.subscribe("ariealstab", this::userInfoListener);

        this.manager.setCellProvider(1, 0, new StaticProvider("&lTy"));
        this.manager.setCellProvider(1, 1, ctx -> "&6nick: &r" + ctx.getPlayer().getName());
        this.manager.setCellProvider(1, 2, ctx -> "&6ranga: &r" + ctx.getNetworkPlayer().get().getGroup().getName());
        this.manager.setCellProvider(1, 3, new UserMoney());

        this.manager.setCellProvider(1, 5, new StaticProvider("&c&lAdmini:"));
        this.manager.setCellProvider(2, 5, new StaticProvider("&d&lYouTuberzy:"));
        for (int i = 0; i < 10; i++)
        {
            final StaticProvider staticProvider = new StaticProvider("");
            this.manager.setCellProvider(1, i + 6, staticProvider);
            this.onlineAdmins[i] = staticProvider;
            final StaticProvider staticProvider2 = new StaticProvider("");
            this.manager.setCellProvider(2, i + 6, staticProvider2);
            this.onlineYoutubers[i] = staticProvider2;
        }

        this.manager.setCellProvider(2, 0, new StaticProvider("&lTwoja wyspa"));
        this.manager.setCellProvider(2, 1, ctx -> "&6punkty: &r" + (ctx.hasIsland() ? ctx.getIslandData().getPoints().intValue() : "--"));
        this.manager.setCellProvider(2, 2, new IslandSizeProvider());

        this.manager.setCellProvider(0, 2, new StaticProvider("&lTOP WYSP"));
        for (int i = 0; i < 15; i++)
        {
            final StaticProvider staticProvider = new StaticProvider("");
            this.manager.setCellProvider(0, i + 3, staticProvider);
            this.skyRanking[i] = staticProvider;
        }

        this.manager.setCellProvider(3, 2, new StaticProvider("&lTOP WALUTY"));
        for (int i = 0; i < 15; i++)
        {
            final StaticProvider staticProvider = new StaticProvider("");
            this.manager.setCellProvider(3, i + 3, staticProvider);
            this.moneyRanking[i] = staticProvider;
        }

        this.manager.setCellProvider(1, 17, new StaticProvider("&LKOMENDY:"));
        this.manager.setCellProvider(1, 18, new StaticProvider("&6/wyspa"));
        this.manager.setCellProvider(2, 18, new StaticProvider("&6/kompas"));
    }

    public synchronized void update() // only one thread can process update
    {
        int i = 0;
        for (final UUID islandId : this.skyblock.getRanking().getTopIslands(15))
        {
            final IslandData island = this.skyblock.getIslandDao().getIsland(islandId);
            final String ownerNick = this.networkManager.getPlayers().getNickFromUuid(island.getOwnerId());
            final String points = String.valueOf(island.getPoints().intValue()).replace("\u00a0", "");
            final String msg = MessageFormat.format("&e{0}.&r{1} &6{2}", i + 1, ownerNick, points);
            this.skyRanking[i++].setText(msg);
        }

        final IEconomyManager ecoManager = this.economy.getEconomyManager();
        final ICurrency currency = ecoManager.getCurrency("skyblock");
        int j = 0;
        for (final Pair<UUID, Long> player : ecoManager.getRanking(currency).getTopPlayersMoney(15))
        {
            final String nick = this.networkManager.getPlayers().getNickFromUuid(player.getKey());
            final String amount = String.valueOf(player.getValue().intValue()).replace("\u00a0", "");
            final String msg = MessageFormat.format("&e{0}.&r{1} &6{2}", j + 1, nick, amount);
            this.moneyRanking[j++].setText(msg);
        }

        for (final StaticProvider onlineAdmin : this.onlineAdmins)
        {
            onlineAdmin.setText("");
        }
        for (final StaticProvider onlineYoutuber : this.onlineYoutubers)
        {
            onlineYoutuber.setText("");
        }

        int latestAdminSlot = 0, latestYouTubeSlot = 0;
        for (final Map.Entry<UUID, UserInfo> userInfo : this.userInfos.entrySet())
        {
            if (userInfo.getValue().getYoutuber())
            {
                this.onlineYoutubers[latestYouTubeSlot++].setText("&d" + userInfo.getValue().getName());
            }
            else
            {
                this.onlineAdmins[latestAdminSlot++].setText(userInfo.getValue().getName());
            }
        }
    }

    private void userInfoListener(final String channel, final byte[] bytes)
    {
        final UserInfo userInfo = this.msgPackTemplates.deserialize(UserInfo.class, bytes);
        if (userInfo.getRemove())
        {
            this.userInfos.remove(userInfo.getUuid());
        }
        else
        {
            this.userInfos.put(userInfo.getUuid(), userInfo);
        }
        this.update();
        this.manager.updateAll();
    }

    @EventHandler
    public void addAdminYtToTab(final PostLoginEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();
        final IOnlinePlayer netPlayer = this.networkManager.getOnlinePlayer(player.getName()).get();
        if (netPlayer == null)
        {
            return;
        }
        final String group = netPlayer.getGroup().getName();
        switch (group)
        {
            case "admin":
            {
                final UserInfo userInfo = new UserInfo(player.getUniqueId(), false, false, "&c" + player.getName());
                this.subscriber.publish("ariealstab", this.msgPackTemplates.serialize(UserInfo.class, userInfo));
                break;
            }
            case "youtuber":
            case "youtuber2":
            {
                final UserInfo userInfo = new UserInfo(player.getUniqueId(), false, true, player.getName());
                this.subscriber.publish("ariealstab", this.msgPackTemplates.serialize(UserInfo.class, userInfo));
                break;
            }
            case "moderator":
            {
                final UserInfo userInfo = new UserInfo(player.getUniqueId(), false, false, "&2" + player.getName());
                this.subscriber.publish("ariealstab", this.msgPackTemplates.serialize(UserInfo.class, userInfo));
                break;
            }
        }
    }

    @EventHandler
    public void removeAdminYtFromTab(final PlayerDisconnectEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();
        if (! this.userInfos.containsKey(player.getUniqueId()))
        {
            return;
        }
        final UserInfo userInfo = new UserInfo(player.getUniqueId(), true, false, player.getName());
        this.subscriber.publish("ariealstab", this.msgPackTemplates.serialize(UserInfo.class, userInfo));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

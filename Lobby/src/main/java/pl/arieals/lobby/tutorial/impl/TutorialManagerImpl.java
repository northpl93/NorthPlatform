package pl.arieals.lobby.tutorial.impl;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.api.minigame.server.lobby.hub.LocalHubServer;
import pl.arieals.lobby.tutorial.ITutorialManager;
import pl.arieals.lobby.tutorial.TutorialStatus;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.metadata.MetaStore;
import pl.north93.zgame.api.global.network.INetworkManager;
import pl.north93.zgame.api.global.network.players.IPlayerTransaction;
import pl.north93.zgame.api.global.network.players.Identity;

/*default*/ class TutorialManagerImpl implements ITutorialManager
{
    @Inject
    private BukkitApiCore   apiCore;
    @Inject
    private MiniGameServer  miniGameServer;
    @Inject
    private INetworkManager networkManager;

    @Bean
    private TutorialManagerImpl()
    {
    }

    @Override
    public String getTutorialHub(final HubWorld hubWorld)
    {
        if (this.isTutorialHub(hubWorld))
        {
            return hubWorld.getHubId();
        }

        final String hubId = hubWorld.getHubId() + "_tutorial";
        if (this.getThisHubServer().getHubWorld(hubId) == null)
        {
            // todo przy rozbudowie systemu shardowania
            return null;
        }

        return hubId;
    }

    @Override
    public boolean isTutorialHub(final HubWorld hubWorld)
    {
        return hubWorld.getHubId().endsWith("_tutorial");
    }

    @Override
    public void startTutorial(final Player player)
    {
        final HubWorld hubWorld = this.getThisHubServer().getHubWorld(player);

        final String tutorialHub = this.getTutorialHub(hubWorld);
        if (tutorialHub == null)
        {
            return;
        }

        this.getThisHubServer().movePlayerToHub(player, tutorialHub);
    }

    @Override
    public void exitTutorial(final Player player)
    {
        final HubWorld hubWorld = this.getThisHubServer().getHubWorld(player);

        if (! this.isTutorialHub(hubWorld))
        {
            return;
        }

        final String tutorialHubId = hubWorld.getHubId();
        final String normalHubId = tutorialHubId.substring(0, tutorialHubId.length() - "_tutorial".length());

        this.getThisHubServer().movePlayerToHub(player, normalHubId);
    }

    @Override
    public TutorialStatus getStatus(final Identity identity, final String tutorialId)
    {
        final MetaKey tutorialStatusKey = this.getTutorialStatusKey(tutorialId);
        return this.networkManager.getPlayers().unsafe().get(identity).map(player ->
        {
            final MetaStore metaStore = player.getMetaStore();
            if (metaStore.contains(tutorialStatusKey))
            {
                return TutorialStatus.valueOf(metaStore.get(tutorialStatusKey));
            }

            return TutorialStatus.NEVER_PLAYED;
        }).orElse(null);
    }

    @Override
    public void updateStatus(final Identity identity, final String tutorialId, final TutorialStatus status)
    {
        try (final IPlayerTransaction t = this.networkManager.getPlayers().transaction(identity))
        {
            final MetaStore metaStore = t.getPlayer().getMetaStore();
            final MetaKey tutorialStatusKey = this.getTutorialStatusKey(tutorialId);

            metaStore.set(tutorialStatusKey, status.name());
        }
    }

    private MetaKey getTutorialStatusKey(final String tutorialId)
    {
        return MetaKey.get("hubTutorial_" + tutorialId);
    }

    // zwraca obiekt LocalHub reprezentujacy ten serwer hostujacy huby.
    private LocalHubServer getThisHubServer()
    {
        final LobbyManager serverManager = this.miniGameServer.getServerManager();
        return serverManager.getLocalHub();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

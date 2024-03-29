package pl.north93.northplatform.lobby.tutorial.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.IBukkitServerManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.metadata.MetaKey;
import pl.north93.northplatform.api.global.metadata.MetaStore;
import pl.north93.northplatform.api.global.network.players.IPlayerTransaction;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.uri.UriHandler;
import pl.north93.northplatform.api.global.uri.UriInvocationContext;
import pl.north93.northplatform.api.minigame.server.lobby.LobbyManager;
import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.api.minigame.server.lobby.hub.LocalHubServer;
import pl.north93.northplatform.lobby.tutorial.ITutorialManager;
import pl.north93.northplatform.lobby.tutorial.TutorialStatus;
import pl.north93.northplatform.lobby.tutorial.event.TutorialStatusChangedEvent;

@Slf4j
/*default*/ class TutorialManagerImpl implements ITutorialManager
{
    @Inject
    private LobbyManager lobbyManager;
    @Inject
    private IPlayersManager playersManager;
    @Inject
    private IBukkitServerManager serverManager;

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
    public boolean isInTutorial(final Player player)
    {
        final LocalHubServer hubServer = this.getThisHubServer();
        return this.isTutorialHub(hubServer.getHubWorld(player));
    }

    @Override
    public String getTutorialId(final Player player)
    {
        final LocalHubServer hubServer = this.getThisHubServer();

        final HubWorld playerHub = hubServer.getHubWorld(player);
        if (this.isTutorialHub(playerHub))
        {
            return playerHub.getHubId();
        }

        return null;
    }

    @Override
    public boolean canStartTutorial(final Player player)
    {
        final LocalHubServer hubServer = this.getThisHubServer();
        return this.getTutorialHub(hubServer.getHubWorld(player)) != null;
    }

    @Override
    public void startTutorial(final Player player, final String tutorialId)
    {
        this.getThisHubServer().movePlayerToHub(player, tutorialId);
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
        return this.playersManager.unsafe().get(identity).map(player ->
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
        try (final IPlayerTransaction t = this.playersManager.transaction(identity))
        {
            final MetaStore metaStore = t.getPlayer().getMetaStore();
            final MetaKey tutorialStatusKey = this.getTutorialStatusKey(tutorialId);

            metaStore.set(tutorialStatusKey, status.name());
        }
        catch (final Exception e)
        {
            log.error("Failed to update tutorial status", e);
            return;
        }

        this.serverManager.callEvent(new TutorialStatusChangedEvent(identity, tutorialId, status));
    }

    @UriHandler("/lobby/tutorial/complete/:uuid")
    public void markTutorialAsCompleted(final UriInvocationContext context)
    {
        final Player player = Bukkit.getPlayer(context.asUuid("uuid"));
        final LocalHubServer hubServer = this.getThisHubServer();

        final HubWorld hubWorld = hubServer.getHubWorld(player);
        if (! this.isTutorialHub(hubWorld))
        {
            return;
        }

        this.exitTutorial(player);
        this.updateStatus(Identity.of(player), hubWorld.getHubId(), TutorialStatus.PLAYED_COMPLETED);
    }

    private MetaKey getTutorialStatusKey(final String tutorialId)
    {
        return MetaKey.get("hubTutorial_" + tutorialId);
    }

    // zwraca obiekt LocalHub reprezentujacy ten serwer hostujacy huby.
    private LocalHubServer getThisHubServer()
    {
        return this.lobbyManager.getLocalHub();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

package pl.north93.northplatform.lobby.tutorial.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import pl.north93.northplatform.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.northplatform.api.minigame.server.lobby.hub.event.PlayerPreSwitchHubEvent;
import pl.north93.northplatform.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.north93.northplatform.api.minigame.server.shared.party.PartyClient;
import pl.north93.northplatform.lobby.tutorial.ITutorialManager;
import pl.north93.northplatform.lobby.tutorial.TutorialStatus;
import pl.north93.northplatform.lobby.tutorial.event.PlayerEnterTutorialEvent;
import pl.north93.northplatform.lobby.tutorial.event.PlayerExitTutorialEvent;
import pl.north93.northplatform.api.bukkit.BukkitApiCore;
import pl.north93.northplatform.api.bukkit.utils.AutoListener;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.Identity;

public class TutorialManagementListener implements AutoListener
{
    @Inject
    private BukkitApiCore    apiCore;
    @Inject
    private PartyClient      partyClient;
    @Inject
    private ITutorialManager tutorialManager;

    @EventHandler
    public void callStartTutorialEvent(final PlayerSwitchedHubEvent event)
    {
        final Player player = event.getPlayer();

        final HubWorld oldHub = event.getOldHub();
        if (this.tutorialManager.isTutorialHub(oldHub))
        {
            this.apiCore.callEvent(new PlayerExitTutorialEvent(player, oldHub));
        }

        final HubWorld newHub = event.getNewHub();
        if (this.tutorialManager.isTutorialHub(newHub))
        {
            this.apiCore.callEvent(new PlayerEnterTutorialEvent(player, newHub));
        }
    }

    @EventHandler
    public void teleportPlayerToTutorialHub(final PlayerPreSwitchHubEvent event)
    {
        final Player player = event.getPlayer();
        final HubWorld newHub = event.getNewHub();

        final String tutorialHub = this.tutorialManager.getTutorialHub(newHub);
        if (tutorialHub == null)
        {
            // nie ma takiego tutoriala
            return;
        }

        if (this.partyClient.cantDecideAboutHimself(player) || this.isAlreadyPlayedTutorial(player, tutorialHub))
        {
            // jesli gracz jest w party to nie zmuszamy go do samouczka
            // jeśli gracz już grał ten samouczek to nie teleportujemy go drugi raz
            return;
        }

        this.tutorialManager.updateStatus(Identity.of(player), tutorialHub, TutorialStatus.PLAYED_ABORTED);
        this.tutorialManager.startTutorial(player, tutorialHub);
        event.setCancelled(true);
    }

    private boolean isAlreadyPlayedTutorial(final Player player, final String tutorialHubId)
    {
        final TutorialStatus status = this.tutorialManager.getStatus(Identity.of(player), tutorialHubId);
        return status != TutorialStatus.NEVER_PLAYED;
    }
}

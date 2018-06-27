package pl.arieals.lobby.tutorial.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerPreSwitchHubEvent;
import pl.arieals.api.minigame.server.lobby.hub.event.PlayerSwitchedHubEvent;
import pl.arieals.api.minigame.server.utils.party.PartyClient;
import pl.arieals.lobby.tutorial.ITutorialManager;
import pl.arieals.lobby.tutorial.TutorialStatus;
import pl.arieals.lobby.tutorial.event.PlayerEnterTutorialEvent;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.AutoListener;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.Identity;

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
        final HubWorld newHub = event.getNewHub();
        final Player player = event.getPlayer();

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
        this.tutorialManager.startTutorial(player);
        event.setCancelled(true);
    }

    private boolean isAlreadyPlayedTutorial(final Player player, final String tutorialHubId)
    {
        final TutorialStatus status = this.tutorialManager.getStatus(Identity.of(player), tutorialHubId);
        return status != TutorialStatus.NEVER_PLAYED;
    }
}

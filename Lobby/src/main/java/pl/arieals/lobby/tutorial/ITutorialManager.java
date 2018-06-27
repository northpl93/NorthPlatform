package pl.arieals.lobby.tutorial;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.global.network.players.Identity;

public interface ITutorialManager
{
    String getTutorialHub(HubWorld hubWorld);

    boolean isTutorialHub(HubWorld hubWorld);

    void startTutorial(Player player);

    void exitTutorial(Player player);

    TutorialStatus getStatus(Identity identity, String tutorialId);

    void updateStatus(Identity identity, String tutorialId, TutorialStatus status);
}

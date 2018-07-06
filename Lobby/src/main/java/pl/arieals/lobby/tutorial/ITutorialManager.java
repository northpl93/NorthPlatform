package pl.arieals.lobby.tutorial;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import pl.arieals.api.minigame.server.lobby.hub.HubWorld;
import pl.north93.zgame.api.global.network.players.Identity;

public interface ITutorialManager
{
    @Nullable
    String getTutorialHub(HubWorld hubWorld);

    boolean isTutorialHub(HubWorld hubWorld);

    boolean isInTutorial(Player player);

    @Nullable
    String getTutorialId(Player player);

    boolean canStartTutorial(Player player);

    void startTutorial(Player player);

    void exitTutorial(Player player);

    TutorialStatus getStatus(Identity identity, String tutorialId);

    void updateStatus(Identity identity, String tutorialId, TutorialStatus status);
}

package pl.arieals.api.minigame.server.gamehost.reward;

import java.util.List;
import java.util.Locale;

import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.network.players.Identity;

public interface IReward
{
    @FunctionalInterface
    interface RewardMessageRenderer
    {
        String[] composeMessage(MessagesBox messagesBox, Locale locale, List<IReward> allRewardsOfType);
    }

    String getId();

    void apply(Identity identity);

    RewardMessageRenderer getRenderer();
}

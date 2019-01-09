package pl.north93.northplatform.api.minigame.server.gamehost.reward;

import java.util.List;
import java.util.Locale;

import net.md_5.bungee.api.chat.BaseComponent;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.network.players.Identity;

public interface IReward
{
    @FunctionalInterface
    interface RewardMessageRenderer
    {
        BaseComponent composeMessage(MessagesBox messagesBox, Locale locale, List<IReward> allRewardsOfType);
    }

    String getId();

    IReward apply(Identity identity);

    RewardMessageRenderer getRenderer();
}

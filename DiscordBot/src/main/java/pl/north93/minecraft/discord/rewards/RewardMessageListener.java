package pl.north93.minecraft.discord.rewards;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import pl.north93.minecraft.discord.DiscordBotConfig;
import pl.north93.minecraft.discord.rewards.builtin.CurrencyDiscordReward;
import pl.north93.minecraft.discord.rewards.builtin.VipDiscordReward;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.network.players.PlayerNotFoundException;

/*default*/ class RewardMessageListener extends ListenerAdapter
{
    @Inject
    private RewardsManager   rewardsManager;
    @Inject
    private DiscordBotConfig discordBotConfig;

    @Bean
    private RewardMessageListener(final JDA jda)
    {
        jda.addEventListener(this);
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event)
    {
        final User author = event.getAuthor();
        if (author.isBot())
        {
            return;
        }

        final TextChannel channel = event.getChannel();
        if (! this.discordBotConfig.getRewardsChannel().equals(channel.getId()))
        {
            return;
        }

        final Message eventMessage = event.getMessage();

        final String messageContent = eventMessage.getContentRaw();
        if (this.isMessageInvalid(messageContent))
        {
            eventMessage.delete().submit();

            final Message message = new MessageBuilder().append(author).append(" w treści wiadomości podaj swój nick w grze").build();
            channel.sendMessage(message).submit();

            return;
        }

        try
        {
            this.rewardsManager.applyRewards(author.getId(), messageContent, this.getRewardsList());

            final Message message = new MessageBuilder().append(author).append(" nadałem nagrody").build();
            channel.sendMessage(message).submit();
        }
        catch (final PlayerNotFoundException exception)
        {
            final Message message = new MessageBuilder().append(author).append(" nie znalazłem użytkownika o podanym nicku :(").build();
            channel.sendMessage(message).submit();
        }
        catch (final PlayerAlreadyTakenRewardsException exception)
        {
            final Message message = new MessageBuilder().append(author).append(" nagrody możesz odebrać tylko raz :(").build();
            channel.sendMessage(message).submit();
        }
    }

    private DiscordRewardsList getRewardsList()
    {
        final DiscordRewardsList rewardsList = new DiscordRewardsList();

        rewardsList.addReward(new CurrencyDiscordReward("minigame", 2000));
        rewardsList.addReward(new VipDiscordReward());

        return rewardsList;
    }

    private boolean isMessageInvalid(final String message)
    {
        final int minUserLength = 3;
        final int maxUserLength = 16;
        return message.contains(" ") || message.length() < minUserLength || message.length() > maxUserLength;
    }
}

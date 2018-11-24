package pl.north93.northplatform.discord.rewards;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import com.mongodb.client.MongoCollection;

import org.bson.Document;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.global.component.annotations.bean.Bean;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.network.players.Identity;
import pl.north93.northplatform.api.global.network.players.PlayerNotFoundException;
import pl.north93.northplatform.api.global.storage.StorageConnector;

@Slf4j
public class RewardsManager
{
    @Inject
    private IPlayersManager  playersManager;
    @Inject
    private StorageConnector storageConnector;

    @Bean
    private RewardsManager()
    {
    }

    /**
     * @param discordId Identyfikator użytkownika discorda.
     * @param username Nick użytkownika w Minecraft.
     * @param rewards Obiekt przechowujący nagrody do przydzielenia.
     * @throws PlayerNotFoundException Gdy gracz MC o podanym nicku nie zostanie znaleziony.
     * @throws PlayerAlreadyTakenRewardsException Gdy użytkownik (discord lub mc) odebrał już nagrody.
     */
    public void applyRewards(final String discordId, final String username, final DiscordRewardsList rewards)
    {
        // wyrzuci PlayerNotFoundException jak nie znajdziemy gracza
        final Identity identity = this.playersManager.completeIdentity(Identity.create(null, username));

        final RewardTakeEntry rewardTakeEntry = this.findRewardTakeEntry(discordId, identity.getUuid());
        if (rewardTakeEntry != null)
        {
            log.info("User with discordId {} and Minecraft username {} already taken rewards!", discordId, username);
            throw new PlayerAlreadyTakenRewardsException(rewardTakeEntry);
        }

        log.info("Applying rewards for discordId {} and Minecraft username {}", discordId, username);
        rewards.apply(identity);

        this.markUserTakenRewards(discordId, identity);
    }

    private void markUserTakenRewards(final String discordId, final Identity identity)
    {
        final MongoCollection<RewardTakeEntry> collection = this.getCollection();
        collection.insertOne(new RewardTakeEntry(discordId, identity.getUuid(), Instant.now()));
    }

    private RewardTakeEntry findRewardTakeEntry(final String discordId, final UUID minecraftId)
    {
        final MongoCollection<RewardTakeEntry> collection = this.getCollection();

        return collection.find(new Document("$or", Arrays.asList(
                new Document("discordId", discordId),
                new Document("userId", minecraftId)))).first();
    }

    private MongoCollection<RewardTakeEntry> getCollection()
    {
        return this.storageConnector.getMainDatabase()
                                    .getCollection("discordRewards")
                                    .withDocumentClass(RewardTakeEntry.class);
    }
}

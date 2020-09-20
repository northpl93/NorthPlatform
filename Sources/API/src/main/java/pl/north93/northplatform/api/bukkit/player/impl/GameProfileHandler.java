package pl.north93.northplatform.api.bukkit.player.impl;

import java.util.Optional;
import java.util.UUID;

import com.destroystokyo.paper.event.profile.PreFillProfileEvent;
import com.destroystokyo.paper.event.profile.PreLookupProfileEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Sets;

import org.bukkit.event.EventHandler;

import org.diorite.commons.function.consumer.Consumer;

import lombok.extern.slf4j.Slf4j;
import pl.north93.northplatform.api.bukkit.server.AutoListener;
import pl.north93.northplatform.api.global.network.players.IPlayersManager;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.network.mojang.CachedMojangProfile;
import pl.north93.northplatform.api.global.network.mojang.CachedMojangProfileProperty;
import pl.north93.northplatform.api.global.network.mojang.IMojangCache;

@Slf4j
public class GameProfileHandler implements AutoListener
{
    @Inject
    private IMojangCache    mojangCache;
    @Inject
    private IPlayersManager playersManager;

    @EventHandler
    public void lookupProfile(final PreLookupProfileEvent event)
    {
        this.playersManager.getUuidFromNick(event.getName()).ifPresent(uuid ->
        {
            event.setUUID(uuid);
            this.fetchCachedProperties(uuid, property ->
            {
                event.addProfileProperties(Sets.newHashSet(property));
            });
        });
    }

    @EventHandler
    public void fillProfile(final PreFillProfileEvent event)
    {
        final PlayerProfile playerProfile = event.getPlayerProfile();
        if (playerProfile.getId() == null)
        {
            log.info("Skipped filling of profile {} because there is no UUID", playerProfile);
            return;
        }

        this.fetchCachedProperties(playerProfile.getId(), playerProfile::setProperty);
    }

    private void fetchCachedProperties(final UUID profileId, final Consumer<ProfileProperty> propertyConsumer)
    {
        final Optional<CachedMojangProfile> cacheResult = this.mojangCache.getProfile(profileId);
        if (cacheResult.isPresent())
        {
            final CachedMojangProfile cachedProfile = cacheResult.get();
            for (final CachedMojangProfileProperty cachedProperty : cachedProfile.getProperties())
            {
                propertyConsumer.accept(new ProfileProperty(cachedProperty.getName(), cachedProperty.getValue(), cachedProperty.getSignature()));
            }

            log.info("Filled profile {}", profileId);
        }
        else
        {
            log.info("There is no such cached profile {}", profileId);
        }
    }
}

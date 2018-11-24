package pl.north93.northplatform.api.minigame.shared.api.match;

import java.util.Collection;
import java.util.UUID;

import org.bson.types.ObjectId;

import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.global.network.players.Identity;

public interface IMatchManager
{
    IMatchAccess createMatch(UUID arenaId, GameIdentity identity, UUID serverId, String mapId, Collection<Identity> startParticipants);

    IMatch getMatch(ObjectId matchId);
}

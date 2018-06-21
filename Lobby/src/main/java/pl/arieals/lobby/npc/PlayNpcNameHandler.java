package pl.arieals.lobby.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaClient;
import pl.arieals.api.minigame.server.lobby.arenas.IArenaObserver;
import pl.arieals.api.minigame.server.utils.citizens.TranslatedNameTrait;
import pl.arieals.api.minigame.shared.api.GameIdentity;
import pl.arieals.api.minigame.shared.api.arena.IArena;
import pl.arieals.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.utils.Vars;

public class PlayNpcNameHandler implements IArenaObserver
{
    private final MessagesBox  uiMessages;
    private final IArenaClient arenaClient;

    @Bean
    private PlayNpcNameHandler(final IArenaClient arenaClient, @Messages("UserInterface") final MessagesBox uiMessages)
    {
        this.arenaClient = arenaClient;
        this.uiMessages = uiMessages;
        
        this.arenaClient.observe(new ArenaQuery(), this);
    }

    public void updateAllNPCs()
    {
        final NPCRegistry namedNPCRegistry = CitizensAPI.getNamedNPCRegistry("hub");
        if (namedNPCRegistry == null)
        {
            return;
        }

        namedNPCRegistry.forEach(this::handleNPC);
    }

    private void handleNPC(final NPC npc)
    {
        if ( npc.hasTrait(PlayGameTrait.class) )
        {
            this.updateName(npc);
        }
    }

    public void updateName(final NPC npc)
    {
        final GameIdentity gameIdentity = npc.getTrait(PlayGameTrait.class).getGameIdentity();
        final Vars<Object> vars = Vars.of("players", this.getPlayersCount(gameIdentity));
        
        final TranslatableString clickToPlay = TranslatableString.of(this.uiMessages, "@npc.clicktoplay");
        final TranslatableString gameName = TranslatableString.of(this.uiMessages, this.getNpcNameId(gameIdentity));
        final TranslatableString playersCount = TranslatableString.of(this.uiMessages, "@npc.playerscount$players").withVars(vars);
        
        npc.getTrait(TranslatedNameTrait.class).setNameLines(clickToPlay, gameName, playersCount);
    }

    private String getNpcNameId(final GameIdentity gameIdentity)
    {
        return "@npc." + gameIdentity.getGameId() + "_" + gameIdentity.getVariantId();
    }
    
    private int getPlayersCount(final GameIdentity gameIdentity)
    {
        return this.arenaClient.get(new ArenaQuery().miniGame(gameIdentity)).stream().mapToInt(this::getPlayersCount).sum();
    }
    
    private int getPlayersCount(final IArena arena)
    {
        final Integer signedPlayers = arena.getMetadata().get(StandardArenaMetaData.SIGNED_PLAYERS);
        return signedPlayers != null ? signedPlayers : arena.getPlayersCount();
    }
    
    // XXX: should we always update all npcs
    
    @Override
    public void arenaCreated(final IArena arena)
    {
        this.updateAllNPCs();
    }
    
    @Override
    public void arenaRemoved(final IArena arena)
    {
        this.updateAllNPCs();
    }
    
    @Override
    public void arenaUpdated(final IArena arena)
    {
        this.updateAllNPCs();
    }
}

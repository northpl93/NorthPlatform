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
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;
import pl.north93.zgame.api.global.messages.TranslatableString;
import pl.north93.zgame.api.global.metadata.MetaKey;
import pl.north93.zgame.api.global.utils.Vars;

public class PlayNpcNameHandler implements IArenaObserver
{
    private final MessagesBox uiMessages;
    
    private final IArenaClient arenaClient;

    @Bean
    private PlayNpcNameHandler(IArenaClient arenaClient, @Messages("UserInterface") MessagesBox uiMessages)
    {
        this.arenaClient = arenaClient;
        this.uiMessages = uiMessages;
        
        this.arenaClient.observe(new ArenaQuery(), this);
    }
    
    public void updateName(NPC npc)
    {
        GameIdentity gameIdentity = npc.getTrait(PlayGameTrait.class).getGameIdentity();
        Vars<Object> vars = Vars.of("players", getPlayersCount(gameIdentity));
        
        final TranslatableString clickToPlay = TranslatableString.of(uiMessages, "@npc.clicktoplay");
        final TranslatableString gameName = TranslatableString.of(uiMessages, getNpcNameId(gameIdentity));
        final TranslatableString playersCount = TranslatableString.of(uiMessages, "@npc.playerscount$players").withVars(vars);
        
        npc.getTrait(TranslatedNameTrait.class).setNameLines(clickToPlay, gameName, playersCount);
    }
    
    public void updateAllNPCs()
    {
        NPCRegistry namedNPCRegistry = CitizensAPI.getNamedNPCRegistry("hub");
        
        namedNPCRegistry.forEach(this::handleNPC);
    }
    
    private void handleNPC(NPC npc)
    {
        if ( npc.hasTrait(PlayGameTrait.class) )
        {
            updateName(npc);
        }
    }
    
    private String getNpcNameId(GameIdentity gameIdentity)
    {
        return "@npc." + gameIdentity.getGameId() + "_" + gameIdentity.getVariantId();
    }
    
    private int getPlayersCount(GameIdentity gameIdentity)
    {
        return arenaClient.get(new ArenaQuery().miniGame(gameIdentity)).stream().mapToInt(this::getPlayersCount).sum();
    }
    
    private int getPlayersCount(IArena arena)
    {
        Integer signedPlayers = arena.getMetadata().get(MetaKey.get("signedPlayers"));
        return signedPlayers != null ? signedPlayers.intValue() : arena.getPlayersCount();
    }
    
    // XXX: should we always update all npcs
    
    @Override
    public void arenaCreated(IArena arena)
    {
        updateAllNPCs();
    }
    
    @Override
    public void arenaRemoved(IArena arena)
    {
        updateAllNPCs();
    }
    
    @Override
    public void arenaUpdated(IArena arena)
    {
        updateAllNPCs();
    }
}

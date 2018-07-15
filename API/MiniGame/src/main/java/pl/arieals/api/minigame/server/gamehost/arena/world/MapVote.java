package pl.arieals.api.minigame.server.gamehost.arena.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteRandomUtils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import pl.arieals.api.minigame.server.gamehost.GameHostManager;
import pl.arieals.api.minigame.server.gamehost.arena.LocalArena;
import pl.arieals.api.minigame.server.gamehost.arena.player.ArenaChatManager;
import pl.arieals.api.minigame.shared.api.MapTemplate;
import pl.north93.zgame.api.bukkit.player.INorthPlayer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.messages.Messages;
import pl.north93.zgame.api.global.messages.MessagesBox;

public class MapVote
{
    private final GameHostManager gameHostManager;
    private final LocalArena      arena;
    
    private final Map<Player, MapTemplate> votes = new WeakHashMap<>();
    private MapTemplate[] options;
    
    @Inject @Messages("MiniGameApi")
    private MessagesBox    messages;

    public MapVote(GameHostManager gameHostManager, LocalArena arena)
    {
        this.gameHostManager = gameHostManager;
        this.arena = arena;
        
        prepareOptions();
    }
    
    private void prepareOptions()
    {
        final Collection<MapTemplate> allTemplates = this.gameHostManager.getMapTemplateManager().getAllTemplates();

        final int maxOptions = this.gameHostManager.getMiniGameConfig().getMapVoting().getNumberOfMaps();
        final int numberOfOptions = Math.min(maxOptions, allTemplates.size());
        
        final List<MapTemplate> results = new ArrayList<>(numberOfOptions);
        DioriteRandomUtils.getRandom(allTemplates, results, numberOfOptions, true);
        
        final MapTemplate[] options = new MapTemplate[numberOfOptions];
        this.options = results.toArray(options);
    }

    public void removeVote(final Player player)
    {
        this.votes.remove(player);
    }

    public MapTemplate vote(final Player player, final int option)
    {
        if (this.options == null || option > this.options.length || option <= 0)
        {
            return null;
        }
        final MapTemplate selectedMap = this.options[option - 1];

        this.votes.put(player, selectedMap);
        return selectedMap;
    }

    public MapTemplate[] getOptions()
    {
        return this.options;
    }

    public void resetVoting()
    {
        this.options = null;
        this.votes.clear();
    }

    public Map<MapTemplate, Integer> getResults()
    {
        final Map<MapTemplate, Integer> results = new HashMap<>();
        
        for ( MapTemplate option : options )
        {
            int count = votes.values().stream().filter(o -> o == option).mapToInt(o -> 1).sum();
            results.put(option, count);
        }
        
        return results;
    }

    public MapTemplate getWinner()
    {
        MapTemplate winner = null;
        int winnerPoints = 0;
        for (final Map.Entry<MapTemplate, Integer> entry : this.getResults().entrySet())
        {
            if (winner == null || winnerPoints < entry.getValue())
            {
                winner = entry.getKey();
                winnerPoints = entry.getValue();
            }
        }
        
        return winner;
    }
    
    public void printStartVoteInfo()
    {
        final ArenaChatManager chatManager = this.arena.getChatManager();
        chatManager.broadcast(this.messages, "vote.started");

        for (int i = 0; i < this.getOptions().length; i++)
        {
            final MapTemplate gameMap = this.getOptions()[i];
            this.printVoteOption(i, gameMap);
        }
    }

    private void printVoteOption(final int number, final MapTemplate template)
    {
        final int friendlyNumber = number + 1;
        for (final INorthPlayer player : this.arena.getPlayersManager().getAllPlayers())
        {
            final Locale locale = player.getMyLocale();
            final BaseComponent line = this.messages.getComponent(locale, "vote.option_line", friendlyNumber, template.getDisplayName());

            line.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mapvote " + friendlyNumber));

            final BaseComponent hover = this.messages.getComponent(locale, "vote.click_to_vote");
            line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{hover}));

            player.sendMessage(line);
        }
    }

    public void printVotingResult()
    {
        final MapTemplate winner = this.getWinner();
        this.arena.getChatManager().broadcast(this.messages, "vote.winner", winner.getDisplayName());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("votes", this.votes).append("options", this.options).toString();
    }
}

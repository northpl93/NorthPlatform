package pl.north93.northplatform.lobby.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.diorite.commons.math.DioriteMathUtils;

import pl.north93.northplatform.api.bukkit.gui.ConfigGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.Gui;
import pl.north93.northplatform.api.bukkit.gui.IGuiIcon;
import pl.north93.northplatform.api.bukkit.gui.element.dynamic.DynamicElementData;
import pl.north93.northplatform.api.bukkit.gui.element.dynamic.DynamicRenderer;
import pl.north93.northplatform.api.bukkit.utils.itemstack.ItemStackBuilder;
import pl.north93.northplatform.api.global.commands.Arguments;
import pl.north93.northplatform.api.global.commands.NorthCommandSender;
import pl.north93.northplatform.api.global.commands.annotation.QuickCommand;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;
import pl.north93.northplatform.api.global.utils.Vars;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.ArenaQuery;
import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.api.minigame.shared.api.arena.IArena;
import pl.north93.northplatform.api.minigame.shared.api.arena.StandardArenaMetaData;
import pl.north93.northplatform.lobby.play.PlayGameController;

public class JoinDynamicGui extends Gui
{
    private static final Comparator<IArena> ARENA_COMPARATOR = Comparator.comparingInt(IArena::getPlayersCount).reversed();
    
    private static final double ALMOST_FULL_RATIO = 0.65;
    private static final double OVERFULL_RATIO = 1.25;
    
    @Inject
    @Messages("UserInterface")
    private static MessagesBox        messages;
    @Inject
    private static PlayGameController playController;
    @Inject
    private static IArenaClient       arenaClient;
    
    private final GameIdentity gameIdentity;
    private final boolean viewAll;
    
    private boolean isExpanded;
    
    public JoinDynamicGui(GameIdentity gameIdentity, boolean viewAll)
    {
        super(messages, getGuiName(gameIdentity));
        this.gameIdentity = gameIdentity;
        this.viewAll = viewAll;
        
        this.getContent().setVariables(Vars.of("expanded", isExpanded));
    }

    public static void openForPlayerAndGame(final Player player, final GameIdentity gameIdentity)
    {
        new JoinDynamicGui(gameIdentity, player.hasPermission("dynamicplay.viewall")).open(player);
    }
    
    @DynamicRenderer
    public Collection<DynamicElementData> renderArenas()
    {
        Collection<DynamicElementData> result = new ArrayList<>();
        Collection<IArena> arenas = getArenas();
        
        arenas.stream().filter(this::isAlmostFull).sorted(ARENA_COMPARATOR).forEach(arena -> result.add(createElementForArena(arena)));
        
        List<IArena> almostEmpty = arenas.stream().filter(this::isAlmostEmpty).sorted(ARENA_COMPARATOR).collect(Collectors.toList());
        almostEmpty.forEach(arena -> result.add(createElementForArena(arena)));
        
        if ( almostEmpty.size() == 0 || viewAll )
        {
            arenas.stream().filter(this::isEmpty).sorted(ARENA_COMPARATOR).limit(viewAll ? Long.MAX_VALUE : 1).forEach(arena -> result.add(createElementForArena(arena)));
        }

        arenas.stream().filter(this::isFull).sorted(ARENA_COMPARATOR).forEach(arena -> result.add(createElementForArena(arena)));
        arenas.stream().filter(this::isOverFull).sorted(ARENA_COMPARATOR).forEach(arena -> result.add(createElementForArena(arena)));
        
        if ( result.size() == 0 )
        {
            result.add(createNonArenaElement());
        }
        
        return result;
    }
    
    private DynamicElementData createElementForArena(IArena arena)
    {
        ItemStack is = new ItemStackBuilder().material(isEmpty(arena) || isAlmostEmpty(arena) || isAlmostFull(arena) ? Material.CHEST : Material.ENDER_CHEST)
                .amount(Math.max(1, getSignedPlayers(arena))).build();
        
        IGuiIcon icon = ConfigGuiIcon.builder().itemStack(is)
                .name(TranslatableString.of(messages, isOverFull(arena) ? "@play.dynamic.icon.full.name" : "@play.dynamic.icon.join.name"))
                .lore(TranslatableString.of(messages, isOverFull(arena) ? "@play.dynamic.icon.full.lore$players,max,map" : "@play.dynamic.icon.join.lore$players,max,map"))
                .build();
                
        return DynamicElementData.builder().icon(icon).clickHandler(event -> tryJoin(event.getWhoClicked(), arena))
                .vars(Vars.builder().and("players", getSignedPlayers(arena)).and("max", arena.getMaxPlayers()).and("map", arena.getWorldDisplayName()).build()).build();
    }
    
    private void tryJoin(Player player, IArena arena)
    {
        if ( viewAll || !isOverFull(arena) )
        {
            playController.playGame(player, arena);
        }
    }
    
    private DynamicElementData createNonArenaElement()
    {
        ItemStack is = new ItemStackBuilder().material(Material.STAINED_CLAY).data(14).build();
        
        IGuiIcon icon = ConfigGuiIcon.builder().itemStack(is).name(TranslatableString.of(messages, "@play.dynamic.noarenas.icon.name")).build();
        
        return DynamicElementData.builder().icon(icon).build();
    }
    
    private Collection<IArena> getArenas()
    {
        return arenaClient.get(new ArenaQuery().miniGame(gameIdentity));
    }
    
    private boolean isEmpty(IArena arena)
    {
        return getSignedPlayers(arena) == 0;
    }
    
    private boolean isAlmostEmpty(IArena arena)
    {
        return getSignedPlayers(arena) > 0 && getSignedPlayers(arena) < DioriteMathUtils.ceil(arena.getMaxPlayers() * ALMOST_FULL_RATIO);
    }
    
    private boolean isAlmostFull(IArena arena)
    {
        int signedPlayers = getSignedPlayers(arena);
        return signedPlayers >= DioriteMathUtils.ceil(arena.getMaxPlayers() * ALMOST_FULL_RATIO) && signedPlayers < arena.getMaxPlayers();
    }
    
    private boolean isFull(IArena arena)
    {
        int signedPlayers = getSignedPlayers(arena);
        return signedPlayers >= arena.getMaxPlayers() && signedPlayers < DioriteMathUtils.ceil(arena.getMaxPlayers() * OVERFULL_RATIO);
    }
    
    private boolean isOverFull(IArena arena)
    {
        return getSignedPlayers(arena) >= DioriteMathUtils.ceil(arena.getMaxPlayers() * OVERFULL_RATIO);
    }
    
    private int getSignedPlayers(IArena arena)
    {
        Integer signedPlayers = arena.getMetaStore().get(StandardArenaMetaData.SIGNED_PLAYERS);
        return signedPlayers != null ? signedPlayers.intValue() : arena.getPlayersCount();
    }
    
    private static String getGuiName(final GameIdentity gameIdentity)
    {
        return "playdynamic/play_" + gameIdentity.getGameId().toLowerCase() + "_" + gameIdentity.getVariantId();
    }

    @QuickCommand(name = "testplaydynamic")
    public static void testCmd(final NorthCommandSender sender, final Arguments args, final String label)
    {
        if ( System.getProperty("debug") == null )
        {
            return;
        }
        
        final Player player = (Player) sender.unwrapped();
        openForPlayerAndGame(player, GameIdentity.create("GoldHunter", "team12"));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameIdentity", this.gameIdentity).toString();
    }
}

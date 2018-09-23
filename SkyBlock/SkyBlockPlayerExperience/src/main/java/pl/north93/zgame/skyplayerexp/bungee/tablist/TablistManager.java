package pl.north93.zgame.skyplayerexp.bungee.tablist;

import static pl.north93.zgame.skyplayerexp.bungee.tablist.Utils.packetJson;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PlayerListHeaderFooter;
import net.md_5.bungee.protocol.packet.PlayerListItem;

public class TablistManager implements Listener
{
    private static final String TEXTURES  = "eyJ0aW1lc3RhbXAiOjE0ODc3NTE1Njg2NTgsInByb2ZpbGVJZCI6ImNjNzliYThjZTU0NTQ3YTNhNDU0NWU2NWY5YjgyOTdjIiwicHJvZmlsZU5hbWUiOiJOb3J0aFBMOTMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJiMjJhZGYzOThhNjUxM2JhOGI5MWFlMjY5MmQyZjQyYzVhYjMwNWU2NmExZGVhMzVjZjhjMTM5NjRiZTMxOGEifX19";
    private static final String SIGNATURE = "uCDIPCwVcbCk/0QvOHGKdvtqN3v+QROQGGVq/oan9RP2ASySCcQfaC38oGhkKBBjhH75cWr2wkggZIMIbk1jD0S4LDkPny5uylQUdoNgBj/JC9j/v9r7RM5M8HDpQSXtmuUPoa68UdbgSDPoW9El5bQYpbaxgABYMgpQGRqE++57svWkx8gYwRCD08W0Big4LIYsoLWQC7hZJ4DUELuR7D1R90xND1h2P3gondCYf3LNy6yspE4x5/Fg4ZanyAuCSWczvkbcACMv6KQaoA/uoe+CMQI5VXdH6azfzmT+oEqHbqBT1GXEuzbg2XL9qRZSN9HsavyFqh6X17F6oXWcLJPL/MBfUpB8V5H6wFH/5lFJNfXBFxnfVsL/TJURS2bCK2OACfnRHE6i4OQZWCKMRplmKbprtw2sNfDg/tulyo2onrD1JMZeq5W56Sz1i2EKRuihfAWyZHNFZgI7zxuWHNaYoBO14ens7nuBhNgiW53v+deGPq3lLb7yYpgLs4MDxhIBYrv2pcD9qKvZsHMv+faXFg76/6ZBq6oYzTygXrTcs+U05Nm+kC9IEyBvDUpuEJwZPLLGGFKs3f1vK+reVXCE5Bq022SxjStpIAaXNQbE0ADsrEQHfy+gDOQB+fAU3gZosDj3XZAUylELGIXSey0V6AOMwPQMl9TAIxnbA2I=";
    private static final char[] ALPHABET  = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T'};
    private static final int    TABLIST_SIZE = 80;
    private final Map<Integer, TablistSlot> slots;
    private ContextProvider contextProvider = new ContextProvider();

    public TablistManager()
    {
        this.slots = new HashMap<>(TABLIST_SIZE);
        for (int i = 0; i < TABLIST_SIZE; i++)
        {
            this.slots.put(i, new TablistSlot(i, UUID.randomUUID(), "$" + this.getCharId(i), TEXTURES));
        }
    }

    public void setCellProvider(final int x, final int y, final ICellProvider provider)
    {
        final TablistSlot tablistSlot = this.slots.get(x * 20 + y);
        tablistSlot.setCellProvider(provider);
    }

    public void updateAll()
    {
        for (final ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
        {
            this.update(player);
        }
    }

    private void update(final ProxiedPlayer player)
    {
        final TablistDrawingContext ctx = this.contextProvider.getFor(player);

        final PlayerListItem packet = new PlayerListItem();
        packet.setAction(PlayerListItem.Action.UPDATE_DISPLAY_NAME);

        final PlayerListItem.Item[] items = new PlayerListItem.Item[80];
        int i = 0;
        for (final TablistSlot tablistSlot : this.slots.values())
        {
            final PlayerListItem.Item item = new PlayerListItem.Item();
            item.setUuid(tablistSlot.getFakePlayerId());
            item.setDisplayName(tablistSlot.processThisCell(ctx));
            items[i++] = item;
        }

        packet.setItems(items);
        player.unsafe().sendPacket(packet);
    }

    @SuppressWarnings("MagicNumber")
    private String getCharId(final int id)
    {
        if (id < 20)
        {
            return "A" + ALPHABET[id];
        }
        if (id < 40)
        {
            return "B" + ALPHABET[id - 20];
        }
        if (id < 60)
        {
            return "C" + ALPHABET[id - 40];
        }
        if (id < 80)
        {
            return "D" + ALPHABET[id - 60];
        }
        return "";
    }

    @EventHandler
    public void onPlayerJoin(final PostLoginEvent event)
    {
        final ProxiedPlayer player = event.getPlayer();
        final TablistDrawingContext ctx = this.contextProvider.getFor(player);

        final PlayerListItem packet = new PlayerListItem();
        packet.setAction(PlayerListItem.Action.ADD_PLAYER);

        final PlayerListItem.Item[] items = new PlayerListItem.Item[80];
        int i = 0;
        for (final TablistSlot tablistSlot : this.slots.values())
        {
            final PlayerListItem.Item item = new PlayerListItem.Item();
            item.setUuid(tablistSlot.getFakePlayerId());
            item.setUsername(tablistSlot.getSortName());
            item.setPing(1000);
            item.setGamemode(0);
            item.setProperties(new String[][] {new String[] {"textures", tablistSlot.getTextures(), SIGNATURE}});
            item.setDisplayName(tablistSlot.processThisCell(ctx));
            items[i++] = item;
        }

        packet.setItems(items);
        player.unsafe().sendPacket(packet);
        final String header = "&bWitaj na &lSkyBlocku Arieals.PL";
        final String footer = "Zapraszamy na nasza strone: &6www.arieals.pl";
        player.unsafe().sendPacket(new PlayerListHeaderFooter(packetJson(header), packetJson(footer)));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("slots", this.slots).toString();
    }
}

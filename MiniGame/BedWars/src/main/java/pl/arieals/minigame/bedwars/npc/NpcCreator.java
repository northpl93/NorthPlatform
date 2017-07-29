package pl.arieals.minigame.bedwars.npc;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import org.apache.commons.lang3.tuple.Pair;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.shared.ItemsGroup;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.shop.ShopGuiManager;
import pl.arieals.minigame.bedwars.shop.gui.ShopMain;
import pl.arieals.minigame.bedwars.shop.gui.UpgradesGui;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

public class NpcCreator implements Listener
{
    @Inject
    private BukkitApiCore  apiCore;
    @Inject
    private IGlobalShops   globalShops;
    @Inject
    private ShopGuiManager shopGuiManager;
    @Inject @Named("BedWarsNpcRegistry")
    private NPCRegistry    npcRegistry;

    @EventHandler(priority = EventPriority.HIGH)
    public void createNpc(final GameStartEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();
        final ItemsGroup shoppers = this.globalShops.getGroup("bed_wars_shoppers");

        for (final Team team : arenaData.getTeams())
        {
            this.apiCore.sync(() -> this.getTeamNpc(shoppers, team), npc ->
            {
                final NPC shopper = this.createNpc(npc.getKey());
                shopper.addTrait(new ShopTrait(ShopTrait.NpcType.SHOP));
                shopper.setName("Sklep");
                shopper.spawn(team.getConfig().getShopNpc().toBukkit(event.getArena().getWorld().getCurrentWorld()));

                final NPC upgrader = this.createNpc(npc.getValue());
                upgrader.addTrait(new ShopTrait(ShopTrait.NpcType.UPGRADES));
                upgrader.setName("Ulepszenia");
                upgrader.spawn(team.getConfig().getUpgradesNpc().toBukkit(event.getArena().getWorld().getCurrentWorld()));
            });
        }
    }

    @EventHandler
    public void onInteractWithNpc(final PlayerInteractAtEntityEvent event)
    {
        final Entity entity = event.getRightClicked();
        if (! this.npcRegistry.isNPC(entity))
        {
            return;
        }

        final NPC npc = this.npcRegistry.getNPC(entity);
        if (! npc.hasTrait(ShopTrait.class))
        {
            return;
        }

        final Player player = event.getPlayer();

        final ShopTrait trait = npc.getTrait(ShopTrait.class);
        if (trait.getType() == ShopTrait.NpcType.SHOP)
        {
            final ShopMain shopMain = new ShopMain(player);
            shopMain.open(player);
        }
        else
        {
            final UpgradesGui upgradesGui = new UpgradesGui(player);
            upgradesGui.open(player);
        }
    }

    private Pair<NpcItem, NpcItem> getTeamNpc(final ItemsGroup group, final Team team)
    {
        final Iterator<NpcItem> skins = team.getPlayers()
                                            .stream()
                                            .map(player -> this.globalShops.getPlayer(player).getActiveItem(group))
                                            .filter(Objects::nonNull)
                                            .map(NpcItem::new)
                                            .sorted(Comparator.comparing(NpcItem::getPriority)).iterator();

        final NpcItem first = skins.hasNext() ? skins.next() : null;
        final NpcItem second = skins.hasNext() ? skins.next() : first;

        return Pair.of(first, second);
    }

    private NPC createNpc(final NpcItem item)
    {
        if (item == null)
        {
            return this.npcRegistry.createNPC(EntityType.VILLAGER, UUID.randomUUID().toString());
        }
        return this.npcRegistry.createNPC(item.getEntityType(), UUID.randomUUID().toString());
    }
}

package pl.arieals.minigame.bedwars.npc;

import static pl.arieals.api.minigame.server.gamehost.MiniGameApi.getPlayerStatus;


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
import net.citizensnpcs.trait.VillagerProfession;
import pl.arieals.api.minigame.server.gamehost.event.arena.gamephase.GameStartEvent;
import pl.arieals.api.minigame.server.utils.citizens.SkinTrait;
import pl.arieals.api.minigame.shared.api.PlayerStatus;
import pl.arieals.globalshops.server.IGlobalShops;
import pl.arieals.globalshops.server.domain.ItemsGroup;
import pl.arieals.minigame.bedwars.arena.BedWarsArena;
import pl.arieals.minigame.bedwars.arena.Team;
import pl.arieals.minigame.bedwars.shop.ShopGuiManager;
import pl.arieals.minigame.bedwars.shop.gui.ShopMain;
import pl.arieals.minigame.bedwars.shop.gui.UpgradesGui;
import pl.north93.zgame.api.bukkit.server.IBukkitExecutor;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

public class NpcCreator implements Listener
{
    @Inject
    private IBukkitExecutor executor;
    @Inject
    private IGlobalShops    globalShops;
    @Inject
    private ShopGuiManager  shopGuiManager;
    @Inject @Named("BedWarsNpcRegistry")
    private NPCRegistry     npcRegistry;

    @EventHandler(priority = EventPriority.HIGH)
    public void createNpc(final GameStartEvent event)
    {
        final BedWarsArena arenaData = event.getArena().getArenaData();
        final ItemsGroup shoppers = this.globalShops.getGroup("bedwars_shoppers");

        for (final Team team : arenaData.getTeams())
        {
            this.executor.mixed(() -> this.getTeamNpc(shoppers, team), npc ->
            {
                // NPC z sklepem
                final NPC shopper = this.createNpc(npc.getKey());
                shopper.addTrait(new ShopTrait(ShopTrait.NpcType.SHOP));
                shopper.setName("Sklep");
                shopper.spawn(team.getConfig().getShopNpc().toBukkit(event.getArena().getWorld().getCurrentWorld()));

                // NPC z ulepszeniami
                final NPC upgrader = this.createNpc(npc.getValue());
                upgrader.addTrait(new ShopTrait(ShopTrait.NpcType.UPGRADES));
                upgrader.setName("Ulepszenia");
                upgrader.spawn(team.getConfig().getUpgradesNpc().toBukkit(event.getArena().getWorld().getCurrentWorld()));
            });
        }
    }

    @EventHandler(ignoreCancelled = true) // spectatorzy beda mieli anulowany ten event
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

        // spectatorzy nie moga klikac na npcty
        final PlayerStatus playerStatus = getPlayerStatus(player);
        if (playerStatus == null || playerStatus.isSpectator())
        {
            return;
        }

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
        final Iterator<NpcItem> skins = team.getBukkitPlayersAsStream()
                                            .map(player -> this.globalShops.getPlayer(player).getActiveItem(group))
                                            .filter(Objects::nonNull)
                                            .map(NpcItem::new)
                                            .sorted(Comparator.comparing(NpcItem::getPriority).reversed()).iterator();

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

        final NPC npc = this.npcRegistry.createNPC(item.getEntityType(), UUID.randomUUID().toString());
        if (item.getEntityType() == EntityType.PLAYER)
        {
            npc.addTrait(new SkinTrait(item.getProfileData(), item.getDataSign()));
        }
        else if (item.getEntityType() == EntityType.VILLAGER)
        {
            npc.getTrait(VillagerProfession.class).setProfession(item.getVillagerProfession());
        }

        return npc;
    }
}

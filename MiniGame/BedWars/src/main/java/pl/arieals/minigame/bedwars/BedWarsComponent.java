package pl.arieals.minigame.bedwars;

import javax.xml.bind.JAXB;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.api.minigame.server.MiniGameServer;
import pl.arieals.api.minigame.server.lobby.LobbyManager;
import pl.arieals.minigame.bedwars.arena.generator.ItemRotator;
import pl.arieals.minigame.bedwars.cfg.BwConfig;
import pl.arieals.minigame.bedwars.listener.ArenaStartListener;
import pl.arieals.minigame.bedwars.listener.BedDestroyListener;
import pl.arieals.minigame.bedwars.listener.BuildListener;
import pl.arieals.minigame.bedwars.listener.DeathListener;
import pl.arieals.minigame.bedwars.listener.DeathMatchStartListener;
import pl.arieals.minigame.bedwars.listener.GameEndListener;
import pl.arieals.minigame.bedwars.listener.ItemBuyListener;
import pl.arieals.minigame.bedwars.listener.PlayerItemsListener;
import pl.arieals.minigame.bedwars.listener.PlayerTeamListener;
import pl.arieals.minigame.bedwars.listener.SpecialItems;
import pl.arieals.minigame.bedwars.listener.UpgradeInstallListener;
import pl.arieals.minigame.bedwars.npc.NpcCreator;
import pl.north93.zgame.api.bukkit.BukkitApiCore;
import pl.north93.zgame.api.bukkit.utils.dmgtracker.DamageTracker;
import pl.north93.zgame.api.global.ApiCore;
import pl.north93.zgame.api.global.component.Component;
import pl.north93.zgame.api.global.component.annotations.bean.Bean;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.component.annotations.bean.Named;

public class BedWarsComponent extends Component
{
    @Inject
    private MiniGameServer server;
    @Inject
    private BukkitApiCore  bukkitApi;

    @Override
    protected void enableComponent()
    {
        if (this.server.getServerManager() instanceof LobbyManager)
        {
            return; // TODO prevent errors in testing environment
        }
        this.bukkitApi.registerEvents(
                new ArenaStartListener(),
                new NpcCreator(), // zarzadza tworzeniem NPC w bazie
                new PlayerTeamListener(), //wejscie,start areny,wyjscie
                new BuildListener(), // crafting,budowanie,niszczenie
                new DeathListener(), // smierc gracza
                new BedDestroyListener(), // zniszczenie lozka
                new PlayerItemsListener(), // pilnuje ekwipunku, dropu po śmierci
                new ItemBuyListener(), // zakup itemów,wysyłanie komunikatu zakupu
                new UpgradeInstallListener(), // instalowanie apgrejdów,wysyłanie komunikatu u upgrade
                new SpecialItems(), // obsluga itemow specjalnych
                new DeathMatchStartListener(), // przygotowujemy deathmatcha
                new GameEndListener()); // eliminacja teamu i koniec gry

        new ItemRotator().start(); // uruchamiamy watek obracajacy itemkami
        DamageTracker.get(); // uruchamiamy damage trackera.
    }

    @Override
    protected void disableComponent()
    {
    }

    @Bean
    private BwConfig bedWarsConfig(final ApiCore api)
    {
        return JAXB.unmarshal(api.getFile("MiniGame.BedWars.xml"), BwConfig.class);
    }

    @Bean @Named("BedWarsNpcRegistry")
    private NPCRegistry bedWarsNpcRegistry()
    {
        return CitizensAPI.createNamedNPCRegistry("bedwars", new MemoryNPCDataStore());
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}

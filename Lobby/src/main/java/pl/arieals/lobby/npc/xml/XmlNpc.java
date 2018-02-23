package pl.arieals.lobby.npc.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import pl.arieals.api.minigame.server.utils.citizens.SkinTrait;
import pl.north93.zgame.api.bukkit.utils.xml.XmlLocation;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XmlNpc
{
    @XmlElement
    private String      name;
    @XmlElement
    private XmlSkin     skin;
    @XmlElement
    private XmlLocation location;

    protected NPC loadNpc(final NPCRegistry registry)
    {
        final NPC npc = registry.createNPC(EntityType.PLAYER, this.name);

        if (this.skin != null)
        {
            final String data = this.skin.getData();
            final String sign = this.skin.getSign();

            npc.addTrait(new SkinTrait(data, sign));
        }

        return npc;
    }

    protected void postSpawn(final NPC npc)
    {
        // hack wymagany bo Citizens zmienia EntityTrackerEntry na swoje i przez to usuwa listenera
    }

    public final NPC createNpc(final NPCRegistry registry, final World world)
    {
        final NPC npc = this.loadNpc(registry);
        npc.spawn(this.location.toBukkit(world));
        this.postSpawn(npc);
        return npc;
    }
}

package pl.north93.northplatform.lobby.npc.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import pl.north93.northplatform.api.minigame.server.lobby.arenas.IArenaClient;
import pl.north93.northplatform.api.minigame.server.shared.citizens.TranslatedNameTrait;
import pl.north93.northplatform.api.minigame.shared.api.GameIdentity;
import pl.north93.northplatform.lobby.npc.PlayGameTrait;
import pl.north93.northplatform.lobby.npc.PlayNpcNameHandler;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;

@XmlRootElement(name = "playGame")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayGameXmlNpc extends XmlNpc
{
    @XmlTransient
    @Inject
    private PlayNpcNameHandler playNpcNameHandler;
    @XmlTransient
    @Inject
    private IArenaClient       arenaClient;
    
    @XmlElement(required = true)
    private GameIdentity gameIdentity;
    @XmlElement
    private boolean      isDynamic = false;

    @Override
    public NPC loadNpc(final NPCRegistry registry)
    {
        final NPC npc = super.loadNpc(registry);

        npc.addTrait(new PlayGameTrait(this.gameIdentity, this.isDynamic));

        return npc;
    }

    @Override
    protected void postSpawn(final NPC npc)
    {
        npc.addTrait(new TranslatedNameTrait());
        this.playNpcNameHandler.updateName(npc);
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameIdentity", this.gameIdentity).toString();
    }
}

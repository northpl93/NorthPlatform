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
import pl.north93.northplatform.api.minigame.server.shared.citizens.TranslatedNameTrait;
import pl.north93.northplatform.lobby.npc.ShopperTrait;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.messages.Messages;
import pl.north93.northplatform.api.global.messages.MessagesBox;
import pl.north93.northplatform.api.global.messages.TranslatableString;

@XmlRootElement(name = "lobbyShopper")
@XmlAccessorType(XmlAccessType.FIELD)
public class LobbyShopperXmlNpc extends XmlNpc
{
    @XmlTransient
    @Inject @Messages("UserInterface")
    private MessagesBox uiMessages;
    @XmlElement
    private String      gameId; // wartosc z enuma ShopperTrait.Shop

    @Override
    protected NPC loadNpc(final NPCRegistry registry)
    {
        final NPC npc = super.loadNpc(registry);

        final ShopperTrait.Shop shop = ShopperTrait.getById(this.gameId);
        npc.addTrait(new ShopperTrait(shop));

        return npc;
    }

    @Override
    protected void postSpawn(final NPC npc)
    {
        final TranslatableString npcName = TranslatableString.of(this.uiMessages, "@npc.shop");

        final String gameId = ShopperTrait.getById(this.gameId).getGameId();
        final TranslatableString gameName = TranslatableString.of(this.uiMessages, "@npc." + gameId);

        npc.addTrait(new TranslatedNameTrait(npcName, gameName));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameId", this.gameId).toString();
    }
}

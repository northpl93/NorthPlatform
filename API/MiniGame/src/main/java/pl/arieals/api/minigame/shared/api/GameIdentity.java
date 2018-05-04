package pl.arieals.api.minigame.shared.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.Document;

/**
 * Obiekt identyfikujcy w sieci dany typ minigry wraz z jej wariantem.
 * Typ gry to np. elytrarace a wariant to np. score_mode.
 */
@XmlRootElement(name = "gameIdentity")
@XmlAccessorType(XmlAccessType.FIELD)
public final class GameIdentity
{
    @XmlElement(required = true)
    private String gameId;
    @XmlElement(required = true)
    private String variantId;

    public static GameIdentity create(final String gameId, final String variantId)
    {
        final GameIdentity identity = new GameIdentity();
        identity.gameId = gameId;
        identity.variantId = variantId;

        return identity;
    }

    public static GameIdentity create(final Document document)
    {
        final GameIdentity identity = new GameIdentity();
        identity.gameId = document.getString("gameId");
        identity.variantId = document.getString("variantId");

        return identity;
    }

    /**
     * Zwraca identyfikator tej minigry, unikalny we wszystkich minigrach.
     *
     * @return identyfikator gry.
     */
    public String getGameId()
    {
        return this.gameId;
    }

    /**
     * Zwraca wariant minigry. Jedna minigra moze miec kilka wariantow.
     * Na podstawie tej wartosci plugin minigry moze zmieniac dzialanie
     * (ale nie musi).
     *
     * @return wariant gry.
     */
    public String getVariantId()
    {
        return this.variantId;
    }

    public Document toDocument()
    {
        final Document document = new Document();
        document.put("gameId", this.gameId);
        document.put("variantId", this.variantId);

        return document;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }
        final GameIdentity that = (GameIdentity) o;
        return Objects.equals(this.gameId, that.gameId) && Objects.equals(this.variantId, that.variantId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.gameId, this.variantId);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("gameId", this.gameId).append("variantId", this.variantId).toString();
    }
}

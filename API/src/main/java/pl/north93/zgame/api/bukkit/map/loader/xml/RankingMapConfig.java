package pl.north93.zgame.api.bukkit.map.loader.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import pl.north93.zgame.api.bukkit.map.IMapRenderer;
import pl.north93.zgame.api.bukkit.map.renderer.ranking.IRankingRenderer;
import pl.north93.zgame.api.bukkit.map.renderer.ranking.RankingRenderer;
import pl.north93.zgame.api.global.component.annotations.bean.Inject;
import pl.north93.zgame.api.global.uri.IUriManager;

@XmlRootElement(name = "ranking")
@XmlAccessorType(XmlAccessType.FIELD)
public class RankingMapConfig extends MapConfig
{
    @Inject @XmlTransient
    private IUriManager          uriManager;
    @XmlElement(required = true)
    private XmlTranslatableImage background;
    @XmlElement(required = true)
    private String               rankingData;

    @Override
    public IMapRenderer createRenderer()
    {
        final RankingRenderer renderer = new RankingRenderer(this.background);

        final IMapRankingData rankingData = (IMapRankingData) this.uriManager.call(this.rankingData);
        rankingData.setUp(renderer);

        return renderer;
    }

    /**
     * Interfejs który ma za zadanie wprowadzić dane rankingu do IRankingRenderer.
     * Zostaje zwrócony przez wykonanie URLa {@literal northplatform://}.
     */
    public interface IMapRankingData
    {
        void setUp(IRankingRenderer rankingRenderer);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("background", this.background).append("rankingData", this.rankingData).toString();
    }
}

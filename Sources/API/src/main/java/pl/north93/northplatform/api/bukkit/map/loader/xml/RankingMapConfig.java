package pl.north93.northplatform.api.bukkit.map.loader.xml;

import lombok.ToString;
import pl.north93.northplatform.api.bukkit.map.IMapCanvas;
import pl.north93.northplatform.api.bukkit.map.IMapRenderer;
import pl.north93.northplatform.api.bukkit.map.renderer.ranking.IRankingRenderer;
import pl.north93.northplatform.api.bukkit.map.renderer.ranking.RankingRenderer;
import pl.north93.northplatform.api.bukkit.player.INorthPlayer;
import pl.north93.northplatform.api.global.component.annotations.bean.Inject;
import pl.north93.northplatform.api.global.uri.IUriManager;

import javax.xml.bind.annotation.*;
import java.net.URI;

@XmlRootElement(name = "ranking")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString(of = {"background", "rankingData"})
public class RankingMapConfig extends MapConfig
{
    @Inject @XmlTransient
    private IUriManager          uriManager;
    @XmlElement(required = true)
    private XmlTranslatableImage background;
    @XmlElement(required = true)
    private URI                  rankingData;

    @Override
    public IMapRenderer createRenderer()
    {
        /*
         * Klasa uzywana do opóznienia wywolania pobrania danych rankingu.
         * Usuwamy tym samym blad powstajacy gdy mapa zaladuje sie szybciej niz
         * komponent udostepniajacy dane rankingu.
         */
        @ToString(of = {"renderer", "rankingDataUri"})
        class LazyRankingRenderer implements IMapRenderer
        {
            @Inject
            private       IUriManager     uriManager;
            private final RankingRenderer renderer;
            private final URI             rankingDataUri;

            public LazyRankingRenderer(final RankingRenderer renderer, final URI rankingDataUri)
            {
                this.renderer = renderer;
                this.rankingDataUri = rankingDataUri;
            }

            @Override
            public void render(final IMapCanvas canvas, final INorthPlayer player) throws Exception
            {
                final IMapRankingData rankingData = (IMapRankingData) this.uriManager.call(this.rankingDataUri);
                rankingData.setUp(this.renderer);

                this.renderer.render(canvas, player);
            }
        }

        final RankingRenderer renderer = new RankingRenderer(this.background);
        return new LazyRankingRenderer(renderer, this.rankingData);
    }

    /**
     * Interfejs który ma za zadanie wprowadzić dane rankingu do IRankingRenderer.
     * Zostaje zwrócony przez wykonanie URLa {@literal northplatform://}.
     */
    public interface IMapRankingData
    {
        void setUp(IRankingRenderer rankingRenderer);
    }
}
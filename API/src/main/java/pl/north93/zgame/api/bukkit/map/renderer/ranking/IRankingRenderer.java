package pl.north93.zgame.api.bukkit.map.renderer.ranking;

import pl.north93.zgame.api.bukkit.map.IMapRenderer;

public interface IRankingRenderer extends IMapRenderer
{
    void setLeftPlace(int place, RankingEntry rankingEntry);

    void setRightPlace(int place,  RankingEntry rankingEntry);
}

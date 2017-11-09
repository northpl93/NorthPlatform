package pl.arieals.lobby.maps;

import pl.arieals.api.minigame.shared.api.statistics.IRanking;
import pl.arieals.api.minigame.shared.api.statistics.IStatisticUnit;

public interface IMapRanking
{
    IRanking<? extends IStatisticUnit> generateLeftRanking();

    IRanking<? extends IStatisticUnit> generateRightRanking();
}

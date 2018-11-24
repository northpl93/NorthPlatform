package pl.north93.northplatform.globalshops.shared;

/**
 * Typ grupy przedmiotow.
 */
public enum GroupType
{
    /**
     * W tym typie gracz moze kupic dowolna liczbe przedmiotow,
     * ale musi wybrac tylko jeden aktywny.
     */
    SINGLE_PICK,
    /**
     * W tym trybie gracz moze kupic dowolna liczbe przedmiotow,
     * i wszystkie sa zawsze wlaczone.
     */
    MULTI_BUY
}

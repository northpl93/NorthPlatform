package pl.arieals.globalshops.server.domain;

public enum BuyResult
{
    /**
     * Moze kupic.
     */
    CAN_BUY,
    /**
     * Brak pieniedzy.
     */
    NO_MONEY,
    /**
     * Brak wymaganych przedmiotow
     */
    NOT_SATISFIED_DEPENDENCIES,
    /**
     * Osiagniety maksymany poziom przedmiotu.
     */
    MAX_LEVEL
}

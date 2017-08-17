package pl.arieals.globalshops.server;

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
     * Osiagniety maksymany poziom przedmiotu.
     */
    MAX_LEVEL
}

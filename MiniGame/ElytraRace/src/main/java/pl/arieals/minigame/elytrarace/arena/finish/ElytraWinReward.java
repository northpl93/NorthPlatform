package pl.arieals.minigame.elytrarace.arena.finish;

public final class ElytraWinReward
{
    /**
     * Metoda obliczająca nagrodę Elytry dla podanego miejsca.
     *
     * @param totalPlayers Startowa ilość graczy na arenie.
     * @param place Miejsce zajęte przez gracza, indexowane od 0.
     * @return Wartość nagrody za ukończenie areny.
     */
    public static int calculateReward(final int totalPlayers, final int place)
    {
        final int multiplier = totalPlayers - place - 1;
        return 4 + (multiplier * 2);
    }
}

package pl.north93.zgame.api.bukkit.scoreboard;

import java.util.List;

public interface IScoreboardLayout
{
    /**
     * Wywolywane gdy tworzony jest kontekst z przypisanym tym layoutem.
     * Tutaj powinny zostac zainicjowane wszystkie wymagane zmienne.
     *
     * @param context Tworzony kontekst.
     */
    default void initContext(IScoreboardContext context)
    {
    }

    String getTitle(IScoreboardContext context);

    List<String> getContent(IScoreboardContext context);

    default int updateEvery()
    {
        return 0;
    }

    static ContentBuilder builder()
    {
        return new ContentBuilder();
    }
}

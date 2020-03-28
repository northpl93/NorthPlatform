package pl.north93.northplatform.minigame.elytrarace;

public enum ElytraRaceMode
{
    RACE_MODE,
    SCORE_MODE;

    public static ElytraRaceMode fromVariantId(final String variantId)
    {
        switch (variantId)
        {
            case "race":
                return RACE_MODE;
            case "score":
                return SCORE_MODE;
        }

        throw new IllegalArgumentException(variantId);
    }
}

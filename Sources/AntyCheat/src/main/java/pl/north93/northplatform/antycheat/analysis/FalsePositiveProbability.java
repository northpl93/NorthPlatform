package pl.north93.northplatform.antycheat.analysis;

/**
 * Opisuje prawdopodobieństwo wykrycia false positive.
 */
public enum FalsePositiveProbability
{
    /**
     * Na pewno zostal wykryty false-positive, raport jest tylko w celach informacyjnych.
     */
    DEFINITELY,
    /**
     * Wieksze niz zwykle ryzyko wystapienia false-positive.
     */
    HIGH,
    /**
     * Normalne ryzyko wystapienia false-positive.
     */
    MEDIUM,
    /**
     * Niskie ryzyko false-positive.
     * Algorytm jest pewny i mozna mu zaufac.
     */
    LOW
}

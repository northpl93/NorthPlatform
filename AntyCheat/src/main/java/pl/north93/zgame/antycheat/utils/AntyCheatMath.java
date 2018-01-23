package pl.north93.zgame.antycheat.utils;

import org.bukkit.util.Vector;

// Metody matematyczne używające klas Bukkita i używane w AntyCheatcie.
public final class AntyCheatMath
{
    // https://en.wikipedia.org/wiki/Cosine_similarity
    public static double cosineSimilarity(final Vector v1, final Vector v2)
    {
        return v1.dot(v2) / Math.sqrt(v1.dot(v1) * v2.dot(v2));
    }
}

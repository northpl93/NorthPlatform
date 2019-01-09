package pl.arieals.minigame.goldhunter.utils;

public class TimeStringUtils
{
    private TimeStringUtils()
    {
    }
    
    public static String minutesAndSeconds(int seconds)
    {
        int sec = seconds % 60;
        int min = seconds / 60;
        
        return new StringBuilder().append(min).append(':').append(sec < 10 ? "0" : "").append(sec).toString();
    }
}

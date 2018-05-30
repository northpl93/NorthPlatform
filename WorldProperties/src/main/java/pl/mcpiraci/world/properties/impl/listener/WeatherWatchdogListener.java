package pl.mcpiraci.world.properties.impl.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import pl.mcpiraci.world.properties.WorldProperties;
import pl.mcpiraci.world.properties.WorldPropertiesManager;
import pl.north93.zgame.api.bukkit.utils.AutoListener;

public class WeatherWatchdogListener implements AutoListener
{
    private static final Logger logger = LogManager.getLogger();
    
    private final WorldPropertiesManager propertiesManager;
    
    private WeatherWatchdogListener(WorldPropertiesManager propertiesManager)
    {
        this.propertiesManager = propertiesManager;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event)
    {
        WorldProperties properties = propertiesManager.getProperties(event.getWorld());
        
        if ( properties == null || properties.getWeather() == null )
        {
            return;
        }
        
        if ( properties.getWeather().hasRain() != event.toWeatherState() )
        {
            event.setCancelled(true);
            logger.warn("Server attempted to change weather state on world {} when weather should be {}", properties.getWorld(), properties.getWeather());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onThunderChange(ThunderChangeEvent event)
    {
        WorldProperties properties = propertiesManager.getProperties(event.getWorld());
        
        if ( properties == null || properties.getWeather() == null )
        {
            return;
        }
        
        if ( properties.getWeather().hasThundering() != event.toThunderState() )
        {
            event.setCancelled(true);
            logger.warn("Server attempted to change thunder state on world {} when weather should be {}", properties.getWorld(), properties.getWeather());
        }
    }
}

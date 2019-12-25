package thestonedturtle.damagetracker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Damage Tracker"
)
public class DamageTrackerPlugin extends Plugin
{
	@Inject
	private DamageTrackerConfig config;

	@Provides
	DamageTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DamageTrackerConfig.class);
	}
}

package thestonedturtle.damagetracker;

import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import thestonedturtle.damagetracker.performances.PauseablePerformance;

@Slf4j
@PluginDescriptor(
	name = "Damage Tracker"
)
public class DamageTrackerPlugin extends Plugin
{
	@Inject
	private DamageTrackerOverlay overlay;

	@Inject
	private PerformanceManager performanceManager;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private PauseablePerformance performance = new PauseablePerformance();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		performanceManager.addPerformance(performance);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		performanceManager.removePerformance(performance);
	}

	@Subscribe
	public void onOverlayMenuClicked(OverlayMenuClicked c)
	{
		if (!c.getOverlay().equals(overlay))
		{
			return;
		}

		switch (c.getEntry().getOption().toLowerCase())
		{
			case "pause":
				performance.togglePause();
				break;
			case "reset":
				performance.reset();
				break;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGING_IN:
				performance.reset();
				break;
		}
	}
}

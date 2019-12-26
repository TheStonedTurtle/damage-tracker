package thestonedturtle.damagetracker;

import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.WorldType;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.StatChanged;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.http.api.RuneLiteAPI;
import thestonedturtle.damagetracker.npcstats.NpcStats;

@Slf4j
@PluginDescriptor(
	name = "Damage Tracker"
)
public class DamageTrackerPlugin extends Plugin
{
	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
	private static final double HITPOINT_RATIO = 1.33; // Default ratio is 1 dmg dealt = 1.33 hitpoints exp
	private static final double DMM_MULTIPLIER_RATIO = 10;

	private static final ImmutableMap<Integer, NpcStats> statsMap;
	static
	{
		final InputStream data = DamageTrackerPlugin.class.getResourceAsStream("npc_stats.json");
		final Type typeToken = new TypeToken<Map<Integer, NpcStats>>(){}.getType();
		final Map<Integer, NpcStats> x = RuneLiteAPI.GSON.fromJson(new InputStreamReader(data), typeToken);
		statsMap = ImmutableMap.copyOf(x);
	}

	@Inject
	private Client client;

	@Inject
	private DamageTrackerConfig config;

	@Inject
	private DamageTrackerOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private boolean tracking = false;
	@Getter
	private boolean paused = false;
	@Getter
	private Performance performance = new Performance();

	private Actor oldTarget;
	private boolean loginTick = false;
	private double hpExp = 0;

	@Provides
	DamageTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DamageTrackerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
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
			case "reset":
		}
	}

	@Schedule(
		period = 1,
		unit = ChronoUnit.SECONDS
	)
	public void secondTick()
	{
		if (tracking)
		{
			performance.incrementSeconds();
		}
	}

	@Subscribe
	public void onGameTick(final GameTick tick)
	{
		loginTick = false;

		final Player local = client.getLocalPlayer();
		if (local == null)
		{
			oldTarget = null;
		}
		else
		{
			oldTarget = local.getInteracting();
		}
	}

	@Subscribe
	protected void onHitsplatApplied(final HitsplatApplied e)
	{
		if (!tracking || !e.getActor().equals(client.getLocalPlayer()) || e.getHitsplat().getHitsplatType().equals(Hitsplat.HitsplatType.HEAL))
		{
			return;
		}

		performance.addDamageTaken(e.getHitsplat().getAmount());
	}

	@Subscribe
	protected void onStatChanged(final StatChanged event)
	{
		if (!tracking || loginTick || !event.getSkill().equals(Skill.HITPOINTS))
		{
			return;
		}

		final double oldExp = hpExp;
		hpExp = client.getSkillExperience(Skill.HITPOINTS);

		final double diff = hpExp - oldExp;
		if (diff < 1)
		{
			return;
		}

		final double damageDealt = calculateDamageDealt(diff);
		performance.addDamageDealt(damageDealt);
	}

	@Subscribe
	public void onFakeXpDrop(final FakeXpDrop event)
	{
		if (!tracking || !event.getSkill().equals(Skill.HITPOINTS))
		{
			return;
		}

		performance.addDamageDealt(calculateDamageDealt(event.getXp()));
	}

	/**
	 * Calculates damage dealt based on HP xp gained
	 * @param diff HP xp gained
	 * @return damage dealt
	 */
	private double calculateDamageDealt(double diff)
	{
		double damageDealt = diff / HITPOINT_RATIO;
		// DeadMan mode has an XP modifier
		if (client.getWorldType().contains(WorldType.DEADMAN))
		{
			damageDealt = damageDealt / DMM_MULTIPLIER_RATIO;
		}

		// Some NPCs have an XP modifier, account for it here.
		final Player local = client.getLocalPlayer();
		if (local == null)
		{
			return damageDealt;
		}

		Actor a = local.getInteracting();
		if (!(a instanceof NPC))
		{
			// If we are interacting with nothing we may have clicked away at the perfect time fall back to last tick
			if (!(oldTarget instanceof NPC))
			{
				log.warn("Couldn't find current or past target...");
				return damageDealt;
			}

			a = oldTarget;
		}

		final NPC target = (NPC) a;
		final NpcStats npcStats = statsMap.get(target.getId());
		final double modifier = npcStats == null ? 1.0 : npcStats.getExpModifier();

		return damageDealt / modifier;
	}

	// Generic chat message for all performances
	private static String createPerformanceMessage(Performance a)
	{
		return new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Damage dealt: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(a.getDamageDealt()))
			.append(ChatColorType.NORMAL)
			.append(" (Max: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(a.getHighestHitDealt()))
			.append(ChatColorType.NORMAL)
			.append("), Damage Taken: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(a.getDamageTaken()))
			.append(ChatColorType.NORMAL)
			.append(" (Max: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(a.getHighestHitTaken()))
			.append(ChatColorType.NORMAL)
			.append("), Time Spent: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(a.getReadableSecondsSpent())
			.append(ChatColorType.NORMAL)
			.append(" (DPS: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(String.valueOf(a.getDPS()))
			.append(ChatColorType.NORMAL)
			.append(")")
			.build();
	}
}

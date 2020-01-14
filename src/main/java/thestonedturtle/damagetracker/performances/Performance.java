package thestonedturtle.damagetracker.performances;

import java.time.Instant;
import net.runelite.api.Actor;

/**
 * The bare minimum for a performance, use {@link BasicPerformance} for most inheritance needs.
 */
public interface Performance
{
	boolean isTracking();
	Instant getStartTime();
	Instant getEndTime();

	int getDamageTaken();
	void addDamageTaken(int dmg);

	int getDamageDealt();
	void addDamageDealt(double dmg, Actor actor);

	default int getSecondsElapsed()
	{
		final Instant now = getEndTime() == null ? Instant.now() : getEndTime();
		return (int) (now.toEpochMilli() - getStartTime().toEpochMilli()) / 1000;
	}
}

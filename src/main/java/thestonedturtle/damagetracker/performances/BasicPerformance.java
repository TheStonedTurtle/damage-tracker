/*
 * Copyright (c) 2020, TheStonedTurtle <https://github.com/TheStonedTurtle>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package thestonedturtle.damagetracker.performances;

import java.time.Instant;
import lombok.Getter;
import net.runelite.api.Actor;

/**
 * Basic implementation that should handle most damage tracking use cases
 */
@Getter
public class BasicPerformance implements Performance
{
	boolean tracking = true;
	Instant startTime = Instant.now();
	Instant endTime = null;
	int damageTaken = 0;
	int damageDealt = 0;

	@Override
	public void addDamageTaken(int dmg)
	{
		this.damageTaken += dmg;
	}

	@Override
	public void addDamageDealt(double dmg, Actor actor)
	{
		this.damageDealt += dmg;
	}

	public float getDealtDps()
	{
		int seconds = getSecondsElapsed();
		if (seconds == 0)
		{
			return 0;
		}

		return (float) damageDealt / (float) seconds;
	}

	public float getTakenDps()
	{
		int seconds = getSecondsElapsed();
		if (seconds == 0)
		{
			return 0;
		}

		return (float) damageTaken / (float) seconds;
	}

	public void reset()
	{
		startTime = Instant.now();
		endTime = null;
		damageDealt = 0;
		damageTaken = 0;
	}

	/**
	 * Converts this::getSecondsElapsed into minimal human readable format (ss, mm:ss, or hh:mm:ss)
	 * @return
	 */
	public String getReadableSecondsElapsed()
	{
		final double seconds = getSecondsElapsed();
		if (seconds <= 60)
		{
			return String.format("%2.0f", seconds) + "s";
		}

		final double s = seconds % 3600 % 60;
		final double m = Math.floor(seconds % 3600 / 60);
		final double h = Math.floor(seconds / 3600);

		return h < 1 ? String.format("%2.0f:%02.0f", m, s) : String.format("%2.0f:%02.0f:%02.0f", h, m, s);
	}
}

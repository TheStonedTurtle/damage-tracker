/*
 * Copyright (c) 2019, TheStonedTurtle <https://github.com/TheStonedTurtle>
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
package thestonedturtle.damagetracker.npcstats.performances;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BasicPerformance implements Performance
{
	private double damageTaken = 0;
	private double highestHitTaken = 0;
	private double damageDealt = 0;
	private double highestHitDealt = 0;
	private boolean paused = false;
	private boolean enabled = false;

	@Setter
	int lastActivityTick = -1;
	private int ticksSpent = 0;

	@Override
	public void addDamageTaken(double a)
	{
		damageTaken += a;
		if (a > highestHitTaken)
		{
			highestHitTaken = a;
		}
	}

	@Override
	public void addDamageDealt(double a)
	{
		damageDealt += a;
		if (a > highestHitDealt)
		{
			highestHitDealt = a;
		}
	}

	@Override
	public void incrementTicksSpent()
	{
		ticksSpent++;
	}

	@Override
	public int getTicksSpent()
	{
		return ticksSpent;
	}

	@Override
	public void pause()
	{
		paused = true;
	}

	@Override
	public void unpause()
	{
		paused = false;
	}

	@Override
	public void enable()
	{
		enabled = true;
	}

	@Override
	public void disable()
	{
		enabled = false;
	}

	@Override
	public void reset()
	{
		damageDealt = 0;
		highestHitDealt = 0;
		damageTaken = 0;
		highestHitTaken = 0;
		lastActivityTick = -1;
		ticksSpent = 0;
	}
}

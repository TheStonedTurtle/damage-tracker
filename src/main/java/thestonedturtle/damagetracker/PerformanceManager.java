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
package thestonedturtle.damagetracker;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import thestonedturtle.damagetracker.performances.Performance;

@Singleton
public class PerformanceManager
{
	private final List<Performance> performances = new ArrayList<>();
	private final Client client;

	@Inject
	PerformanceManager(Client client, EventBus eventBus)
	{
		this.client = client;
		eventBus.register(this);
	}

	public void addPerformance(Performance p)
	{
		if (performances.contains(p))
		{
			return;
		}

		performances.add(p);
	}

	public void removePerformance(Performance p)
	{
		performances.remove(p);
	}

	@Subscribe
	protected void onHitsplatApplied(final HitsplatApplied e)
	{
		switch (e.getHitsplat().getHitsplatType())
		{
			case DAMAGE:
				// TODO: Update to check for tinted hitsplat (player did damage)
				if (true)
				{
					damageDealt(e.getHitsplat().getAmount(), e.getActor());
				}
				// Intentionally fall through
			case POISON:
			case VENOM:
			case DISEASE:
				if (e.getActor().equals(client.getLocalPlayer()))
				{
					damageTaken(e.getHitsplat().getAmount());
				}
				break;
		}
	}

	private void damageTaken(int damage)
	{
		for (Performance p : performances)
		{
			if (p.isTracking())
			{
				p.addDamageTaken(damage);
			}
		}
	}

	private void damageDealt(int damage, Actor actor)
	{
		for (Performance p : performances)
		{
			if (p.isTracking())
			{
				p.addDamageDealt(damage, actor);
			}
		}
	}
}

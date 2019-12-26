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

import java.text.DecimalFormat;
import net.runelite.api.Client;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;

/**
 * Every type of performance should be based on this
 */
public interface Performance
{
	double getDamageTaken();
	double getHighestHitTaken();
	void addDamageTaken(double dmg);

	double getDamageDealt();
	double getHighestHitDealt();
	void addDamageDealt(double dmg);

	void incrementTicksSpent();
	int getTicksSpent();

	boolean isPaused();
	void pause();
	void unpause();
	default void togglePause()
	{
		if (isPaused())
		{
			unpause();
			return;
		}

		pause();
	}

	boolean isEnabled();
	void enable();
	void disable();

	default boolean isTracking()
	{
		return isEnabled() && !isPaused();
	}

	void reset();
	default void onSubmit()
	{
		reset();
		disable();
	}

	// Some activities require var-bit checks and other stuff that needs to be done rather frequently
	default void onTick(Client client)
	{
	}

	/**
	 * Converts the ticks spent into seconds
	 */
	default double getSecondsSpent()
	{
		// Each tick is .6 seconds
		final double tickLength = 0.6;

		return Math.round(getTicksSpent() * tickLength);
	}

	/**
	 * Converts the result of `this::getSecondsSpent` into human-readable format
	 */
	default String getReadableSecondsSpent()
	{
		final double secondsSpent = getSecondsSpent();
		if (secondsSpent <= 60)
		{
			return String.format("%2.0f", secondsSpent) + "s";
		}

		final double s = secondsSpent % 3600 % 60;
		final double m = Math.floor(secondsSpent % 3600 / 60);
		final double h = Math.floor(secondsSpent / 3600);

		return h < 1 ? String.format("%2.0f:%02.0f", m, s) : String.format("%2.0f:%02.0f:%02.0f", h, m, s);
	}

	default double getDPS()
	{
		return Math.round((getDamageDealt() / getSecondsSpent()) * 100) / 100.00;
	}

	default String getChatMessage()
	{
		final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
		return new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append("Damage dealt: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(getDamageDealt()))
			.append(ChatColorType.NORMAL)
			.append(" (Max: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(getHighestHitDealt()))
			.append(ChatColorType.NORMAL)
			.append("), Damage Taken: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(getDamageTaken()))
			.append(ChatColorType.NORMAL)
			.append(" (Max: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(NUMBER_FORMAT.format(getHighestHitTaken()))
			.append(ChatColorType.NORMAL)
			.append("), Time Spent: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(getReadableSecondsSpent())
			.append(ChatColorType.NORMAL)
			.append(" (DPS: ")
			.append(ChatColorType.HIGHLIGHT)
			.append(String.valueOf(getDPS()))
			.append(ChatColorType.NORMAL)
			.append(")")
			.build();
	}

	/**
	 * Returns the text to display inside overlays
	 */
	default String getOverlayText()
	{
		return "Dealt: " + ((int) getDamageDealt()) + " | Taken: " + ((int) getDamageTaken());
	}
}

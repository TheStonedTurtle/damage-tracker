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
package thestonedturtle.damagetracker.npcstats;

import lombok.Value;

/**
 * Copy of the data object that {@url github.com/deathbeam/osrs-data}'s `NpcStatsDumper.java` uses.
 * https://github.com/deathbeam/osrs-data/blob/master/src/main/java/net/runelite/data/dump/wiki/NpcStatsDumper.java#L56
 */
@Value
public class NpcStats
{
	private String name;
	private final Integer hitpoints;
	private final Integer combatLevel;
	private final Integer slayerLevel;

	private final Integer attackLevel;
	private final Integer strengthLevel;
	private final Integer defenceLevel;
	private final Integer rangeLevel;
	private final Integer magicLevel;

	private final Integer stab;
	private final Integer slash;
	private final Integer crush;
	private final Integer range;
	private final Integer magic;

	private final Integer stabDef;
	private final Integer slashDef;
	private final Integer crushDef;
	private final Integer rangeDef;
	private final Integer magicDef;

	private final Integer bonusAttack;
	private final Integer bonusStrength;
	private final Integer bonusRangeStrength;
	private final Integer bonusMagicDamage;

	private final Boolean poisonImmune;
	private final Boolean venomImmune;

	private final Boolean dragon;
	private final Boolean demon;
	private final Boolean undead;

	/**
	 * Based off the formula found here: https://oldschool.runescape.wiki/w/Combat#Bonus_experience
	 * @return bonus XP modifier
	 */
	public double getExpModifier()
	{
		final double averageLevel = Math.floor((attackLevel + strengthLevel + defenceLevel + hitpoints) / 4);
		final double averageDefBonus = Math.floor((stabDef + slashDef + crushDef) / 3);

		return (1 + Math.floor(averageLevel * (averageDefBonus + bonusStrength + bonusAttack) / 5120) / 40);
	}
}

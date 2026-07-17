package dev.doraemon.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A small rule-based FAQ engine standing in for "knows everything about the
 * world." Every entry is scored by how many of its keywords appear in the
 * asked question; the highest-scoring entry above zero wins, ties broken by
 * declaration order. No network calls, no external services -- everything
 * here is a fixed, hand-written knowledge base of Minecraft world facts.
 */
public final class DoraemonKnowledgeBase {

	private record Entry(String[] keywords, String answer) {
	}

	private static final List<Entry> ENTRIES = new ArrayList<>();

	private static void add(String answer, String... keywords) {
		ENTRIES.add(new Entry(keywords, answer));
	}

	static {
		add("I'm Doraemon! A robot cat from the 22nd century, here to help you out.",
				"who are you", "what are you", "your name");

		add("The Overworld is the dimension you start in -- forests, deserts, oceans, "
				+ "mountains, and everything in between.",
				"overworld");

		add("The Nether is a fiery dimension full of lava, netherrack, and hoglins. "
				+ "One block there equals eight in the Overworld, which makes it great for fast travel.",
				"nether");

		add("The End is home to the Ender Dragon and, once you beat her, the End cities "
				+ "where you can find elytra and shulker boxes.",
				"end", "ender dragon", "elytra");

		add("A Nether portal needs at least a 4x5 obsidian frame, lit with flint and steel.",
				"nether portal", "portal");

		add("Creepers sneak up quietly and explode when they get close -- listen for the hiss "
				+ "and back away fast!",
				"creeper");

		add("Endermen are tall, teleporting mobs that get angry if you look them in the eyes. "
				+ "They carry blocks and only take damage from water indirectly.",
				"enderman", "endermen");

		add("The Wither is a three-headed boss you summon with 4 soul sand (or soul soil) "
				+ "and 3 wither skeleton skulls, arranged in a T shape.",
				"wither");

		add("Diamonds generate most commonly deep underground, especially below Y=-58 in newer "
				+ "versions -- bring an iron pickaxe or better.",
				"diamond", "diamonds");

		add("Netherite is made by combining 4 ancient debris with 4 gold ingots into a netherite "
				+ "ingot, then smithing it onto diamond gear.",
				"netherite");

		add("Hostile mobs spawn in light level 0 -- keep torches around your base to keep things quiet.",
				"mob spawn", "mobs spawn", "spawn mobs", "light level");

		add("Sleeping in a bed skips the night, but only works if it's actually night and no "
				+ "monsters are nearby.",
				"sleep", "bed", "skip night");

		add("A day-night cycle in Minecraft lasts 20 real-world minutes.",
				"day night", "how long is a day", "day cycle");

		add("An enchanting table needs a book, two diamonds, and four obsidian. Surround it with "
				+ "bookshelves for stronger enchantments.",
				"enchant", "enchanting table", "bookshelf");

		add("Brewing stands need blaze powder to run, and you'll want Nether wart as your base "
				+ "potion ingredient.",
				"brew", "brewing", "potion");

		add("Villagers trade based on their profession -- give a farmer a workstation like a "
				+ "composter and they'll start farming trades.",
				"villager", "trading", "trade");

		add("Redstone dust carries a signal up to 15 blocks before it needs a repeater to boost it again.",
				"redstone");

		add("Fall damage kicks in after falling more than 3 blocks -- feather falling boots or a "
				+ "water landing will save you.",
				"fall damage", "feather falling");

		add("Respawn anchors work like a bed in the Nether -- charge them with glowstone, up to "
				+ "four charges each.",
				"respawn anchor");

		add("Experience levels power your enchanting and anvil repairs -- kill mobs, mine ores, "
				+ "smelt items, and breed animals to earn XP.",
				"experience", "xp", "levels");

		add("Breeding animals takes their favorite food -- wheat for cows and sheep, carrots for "
				+ "pigs, seeds for chickens -- then wait for the hearts and a baby appears.",
				"breed", "breeding");

		add("Bring a bucket of water into the Nether and it'll instantly evaporate -- no "
				+ "placing water there!",
				"water nether");

		add("Beds explode if you try to sleep in the Nether or the End -- great for traps, terrible "
				+ "for actually resting.",
				"bed explode", "bed nether");
	}

	private static final String FALLBACK =
			"Hmm, even my knowledge pocket doesn't have that one -- try asking me about mobs, "
					+ "dimensions, mining, farming, or enchanting!";

	private DoraemonKnowledgeBase() {
	}

	public static String answer(String question) {
		String normalized = question.toLowerCase(Locale.ROOT);

		Entry best = null;
		int bestScore = 0;
		for (Entry entry : ENTRIES) {
			int score = 0;
			for (String keyword : entry.keywords()) {
				if (normalized.contains(keyword)) {
					score++;
				}
			}
			if (score > bestScore) {
				bestScore = score;
				best = entry;
			}
		}

		return best != null ? best.answer() : FALLBACK;
	}
}

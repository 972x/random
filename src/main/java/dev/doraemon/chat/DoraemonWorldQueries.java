package dev.doraemon.chat;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

import java.util.Locale;
import java.util.Optional;

/**
 * Live, in-world answers -- "where's the nearest village/sand/water/lava" --
 * as opposed to {@link DoraemonKnowledgeBase}'s fixed trivia. Checked first;
 * the static knowledge base is the fallback for anything not location-based.
 */
public final class DoraemonWorldQueries {

	// locateStructure's radius is in chunks, not blocks.
	private static final int STRUCTURE_SEARCH_RADIUS_CHUNKS = 100;
	private static final int BLOCK_SEARCH_HORIZONTAL_RADIUS = 48;
	private static final int BLOCK_SEARCH_VERTICAL_RADIUS = 16;

	private DoraemonWorldQueries() {
	}

	public static Optional<String> tryAnswer(ServerPlayerEntity player, String question) {
		String lower = question.toLowerCase(Locale.ROOT);

		if (containsAny(lower, "village")) {
			return Optional.of(locateVillage(player));
		}
		if (containsAny(lower, "sand", "beach", "desert")) {
			return Optional.of(locateBlock(player, Blocks.SAND, "sand"));
		}
		if (containsAny(lower, "water", "ocean", "lake", "river")) {
			return Optional.of(locateBlock(player, Blocks.WATER, "water"));
		}
		if (containsAny(lower, "lava")) {
			return Optional.of(locateBlock(player, Blocks.LAVA, "lava"));
		}
		return Optional.empty();
	}

	private static boolean containsAny(String text, String... needles) {
		for (String needle : needles) {
			if (text.contains(needle)) {
				return true;
			}
		}
		return false;
	}

	private static String locateVillage(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		BlockPos origin = player.getBlockPos();
		Pair<BlockPos, RegistryEntry<Structure>> found =
				world.locateStructure(StructureTags.VILLAGE, origin, STRUCTURE_SEARCH_RADIUS_CHUNKS, false);
		if (found == null) {
			return "I couldn't sense a village nearby -- try exploring further out.";
		}
		return describeLocation("village", origin, found.getFirst());
	}

	private static String locateBlock(ServerPlayerEntity player, Block block, String label) {
		ServerWorld world = player.getServerWorld();
		BlockPos origin = player.getBlockPos();
		Optional<BlockPos> found = BlockPos.findClosest(origin, BLOCK_SEARCH_HORIZONTAL_RADIUS,
				BLOCK_SEARCH_VERTICAL_RADIUS, pos -> world.getBlockState(pos).isOf(block));
		if (found.isEmpty()) {
			return "I couldn't find any " + label + " within a good walking distance -- try exploring further out.";
		}
		return describeLocation(label, origin, found.get());
	}

	private static String describeLocation(String label, BlockPos origin, BlockPos target) {
		int dx = target.getX() - origin.getX();
		int dz = target.getZ() - origin.getZ();
		double distance = Math.sqrt((double) dx * dx + (double) dz * dz);
		String direction = compassDirection(dx, dz);
		return String.format(Locale.ROOT,
				"The nearest %s looks to be about %.0f blocks to the %s, around (%d, %d, %d).",
				label, distance, direction, target.getX(), target.getY(), target.getZ());
	}

	private static String compassDirection(int dx, int dz) {
		double angle = Math.toDegrees(Math.atan2(dx, -dz));
		if (angle < 0) {
			angle += 360;
		}
		String[] directions = {"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};
		int index = (int) Math.round(angle / 45.0) % 8;
		return directions[index];
	}
}

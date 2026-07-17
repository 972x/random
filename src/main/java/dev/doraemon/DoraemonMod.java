package dev.doraemon;

import dev.doraemon.chat.DoraemonKnowledgeBase;
import dev.doraemon.chat.DoraemonWorldQueries;
import dev.doraemon.entity.DoraemonEntity;
import dev.doraemon.entity.ModEntities;
import dev.doraemon.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class DoraemonMod implements ModInitializer {

	public static final String MOD_ID = "doraemon";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final double LISTEN_RADIUS = 32.0;

	// One full day-night cycle in ticks -- "the first night has passed" means
	// we've reached this point on the world clock.
	private static final long FIRST_NIGHT_PASSED_TIME = 24000L;
	private static final double SPAWN_MIN_DISTANCE = 10.0;
	private static final double SPAWN_MAX_DISTANCE = 16.0;

	// In-memory only: resets on server restart. A player who logs back in
	// after a restart before their Doraemon has appeared will get another
	// chance to trigger the spawn, which is an acceptable trade-off here.
	private final Set<UUID> greetedPlayers = new HashSet<>();

	@Override
	public void onInitialize() {
		ModItems.register();
		ModEntities.register();
		FabricDefaultAttributeRegistry.register(ModEntities.DORAEMON, DoraemonEntity.createDoraemonAttributes());

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> handleChat(sender, message.getContent().getString()));
		ServerTickEvents.END_WORLD_TICK.register(this::onWorldTick);

		LOGGER.info("Doraemon mod initialized -- he'll show up after your first night!");
	}

	private void onWorldTick(ServerWorld world) {
		if (world.getTimeOfDay() < FIRST_NIGHT_PASSED_TIME) {
			return;
		}
		for (ServerPlayerEntity player : world.getPlayers()) {
			// Mark greeted before attempting so a failure for one player can
			// never spam retries every tick thereafter, and -- critically --
			// so it can never affect the next player in this same loop (each
			// attempt is isolated in its own try/catch below).
			if (greetedPlayers.add(player.getUuid())) {
				try {
					spawnWildDoraemonNear(world, player);
				} catch (Throwable t) {
					LOGGER.error("Doraemon first-night spawn failed for {}", player.getName().getString(), t);
				}
			}
		}
	}

	private void spawnWildDoraemonNear(ServerWorld world, ServerPlayerEntity player) {
		DoraemonEntity doraemon = ModEntities.DORAEMON.create(world);
		if (doraemon == null) {
			return;
		}

		double angle = player.getRandom().nextDouble() * Math.PI * 2.0;
		double distance = SPAWN_MIN_DISTANCE + player.getRandom().nextDouble() * (SPAWN_MAX_DISTANCE - SPAWN_MIN_DISTANCE);
		int x = (int) Math.floor(player.getX() + Math.cos(angle) * distance);
		int z = (int) Math.floor(player.getZ() + Math.sin(angle) * distance);
		BlockPos surface = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));

		doraemon.refreshPositionAndAngles(surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5,
				player.getRandom().nextFloat() * 360.0f, 0.0f);
		doraemon.setPersistent();
		world.spawnEntity(doraemon);

		player.sendMessage(Text.literal("You spot a strange round shape watching you from a distance...")
				.formatted(Formatting.GRAY), false);
	}

	private void handleChat(ServerPlayerEntity sender, String content) {
		try {
			String trimmed = content.trim();
			if (trimmed.isEmpty()) {
				return;
			}

			String lower = trimmed.toLowerCase(Locale.ROOT);
			boolean directedAtDoraemon = lower.startsWith("doraemon") || trimmed.endsWith("?");
			if (!directedAtDoraemon) {
				return;
			}

			Box searchBox = sender.getBoundingBox().expand(LISTEN_RADIUS);
			List<DoraemonEntity> nearby = sender.getWorld().getEntitiesByClass(DoraemonEntity.class, searchBox,
					doraemon -> doraemon.isTamed() && doraemon.isOwner(sender));
			if (nearby.isEmpty()) {
				return;
			}

			String question = lower.startsWith("doraemon") ? trimmed.substring("doraemon".length()).trim() : trimmed;
			question = question.replaceFirst("^[,:\\-\\s]+", "");
			if (question.isEmpty()) {
				return;
			}

			String answer = DoraemonWorldQueries.tryAnswer(sender, question)
					.orElseGet(() -> DoraemonKnowledgeBase.answer(question));
			sender.sendMessage(Text.literal("[Doraemon] ").formatted(Formatting.AQUA)
					.append(Text.literal(answer).formatted(Formatting.WHITE)), false);
		} catch (Throwable t) {
			// A chat listener throwing can be noisy for every message
			// thereafter on some setups -- keep it contained and logged
			// instead of letting it interfere with normal chat.
			LOGGER.error("Doraemon chat handling failed", t);
		}
	}
}

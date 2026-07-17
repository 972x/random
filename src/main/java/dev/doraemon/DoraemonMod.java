package dev.doraemon;

import dev.doraemon.chat.DoraemonKnowledgeBase;
import dev.doraemon.entity.DoraemonEntity;
import dev.doraemon.entity.ModEntities;
import dev.doraemon.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

public class DoraemonMod implements ModInitializer {

	public static final String MOD_ID = "doraemon";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final double LISTEN_RADIUS = 32.0;

	@Override
	public void onInitialize() {
		ModItems.register();
		ModEntities.register();
		FabricDefaultAttributeRegistry.register(ModEntities.DORAEMON, DoraemonEntity.createDoraemonAttributes());

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> handleChat(sender, message.getContent().getString()));

		LOGGER.info("Doraemon mod initialized -- ring the bell and say hello!");
	}

	private void handleChat(ServerPlayerEntity sender, String content) {
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

		String answer = DoraemonKnowledgeBase.answer(question);
		sender.sendMessage(Text.literal("[Doraemon] ").formatted(Formatting.AQUA)
				.append(Text.literal(answer).formatted(Formatting.WHITE)), false);
	}
}

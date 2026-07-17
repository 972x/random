package dev.doraemon.client;

import dev.doraemon.DoraemonMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public final class ModModelLayers {

	public static final EntityModelLayer DORAEMON = new EntityModelLayer(Identifier.of(DoraemonMod.MOD_ID, "doraemon"), "main");

	private ModModelLayers() {
	}
}

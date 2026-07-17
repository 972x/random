package dev.doraemon.client;

import dev.doraemon.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DoraemonModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.DORAEMON, DoraemonEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.DORAEMON, DoraemonEntityRenderer::new);
	}
}

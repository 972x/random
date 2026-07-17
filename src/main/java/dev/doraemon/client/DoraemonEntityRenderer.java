package dev.doraemon.client;

import dev.doraemon.DoraemonMod;
import dev.doraemon.entity.DoraemonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class DoraemonEntityRenderer extends MobEntityRenderer<DoraemonEntity, DoraemonEntityModel> {

	private static final Identifier TEXTURE = Identifier.of(DoraemonMod.MOD_ID, "textures/entity/doraemon.png");

	public DoraemonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new DoraemonEntityModel(context.getPart(ModModelLayers.DORAEMON)), 0.5f);
	}

	@Override
	public Identifier getTexture(DoraemonEntity entity) {
		return TEXTURE;
	}
}

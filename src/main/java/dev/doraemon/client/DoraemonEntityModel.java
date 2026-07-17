package dev.doraemon.client;

import dev.doraemon.entity.DoraemonEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * A hand-built, non-humanoid voxel model: round head, round body, no visible
 * legs (he doesn't canonically have any), stubby arms, and a belly-pocket
 * silhouette baked into the texture rather than the geometry.
 */
public class DoraemonEntityModel extends SinglePartEntityModel<DoraemonEntity> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart armLeft;
	private final ModelPart armRight;

	public DoraemonEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.armLeft = root.getChild("arm_left");
		this.armRight = root.getChild("arm_right");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData rootData = modelData.getRoot();

		rootData.addChild("head",
				ModelPartBuilder.create().uv(0, 0).cuboid(-5.0f, -10.0f, -5.0f, 10, 10, 10),
				ModelTransform.pivot(0.0f, 14.0f, 0.0f));

		rootData.addChild("body",
				ModelPartBuilder.create().uv(0, 20).cuboid(-4.0f, 0.0f, -3.0f, 8, 11, 6),
				ModelTransform.pivot(0.0f, 14.0f, 0.0f));

		rootData.addChild("arm_left",
				ModelPartBuilder.create().uv(36, 20).cuboid(-1.5f, -1.0f, -1.5f, 3, 8, 3),
				ModelTransform.pivot(4.5f, 15.0f, 0.0f));

		rootData.addChild("arm_right",
				ModelPartBuilder.create().uv(36, 32).mirrored().cuboid(-1.5f, -1.0f, -1.5f, 3, 8, 3),
				ModelTransform.pivot(-4.5f, 15.0f, 0.0f));

		rootData.addChild("bell",
				ModelPartBuilder.create().uv(0, 40).cuboid(-1.5f, -1.5f, -3.3f, 3, 3, 1),
				ModelTransform.pivot(0.0f, 18.0f, 0.0f));

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	@Override
	public void setAngles(DoraemonEntity entity, float limbAngle, float limbDistance, float animationProgress,
			float headYaw, float headPitch) {
		this.head.yaw = headYaw * MathHelper.RADIANS_PER_DEGREE;
		this.head.pitch = headPitch * MathHelper.RADIANS_PER_DEGREE;
		this.armLeft.pitch = MathHelper.cos(limbAngle * 0.6662f) * 0.6f * limbDistance;
		this.armRight.pitch = MathHelper.cos(limbAngle * 0.6662f + (float) Math.PI) * 0.6f * limbDistance;
	}
}

package dev.doraemon.entity;

import dev.doraemon.DoraemonMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {

	public static final EntityType<DoraemonEntity> DORAEMON = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(DoraemonMod.MOD_ID, "doraemon"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DoraemonEntity::new)
					.dimensions(EntityDimensions.fixed(0.9f, 1.4f))
					.trackRangeBlocks(10)
					.build());

	private ModEntities() {
	}

	public static void register() {
		// Class-load trigger; registration happens above in the static initializer.
	}
}

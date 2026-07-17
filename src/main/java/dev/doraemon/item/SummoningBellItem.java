package dev.doraemon.item;

import dev.doraemon.entity.DoraemonEntity;
import dev.doraemon.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

/**
 * Doraemon's bell: rings once to summon and bond him to whoever used it.
 * Refuses to summon a second companion if the player already has one nearby.
 */
public class SummoningBellItem extends Item {

	private static final double SEARCH_RADIUS = 24.0;

	public SummoningBellItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		if (world.isClient) {
			return TypedActionResult.success(stack);
		}

		Box searchBox = user.getBoundingBox().expand(SEARCH_RADIUS);
		List<DoraemonEntity> existing = world.getEntitiesByClass(DoraemonEntity.class, searchBox,
				doraemon -> doraemon.isTamed() && doraemon.isOwner(user));
		if (!existing.isEmpty()) {
			user.sendMessage(Text.literal("Doraemon is already nearby!"), true);
			return TypedActionResult.fail(stack);
		}

		ServerWorld serverWorld = (ServerWorld) world;
		DoraemonEntity doraemon = ModEntities.DORAEMON.create(serverWorld);
		if (doraemon == null) {
			return TypedActionResult.fail(stack);
		}

		doraemon.refreshPositionAndAngles(
				user.getX() + (user.getRandom().nextFloat() - 0.5) * 2.0,
				user.getY(),
				user.getZ() + (user.getRandom().nextFloat() - 0.5) * 2.0,
				user.getYaw(), 0.0f);
		doraemon.setOwner(user);
		doraemon.setTamed(true);
		doraemon.setSitting(false);
		doraemon.setPersistent();
		world.spawnEntity(doraemon);
		world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.NEUTRAL, 1.0f, 1.2f);

		if (!user.getAbilities().creativeMode) {
			stack.decrement(1);
		}
		return TypedActionResult.success(stack);
	}
}

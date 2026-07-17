package dev.doraemon.entity;

import dev.doraemon.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * A tame, chatty robot-cat companion. Not affiliated with or endorsed by the
 * Doraemon franchise -- an unofficial, non-commercial fan mob.
 *
 * Canon-accurate touch: real height/weight/chest are all "129.3" -- hence the
 * model proportions below aim for a short, round, decidedly non-humanoid build.
 */
public class DoraemonEntity extends TameableEntity {

	public DoraemonEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createDoraemonAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 20.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.3)
				.add(EntityAttributes.FOLLOW_RANGE, 32.0)
				.add(EntityAttributes.ARMOR, 2.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f, false));
		this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(6, new LookAroundGoal(this));
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (!this.isTamed() && stack.isOf(ModItems.DORAYAKI)) {
			if (!this.getWorld().isClient) {
				if (!player.getAbilities().creativeMode) {
					stack.decrement(1);
				}
				if (this.random.nextInt(3) == 0) {
					this.setOwner(player);
					this.setTamed(true);
					this.navigation.stop();
					this.setTarget(null);
					this.setSitting(false);
					this.setHealth(this.getMaxHealth());
					this.getWorld().sendEntityStatus(this, (byte) 7);
					this.playSound(SoundEvents.ENTITY_CAT_PURR, 1.0f, 1.0f);
				} else {
					this.getWorld().sendEntityStatus(this, (byte) 6);
					this.playSound(SoundEvents.ENTITY_CAT_HISS, 1.0f, 1.0f);
				}
			}
			return ActionResult.SUCCESS;
		}

		if (this.isTamed() && this.isOwner(player) && stack.isEmpty() && hand == Hand.MAIN_HAND) {
			this.setSitting(!this.isSitting());
			return ActionResult.SUCCESS;
		}

		return super.interactMob(player, hand);
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return false;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}
}

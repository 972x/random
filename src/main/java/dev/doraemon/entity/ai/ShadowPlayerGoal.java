package dev.doraemon.entity.ai;

import dev.doraemon.entity.DoraemonEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Before he's tamed, Doraemon shadows the nearest player from a loose,
 * respectful distance instead of walking right up to them -- closing the gap
 * only when they wander too far away.
 */
public class ShadowPlayerGoal extends Goal {

	private static final double MIN_DISTANCE = 8.0;
	private static final double MAX_DISTANCE_SQ = 26.0 * 26.0;
	private static final double CATCH_UP_DISTANCE = 12.0;
	private static final double SPEED = 0.9;

	private final DoraemonEntity doraemon;
	private PlayerEntity target;

	public ShadowPlayerGoal(DoraemonEntity doraemon) {
		this.doraemon = doraemon;
		this.setControls(EnumSet.of(Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (this.doraemon.isTamed()) {
			return false;
		}
		this.target = this.doraemon.getWorld().getClosestPlayer(this.doraemon, 32.0);
		return this.target != null
				&& this.doraemon.squaredDistanceTo(this.target) > MIN_DISTANCE * MIN_DISTANCE;
	}

	@Override
	public boolean shouldContinue() {
		return this.target != null && this.target.isAlive() && !this.doraemon.isTamed()
				&& this.doraemon.squaredDistanceTo(this.target) < MAX_DISTANCE_SQ;
	}

	@Override
	public void start() {
		// Nothing to set up beyond what canStart already resolved.
	}

	@Override
	public void tick() {
		if (this.target == null) {
			return;
		}
		if (this.doraemon.distanceTo(this.target) <= MIN_DISTANCE) {
			this.doraemon.getNavigation().stop();
			return;
		}
		Vec3d awayFromPlayer = this.doraemon.getPos().subtract(this.target.getPos());
		if (awayFromPlayer.lengthSquared() < 1.0E-4) {
			awayFromPlayer = new Vec3d(this.doraemon.getRandom().nextDouble() - 0.5, 0.0,
					this.doraemon.getRandom().nextDouble() - 0.5);
		}
		Vec3d aimPoint = this.target.getPos().add(awayFromPlayer.normalize().multiply(CATCH_UP_DISTANCE));
		this.doraemon.getNavigation().startMovingTo(aimPoint.x, aimPoint.y, aimPoint.z, SPEED);
	}

	@Override
	public void stop() {
		this.target = null;
		this.doraemon.getNavigation().stop();
	}
}

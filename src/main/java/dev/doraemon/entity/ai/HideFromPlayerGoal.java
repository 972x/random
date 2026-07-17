package dev.doraemon.entity.ai;

import dev.doraemon.entity.DoraemonEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Before he's tamed, Doraemon is shy: he bolts if a player gets too close or
 * stares right at him. Sneaking (crouching) lets a patient player close the
 * distance without spooking him -- that's the intended way to sneak up and
 * feed him a Dorayaki.
 */
public class HideFromPlayerGoal extends Goal {

	private static final double PANIC_DISTANCE = 6.0;
	private static final double LOOK_TRIGGER_DISTANCE = 10.0;
	private static final double SAFE_DISTANCE = 14.0;
	private static final double LOOK_DOT_THRESHOLD = 0.85; // roughly a 32-degree cone
	private static final double FLEE_SPEED = 1.15;

	private final DoraemonEntity doraemon;
	private PlayerEntity threat;

	public HideFromPlayerGoal(DoraemonEntity doraemon) {
		this.doraemon = doraemon;
		this.setControls(EnumSet.of(Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (this.doraemon.isTamed()) {
			return false;
		}
		PlayerEntity nearest = this.doraemon.getWorld().getClosestPlayer(this.doraemon, LOOK_TRIGGER_DISTANCE * 2);
		if (nearest == null || nearest.isSneaking()) {
			return false;
		}
		double distance = this.doraemon.distanceTo(nearest);
		if (distance < PANIC_DISTANCE) {
			this.threat = nearest;
			return true;
		}
		if (distance < LOOK_TRIGGER_DISTANCE && isLookingAt(nearest, this.doraemon)) {
			this.threat = nearest;
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldContinue() {
		return this.threat != null && this.threat.isAlive() && !this.doraemon.isTamed()
				&& !this.threat.isSneaking()
				&& this.doraemon.distanceTo(this.threat) < SAFE_DISTANCE;
	}

	@Override
	public void tick() {
		if (this.threat == null) {
			return;
		}
		Vec3d away = this.doraemon.getPos().subtract(this.threat.getPos());
		if (away.lengthSquared() < 1.0E-4) {
			away = new Vec3d(this.doraemon.getRandom().nextDouble() - 0.5, 0.0,
					this.doraemon.getRandom().nextDouble() - 0.5);
		}
		Vec3d fleeTo = this.doraemon.getPos().add(away.normalize().multiply(8.0));
		this.doraemon.getNavigation().startMovingTo(fleeTo.x, fleeTo.y, fleeTo.z, FLEE_SPEED);
	}

	@Override
	public void stop() {
		this.threat = null;
		this.doraemon.getNavigation().stop();
	}

	private static boolean isLookingAt(PlayerEntity player, DoraemonEntity target) {
		Vec3d look = player.getRotationVector().normalize();
		Vec3d toTarget = target.getPos().subtract(player.getEyePos());
		if (toTarget.lengthSquared() < 1.0E-4) {
			return true;
		}
		return look.dotProduct(toTarget.normalize()) > LOOK_DOT_THRESHOLD;
	}
}

package pl.asie.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic Ray Tracer that uses an EntityLivingBase as the base and can detect Entities.
 * @author Vexatos
 */
public class RayTracer {

	private static RayTracer instance = new RayTracer();

	/**
	 * @return The main instance of the RayTracer
	 */
	public static RayTracer instance() {
		if(instance == null) {
			instance = new RayTracer();
		}
		return instance;
	}

	protected RayTraceResult target = null;

	/**
	 * @param entity The {@link EntityLivingBase} to fire from
	 * @param distance The max distance the ray can go
	 */
	public void fire(EntityLivingBase entity, double distance) {
		if(entity.world.isRemote) {
			return;
		}
		this.target = this.rayTrace(entity, distance);
	}

	/**
	 * @return The {@link RayTraceResult} containing the Target Block or Entity
	 */
	@Nullable
	public RayTraceResult getTarget() {
		return this.target;
	}

	@Nullable
	protected RayTraceResult rayTrace(EntityLivingBase entity, double distance) {
		Entity target;
		Vec3d position = new Vec3d(entity.posX, entity.posY, entity.posZ);
		if(entity.getEyeHeight() != 0.12F) {
			position = position.add(0, entity.getEyeHeight(), 0);
		}

		Vec3d look = entity.getLookVec();

		for(double i = 1.0; i < distance; i += 0.2) {
			Vec3d search = position.add(look.x * i, look.y * i, look.z * i);
			AxisAlignedBB searchBox = new AxisAlignedBB(
				search.x - 0.1, search.y - 0.1, search.z - 0.1,
				search.x + 0.1, search.y + 0.1, search.z + 0.1);
			RayTraceResult blockCheck = entity.world.rayTraceBlocks(
				new Vec3d(position.x, position.y, position.z), search, false);
			if(blockCheck != null && blockCheck.typeOfHit == RayTraceResult.Type.BLOCK) {
				/*double d1 = position.squareDistanceTo(blockCheck.hitVec);
				double d2 = position.squareDistanceTo(search);*/
				if(position.squareDistanceTo(blockCheck.hitVec)
					< position.squareDistanceTo(search)) {
					return blockCheck;
				}
			}

			target = getEntity(entity, position, search, look, searchBox, 0.1);
			if(target != null) {
				return new RayTraceResult(target);
			}
		}
		return null;
	}

	@Nullable
	protected Entity getEntity(EntityLivingBase base, Vec3d position, Vec3d search, Vec3d look, AxisAlignedBB searchBox, double v) {
		ArrayList<Entity> entityList = new ArrayList<Entity>();
		List<Entity> entityObjects = base.world.getEntitiesWithinAABB(Entity.class, searchBox);
		for(Entity entity : entityObjects) {
			if(entity != null && entity != base && entity.canBeCollidedWith()) {
				entityList.add(entity);
			}
		}
		if(entityList.size() <= 0) {
			return null;
		}
		Entity entity = null;
		if(entityList.size() > 1) {
			for(Entity e : entityList) {
				if(entity == null || position.squareDistanceTo(new Vec3d(e.posX, e.posY, e.posZ))
					< position.squareDistanceTo(new Vec3d(entity.posX, entity.posY, entity.posZ))) {
					entity = e;
				}
			}
			/*Vec3 newSearch = search.addVector(-v / 2.0 * look.x, -v / 2.0 * look.y, -v / 2.0 * look.z);
			AxisAlignedBB newSearchBox = AxisAlignedBB.getBoundingBox(
				newSearch.x - v / 2.0, newSearch.y - v / 2.0, newSearch.z - v / 2.0,
				newSearch.x + v / 2.0, newSearch.y + v / 2.0, newSearch.z + v / 2.0);
			return getEntity(world, newSearch, look, newSearchBox, v / 2.0);*/
		} else {
			entity = entityList.get(0);
		}
		return entity;
	}
}

package pl.asie.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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

	protected MovingObjectPosition target = null;

	/**
	 * @param entity The {@link EntityLivingBase} to fire from
	 * @param distance The max distance the ray can go
	 */
	public void fire(EntityLivingBase entity, double distance) {
		if(entity.worldObj.isRemote) {
			return;
		}
		this.target = this.rayTrace(entity, distance);
	}

	/**
	 * @return The {@link MovingObjectPosition} containing the Target Block or Entity
	 */
	public MovingObjectPosition getTarget() {
		return this.target;
	}

	protected MovingObjectPosition rayTrace(EntityLivingBase entity, double distance) {
		Entity target;
		final Vec3 position = new Vec3(entity.posX, entity.posY, entity.posZ);
		if(entity.getEyeHeight() != 0.12F) {
			position.addVector(0, entity.getEyeHeight(), 0);
		}

		Vec3 look = entity.getLookVec();

		for(double i = 1.0; i < distance; i += 0.2) {
			Vec3 search = position.addVector(look.xCoord * i, look.yCoord * i, look.zCoord * i);
			AxisAlignedBB searchBox = AxisAlignedBB.fromBounds(
				search.xCoord - 0.1, search.yCoord - 0.1, search.zCoord - 0.1,
				search.xCoord + 0.1, search.yCoord + 0.1, search.zCoord + 0.1);
			MovingObjectPosition blockCheck = entity.worldObj.rayTraceBlocks(
				new Vec3(position.xCoord, position.yCoord, position.zCoord), search, false);
			if(blockCheck != null && blockCheck.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				/*double d1 = position.squareDistanceTo(blockCheck.hitVec);
				double d2 = position.squareDistanceTo(search);*/
				if(position.squareDistanceTo(blockCheck.hitVec)
					< position.squareDistanceTo(search)) {
					return blockCheck;
				}
			}

			target = getEntity(entity, position, search, look, searchBox, 0.1);
			if(target != null) {
				return new MovingObjectPosition(target);
			}
		}
		return null;
	}

	protected Entity getEntity(EntityLivingBase base, Vec3 position, Vec3 search, Vec3 look, AxisAlignedBB searchBox, double v) {
		ArrayList<Entity> entityList = new ArrayList<Entity>();
		List entityObjects = base.worldObj.getEntitiesWithinAABB(Entity.class, searchBox);
		for(Object o : entityObjects) {
			if(o instanceof Entity && o != base && ((Entity) o).canBeCollidedWith()) {
				entityList.add(((Entity) o));
			}
		}
		if(entityList.size() <= 0) {
			return null;
		}
		Entity entity = null;
		if(entityList.size() > 1) {
			for(Entity e : entityList) {
				if(entity == null || position.squareDistanceTo(new Vec3(e.posX, e.posY, e.posZ))
					< position.squareDistanceTo(new Vec3(entity.posX, entity.posY, entity.posZ))) {
					entity = e;
				}
			}
			/*Vec3 newSearch = search.addVector(-v / 2.0 * look.xCoord, -v / 2.0 * look.yCoord, -v / 2.0 * look.zCoord);
			AxisAlignedBB newSearchBox = AxisAlignedBB.getBoundingBox(
				newSearch.xCoord - v / 2.0, newSearch.yCoord - v / 2.0, newSearch.zCoord - v / 2.0,
				newSearch.xCoord + v / 2.0, newSearch.yCoord + v / 2.0, newSearch.zCoord + v / 2.0);
			return getEntity(world, newSearch, look, newSearchBox, v / 2.0);*/
		} else {
			entity = entityList.get(0);
		}
		return entity;
	}
}

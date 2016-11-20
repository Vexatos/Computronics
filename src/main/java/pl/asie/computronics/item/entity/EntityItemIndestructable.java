package pl.asie.computronics.item.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class EntityItemIndestructable extends EntityItem {

	public EntityItemIndestructable(World world, double x, double y, double z) {
		super(world, x, y, z);
		init();
	}

	public EntityItemIndestructable(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
		init();
	}

	public EntityItemIndestructable(World world) {
		super(world);
		init();
	}

	private void init() {
		this.isImmuneToFire = true;
		this.lifespan = 36000;
	}

	@Override
	protected void dealFireDamage(int p_70081_1_) {
		//NO-OP
	}

	@Override
	protected void setOnFireFromLava() {
		//NO-OP
	}

	@Override
	public void setFire(int p_70015_1_) {
		//NO-OP
	}

	@Override
	public boolean isInLava() {
		return this.world.isMaterialInBB(this.getEntityBoundingBox(), Material.LAVA);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}
}

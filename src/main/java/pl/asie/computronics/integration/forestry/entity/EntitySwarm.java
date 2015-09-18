package pl.asie.computronics.integration.forestry.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Vexatos
 */
public class EntitySwarm extends EntityMob {

	public int amplifier;
	public EntityPlayer player;

	public EntitySwarm(EntityPlayer player) {
		super(player.worldObj);
		this.player = player;
	}

	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(player.isDead) {
			this.setDead();
		}
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2 * amplifier);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2);
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float par1) {
		return 0xf0f000;
	}

	protected boolean canDespawn() {
		return false;
	}

	public float getBrightness(float par1) {
		return 1.0F;
	}

	protected String getLivingSound() {
		return "";
	}

	protected String getHurtSound() {
		return "";
	}

	protected String getDeathSound() {
		return "";
	}

	public boolean canBePushed() {
		return false;
	}

	@Override
	protected Entity findPlayerToAttack() {
		return super.findPlayerToAttack();
	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);

	}

	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);
	}
}

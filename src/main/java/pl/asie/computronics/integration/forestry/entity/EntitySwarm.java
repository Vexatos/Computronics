package pl.asie.computronics.integration.forestry.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.apiculture.items.ItemArmorApiarist;
import forestry.apiculture.render.EntityBeeFX;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import pl.asie.computronics.Computronics;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vexatos
 */
public class EntitySwarm extends EntityFlyingCreature {

	public static final DamageSource beeDamageSource = new BeeDamageSource();

	private EntityPlayer player;

	public EntitySwarm(World world) {
		super(world);
		this.setSize(1.0F, 1.0F);
	}

	//private ArrayList<Entity> swarmMembers = new ArrayList<Entity>();
	private int lifespan = 0;

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if(!worldObj.isRemote) {
			if(player == null && lifespan > 0) {
				--lifespan;
				if(lifespan <= 0) {
					this.setDead();
				}
			} else if(player == null || player.isDead
				|| player.isInsideOfMaterial(Material.water) || this.isInsideOfMaterial(Material.water)
				|| (getAttackTarget() == null && player.getDistanceSqToEntity(this) > 2500 && !canEntityBeSeen(player))) {
				this.setDead();
			}
			if(!isTolerant() && worldObj.getTotalWorldTime() % 40 == hashCode() % 40) {
				BiomeGenBase biome = worldObj.getBiomeGenForCoordsBody((int) this.posX, (int) this.posY);
				if(!(biome instanceof BiomeGenDesert) && (worldObj.isRaining() || worldObj.isThundering())) {
					this.setDead();
				}
			}
		} else {
			/*ArrayList<Entity> toKeep = new ArrayList<Entity>();
			for(Entity member : swarmMembers) {
				if(member != null && !member.isDead) {
					toKeep.add(member);
				}
			}
			swarmMembers = toKeep;
			int m = 10;
			int memberCountMax = getAmplifier() * m;
			memberCountMax = Math.max(m, (int) (memberCountMax * (getHealth() / getMaxHealth())));
			if(swarmMembers.size() < memberCountMax) {
				for(int i = 0; i < (memberCountMax - swarmMembers.size()); ++i) {
					double xPos = posX + (width / 2) + (worldObj.rand.nextDouble() * (worldObj.rand.nextBoolean() ? 0.5 : -0.5)),
						yPos = posY + (height / 2) + (worldObj.rand.nextDouble() * (worldObj.rand.nextBoolean() ? 0.5 : -0.5)),
						zPos = posZ + (width / 2) + (worldObj.rand.nextDouble() * (worldObj.rand.nextBoolean() ? 0.5 : -0.5));
					EntitySwarmFX member = new EntitySwarmFX(worldObj, xPos, yPos, zPos, 0xF0F000, this);
					swarmMembers.add(member);
					Computronics.proxy.spawnParticle(member);
				}
			}*/
			//if(worldObj.getTotalWorldTime() % 1 == hashCode() % 1) {
			int m = 10;
			int memberCountMax = getAmplifier() * m;
			memberCountMax = Math.max(Math.min(m, (int) (memberCountMax * (getHealth() / getMaxHealth()))), 1);
			for(int i = 0; i < (memberCountMax); ++i) {
				double xPos = posX /*+ (width / 2f)*/ + (worldObj.rand.nextDouble() * (worldObj.rand.nextBoolean() ? 0.5 : -0.5));
				double yPos = posY + (height / 2f) + (worldObj.rand.nextDouble() * (worldObj.rand.nextBoolean() ? 0.5 : -0.5));
				double zPos = posZ /*+ (width / 2f)*/ + (worldObj.rand.nextDouble() * (worldObj.rand.nextBoolean() ? 0.5 : -0.5));
				EntityBeeFX member = new EntityBeeFX(worldObj, xPos, yPos, zPos, 0f, 0f, 0f, getColor());
				Computronics.proxy.spawnParticle(member);
			}
			//EntityBeeFX member = new EntityBeeFX(worldObj, posX /*+ (width / 2f)*/, posY + (height / 2f), posZ /*+ (width / 2f)*/, 0f, 0f, 0f, 0xF0F000);
			//Computronics.proxy.spawnParticle(member);
			//}
		}
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
		//getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2 * getAmplifier());
		//getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2);
	}

	@Override
	protected void updateEntityActionState() {
		EntityLivingBase target = getAttackTarget();
		if(target != null) {
			if(target.isDead || (this.getDistanceSqToEntity(target) > 25 && !this.canEntityBeSeen(target)) || (target == player && lifespan <= 0)) {
				setAttackTarget(null);
			} else {
				double dist = moveTo(target, target.getEyeHeight(), 0.3f, 1f, 10f);

				//this.faceEntity(target, 10.0F, (float) this.getVerticalFaceSpeed());

				if(dist < 1f && !(target instanceof EntityPlayer && ItemArmorApiarist.wearsItems((EntityPlayer) target, "computronics:swarm", true) >= 4)) {
					target.attackEntityFrom(beeDamageSource, 2 * getAmplifier());
				}
			}
		} else if(player != null && (player.getDistanceSqToEntity(this) < 100 || this.canEntityBeSeen(player))) {
			moveTo(player, 3f, 0.3f, 1f);
		}
	}

	private double moveTo(EntityLivingBase target, double yOffset, float modifier, float xFuzzy) {
		return moveTo(target, yOffset, modifier, xFuzzy, xFuzzy);
	}

	private double moveTo(EntityLivingBase target, double yOffset, float modifier, float xFuzzy, float xFuzzyAttack) {
		double deltaX = (target.posX + (target.width / 2f)) - (posX + (width / 4f));
		double deltaY = (target.posY + yOffset) - (posY + (height / 4f));
		double deltaZ = (target.posZ + (target.width / 2f)) - (posZ + (width / 4f));
		double res = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
		Vec3 vec3 = Vec3.createVectorHelper(deltaX, deltaY, deltaZ);
		//double res = vec3.dotProduct(vec3);
		vec3 = vec3.normalize();
		modifier /= 10f;

		//double nres = res > 0 ? Math.max(res, 0.9) : Math.min(res, -0.9);
		/*double ndeltaX = maxAbs(deltaX, Math.signum(deltaX), 0.1) / nres * modifier;
		double ndeltaY = maxAbs(deltaY, Math.signum(deltaY), 0.1) / nres * modifier;
		double ndeltaZ = maxAbs(deltaZ, Math.signum(deltaZ), 0.1) / nres * modifier;
		motionX += minAbs(ndeltaX, Math.signum(ndeltaX), 0.5);
		motionY += minAbs(ndeltaY, Math.signum(ndeltaY), 0.5);
		motionZ += minAbs(ndeltaZ, Math.signum(ndeltaZ), 0.5);*/
		if(res < 3f * 3f) {
			xFuzzy = xFuzzyAttack;
		}

		double ndeltaX = maxAbs(vec3.xCoord, Math.signum(vec3.xCoord), xFuzzy) * modifier;
		double ndeltaY = maxAbs(vec3.yCoord, Math.signum(vec3.yCoord), xFuzzy) /*vec3.yCoord*/ * modifier;
		double ndeltaZ = maxAbs(vec3.zCoord, Math.signum(vec3.zCoord), xFuzzy) * modifier;
		motionX += minAbs(ndeltaX, Math.signum(ndeltaX), 0.5);
		motionY += minAbs(ndeltaY, Math.signum(ndeltaY), 0.5);
		motionZ += minAbs(ndeltaZ, Math.signum(ndeltaZ), 0.5);

		return res;
	}

	private static double minAbs(double number, double sig, double targetMin) {
		double result = number;
		result = sig > 0 ? Math.min(result, targetMin) : Math.max(result, -targetMin);
		return result;
	}

	private static double maxAbs(double number, double sig, double targetMax) {
		double result = number;
		result = sig > 0 ? Math.max(result, targetMax) : Math.min(result, -targetMax);
		return result;
	}

	@Override
	protected boolean interact(EntityPlayer player) {
		if(!worldObj.isRemote && this.player != null && player == this.player && player.isSneaking() && player.getHeldItem() == null) {
			setDead();
			player.swingItem();
			return true;
		}
		return super.interact(player);
	}

	private static final List<String> damageTypesImmune = Arrays.asList(
		DamageSource.inWall.getDamageType(),
		DamageSource.cactus.getDamageType(),
		DamageSource.anvil.getDamageType(),
		DamageSource.fallingBlock.getDamageType(),
		DamageSource.fall.getDamageType()
	);

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return !(damageTypesImmune.contains(source.getDamageType()) || (this.player != null && source.getEntity() == player))
			&& super.attackEntityFrom(source, Math.min(amount, 2f));

	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {
		super.damageEntity(source, amount);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(20, 0);
		this.dataWatcher.addObject(21, 0xF0F000);
		this.dataWatcher.addObject(22, (byte) 0);
	}

	public void setAmplifier(int amplifier) {
		this.dataWatcher.updateObject(20, amplifier);
		//getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2 * amplifier);
	}

	public int getAmplifier() {
		return this.dataWatcher.getWatchableObjectInt(20);
	}

	public void setColor(int color) {
		this.dataWatcher.updateObject(21, color);
	}

	public int getColor() {
		return this.dataWatcher.getWatchableObjectInt(21);
	}

	public void setTolerant(boolean tolerant) {
		this.dataWatcher.updateObject(22, (byte) (tolerant ? 1 : 0));
	}

	public boolean isTolerant() {
		return this.dataWatcher.getWatchableObjectByte(22) != 0;
	}

	public EntityPlayer getPlayer() {
		return this.player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	public void setAdditionalLifespan(int lifespan) {
		this.lifespan = lifespan;
	}

	@Override
	public void setDead() {
		super.setDead();
		/*for(Entity member : swarmMembers) {
			member.setDead();
		}*/
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender(float par1) {
		return getColor();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public float getBrightness(float par1) {
		return 1.0F;
	}

	@Override
	protected String getLivingSound() {
		return null;
	}

	@Override
	protected String getHurtSound() {
		return null;
	}

	@Override
	protected String getDeathSound() {
		return null;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	public boolean hasCustomNameTag() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.height / 2f;
	}

	@Override
	protected void collideWithNearbyEntities() {
	}

	@Override
	protected void collideWithEntity(Entity entity) {
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		if(tag.hasKey("swarm:amplifier")) {
			setAmplifier(tag.getInteger("swarm:amplifier"));
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("swarm:amplifier", getAmplifier());
	}

	public static class BeeDamageSource extends DamageSource {

		public BeeDamageSource() {
			super("computronics.sting");
			this.setDamageBypassesArmor();
			this.setDamageIsAbsolute();
		}
	}
}

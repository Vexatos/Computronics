package pl.asie.computronics.integration.forestry.entity;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.DefaultBeeModifier;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.ForestryAPI;
import forestry.api.core.IErrorLogic;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenDesert;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.forestry.nanomachines.SwarmProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Vexatos
 */
public class EntitySwarm extends EntityFlyingCreature implements IBeeHousing {

	public static final DamageSource beeDamageSource = new BeeDamageSource("computronics.sting", 5);
	public static final DamageSource beeDamageSourceSelf = new BeeDamageSource("computronics.sting.self", 1);

	private EntityPlayer player;

	public EntitySwarm(World world) {
		super(world);
		this.setSize(1.0F, 1.0F);
		this.inventory = new SwarmHousingInventory();
	}

	public EntitySwarm(World world, double x, double y, double z, ItemStack queen) {
		this(world);
		this.setSize(1.0F, 1.0F);
		this.inventory.setQueen(queen);
		this.setPosition(x, y, z);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	//private ArrayList<Entity> swarmMembers = new ArrayList<Entity>();
	private boolean aggressive = false;

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if(!worldObj.isRemote) {
			if(player != null) {
				if(player.isDead || (player.worldObj.provider.dimensionId != this.worldObj.provider.dimensionId) || this.isInsideOfMaterial(Material.water)
					|| (getAttackTarget() == null
					&& ((player.getDistanceSqToEntity(this) > 2500 && !canEntityBeSeen(player)) || player.isInsideOfMaterial(Material.water)))) {
					this.setDead();
				}
			} else if(!aggressive || getAttackTarget() == null) {
				this.setDead();
			}
			if(!isTolerant() && worldObj.getTotalWorldTime() % 40 == hashCode() % 40) {
				BiomeGenBase biome = worldObj.getBiomeGenForCoordsBody((int) this.posX, (int) this.posY);
				if(!(biome instanceof BiomeGenDesert) && (worldObj.isRaining() || worldObj.isThundering())) {
					this.setDead();
				}
			}
			if(beeLogic.canWork()) {
				beeLogic.doWork();
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
					Computronics.proxy.spawnSwarmParticle(member);
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
				Computronics.proxy.spawnSwarmParticle(worldObj, xPos, yPos, zPos, getColor());
			}
			//EntityBeeFX member = new EntityBeeFX(worldObj, posX /*+ (width / 2f)*/, posY + (height / 2f), posZ /*+ (width / 2f)*/, 0f, 0f, 0f, 0xF0F000);
			//Computronics.proxy.spawnSwarmParticle(member);
			//}
		}
	}

	@Override
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
			if(target.isDead || (target == player && !aggressive) || target.isInsideOfMaterial(Material.water)
				|| (this.getDistanceSqToEntity(target) > 25 && !this.canEntityBeSeen(target))) {
				setAttackTarget(null);
			} else {
				double dist = moveTo(target, target.getEyeHeight(), 0.3f, 1f, 10f);

				//this.faceEntity(target, 10.0F, (float) this.getVerticalFaceSpeed());

				if(dist < 1f && !(target instanceof EntityPlayer && BeeManager.armorApiaristHelper.wearsItems(target, "computronics:swarm", true) >= 4)) {
					target.attackEntityFrom(!aggressive ? beeDamageSource : beeDamageSourceSelf, getAmplifier() + (aggressive ? 1F : 0F));
				}
			}
		} else if(player != null && (player.getDistanceSqToEntity(this) < 100 || this.canEntityBeSeen(player))) {
			if(player.getHeldItem() != null && player.isBlocking()) {
				Vec3 look = player.getLookVec();
				moveTo(player.posX + look.xCoord, player.posY + look.yCoord, player.posZ + look.zCoord,
					player.width / 2f, ((player.height / 2f) + player.getEyeHeight()) / 2f, 0.3f, 1f, 0.5f);
			} else {
				circle(player, 3f, 0.3f, 1f, 1f);
			}
		}
	}

	private double circle(EntityLivingBase target, float yOffset, float modifier, float xFuzzy, float radius) {
		final Vec3 direction;
		{
			Vec3 pos = Vec3.createVectorHelper(posX, posY, posZ);
			double y = pos.yCoord;
			pos = pos.addVector(0, -pos.yCoord, 0);
			Vec3 targetPos = Vec3.createVectorHelper(target.posX, target.posY, target.posZ);
			y = targetPos.yCoord + yOffset - y;
			y /= Math.abs(y);
			targetPos = targetPos.addVector(0, -targetPos.yCoord, 0);
			final Vec3 between = pos.subtract(targetPos);
			final Vec3 betweenX = Vec3.createVectorHelper(between.xCoord / Math.abs(between.xCoord), between.yCoord / Math.abs(between.xCoord), between.zCoord / Math.abs(between.xCoord));
			final double normdist = betweenX.lengthVector();
			Vec3 targetRadius = Vec3.createVectorHelper(betweenX.xCoord * radius / normdist, betweenX.yCoord * radius / normdist, betweenX.zCoord * radius / normdist);
			{
				final Vec3 dir = Vec3.createVectorHelper(0, 1, 0).crossProduct(between).normalize();
				targetRadius = targetRadius.addVector(dir.xCoord, dir.yCoord, dir.zCoord);
			}
			direction = targetRadius.subtract(between).addVector(0, y, 0).normalize();
		}

		modifier /= 10f;

		double res = direction.xCoord * direction.xCoord + direction.yCoord * direction.yCoord + direction.zCoord * direction.zCoord;

		double ndeltaX = maxAbs(direction.xCoord, Math.signum(direction.xCoord), xFuzzy) * modifier;
		double ndeltaY = maxAbs(direction.yCoord, Math.signum(direction.yCoord), xFuzzy) /*vec3.yCoord*/ * modifier;
		double ndeltaZ = maxAbs(direction.zCoord, Math.signum(direction.zCoord), xFuzzy) * modifier;
		motionX += minAbs(ndeltaX, Math.signum(ndeltaX), 0.5);
		motionY += minAbs(ndeltaY, Math.signum(ndeltaY), 0.5);
		motionZ += minAbs(ndeltaZ, Math.signum(ndeltaZ), 0.5);

		return res;
	}

	private double moveTo(EntityLivingBase target, double yOffset, float modifier, float xFuzzy) {
		return moveTo(target, yOffset, modifier, xFuzzy, xFuzzy);
	}

	private double moveTo(EntityLivingBase target, double yOffset, float modifier, float xFuzzy, float xFuzzyAttack) {
		return moveTo(target.posX, target.posY, target.posZ, target.width / 2f, yOffset, modifier, xFuzzy, xFuzzyAttack);
	}

	private double moveTo(double x, double y, double z, double xzOffset, double yOffset, float modifier, float xFuzzy, float xFuzzyAttack) {
		double deltaX = (x + xzOffset) - (posX + (width / 4f));
		double deltaY = (y + yOffset) - (posY + (height / 4f));
		double deltaZ = (z + xzOffset) - (posZ + (width / 4f));
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
			SwarmProvider.swingItem(player, null);
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
		this.dataWatcher.addObject(20, 1);
		this.dataWatcher.addObject(21, 0xF0F000);
		this.dataWatcher.addObject(22, (byte) 0);
	}

	public void setAmplifier(int amplifier) {
		this.dataWatcher.updateObject(20, Math.max(amplifier, 1));
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

	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	@Override
	public void setDead() {
		super.setDead();
		/*for(Entity member : swarmMembers) {
			member.setDead();
		}*/
	}

	@Override
	public void setAttackTarget(EntityLivingBase entity) {
		if(entity != this && (player == null || entity != player)) {
			super.setAttackTarget(entity);
		}
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
		return super.canBePushed();
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
		/*if(!worldObj.isRemote && player != null && player.getHeldItem() != null && player.isBlocking()) {
			super.collideWithNearbyEntities();
		}*/
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		/*if(!worldObj.isRemote && player != null && entity != player && player.getHeldItem() != null && player.isBlocking()) {
			//super.collideWithEntity(entity);
		}*/
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if(!worldObj.isRemote && player != null && entity != player && player.getHeldItem() != null && player.isBlocking()) {
			//entity.attackEntityFrom(new BeeDamageSource(), 2 * getAmplifier());
			entity.motionX /= 2.0D;
			entity.motionY /= 2.0D;
			entity.motionZ /= 2.0D;
			double deltaX = this.posX - entity.posX;
			double deltaZ = this.posZ - entity.posZ;
			double modifier = 0.1D * (getHealth() / getMaxHealth());
			float dist = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
			entity.motionX -= deltaX / dist * modifier;
			entity.motionZ -= deltaZ / dist * modifier;
			/*float health = getHealth();
			float maxHealth = getMaxHealth();
			float diff = health / maxHealth;
			entity.motionX /= 100f * diff;
			entity.motionZ /= 100f * diff;*/
			entity.isAirBorne = true;
			entity.velocityChanged = true;
		}
	}

	@Override
	public void knockBack(Entity entity, float damage, double deltaX, double deltaZ) {
		//super.knockBack(entity, damage, deltaX, deltaZ);
	}

	@Override
	public boolean isPotionApplicable(PotionEffect p_70687_1_) {
		return false;
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

		//private EntityLivingBase entity = null;
		private int numCauses;

		public BeeDamageSource(String key, int numCauses) {
			super(key);
			this.setDamageBypassesArmor();
			this.setDamageIsAbsolute();
			this.numCauses = numCauses;
		}

		@Override
		public IChatComponent func_151519_b(EntityLivingBase victim) {
			EntityLivingBase damager = victim.func_94060_bK();
			String format = "death.attack." + this.damageType + (numCauses > 1 ? "." + (victim.worldObj.rand.nextInt(this.numCauses) + 1) : "");
			String withCauseFormat = format + ".player";
			return damager != null && StatCollector.canTranslate(withCauseFormat) ?
				new ChatComponentTranslation(withCauseFormat, victim.func_145748_c_(), damager.func_145748_c_()) :
				new ChatComponentTranslation(format, victim.func_145748_c_());
		}

		/*public BeeDamageSource(EntityLivingBase entity) {
			this();
			this.entity = entity;
		}

		@Override
		public Entity getEntity() {
			return this.entity;
		}*/
	}

	private final IBeekeepingLogic beeLogic = BeeManager.beeRoot.createBeekeepingLogic(this);
	private final IErrorLogic errorLogic = ForestryAPI.errorStateRegistry.createErrorLogic();
	private final IBeeHousingInventory inventory;
	private final IBeeListener beeListener = new SwarmBeeListener();

	@Override
	public Iterable<IBeeModifier> getBeeModifiers() {
		return Collections.singletonList(SwarmBeeModifier.instance);
	}

	@Override
	public Iterable<IBeeListener> getBeeListeners() {
		return Collections.singletonList(beeListener);
	}

	@Override
	public IBeeHousingInventory getBeeInventory() {
		return inventory;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	@Override
	public EnumTemperature getTemperature() {
		return EnumTemperature.getFromBiome(getBiome(), getX(), getY(), getZ());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().rainfall);
	}

	@Override
	public int getBlockLightValue() {
		return worldObj.getBlockLightValue(getX(), getY(), getZ());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return worldObj.canBlockSeeTheSky(getX(), getY() + 1, getZ());
	}

	@Override
	public World getWorld() {
		return worldObj;
	}

	@Override
	public BiomeGenBase getBiome() {
		return worldObj.getBiomeGenForCoords(getX(), getZ());
	}

	@Override
	public GameProfile getOwner() {
		return player != null ? player.getGameProfile() : null;
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return Vec3.createVectorHelper(posX, posY + 0.25, posZ);
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new ChunkCoordinates(getX(), getY(), getZ());
	}

	public int getX() {
		return MathHelper.floor_double(posX);
	}

	public int getY() {
		return MathHelper.floor_double(posY);
	}

	public int getZ() {
		return MathHelper.floor_double(posZ);
	}

	public static class SwarmHousingInventory implements IBeeHousingInventory {

		private ItemStack queenStack;

		@Override
		public ItemStack getQueen() {
			return queenStack;
		}

		@Override
		public ItemStack getDrone() {
			return null;
		}

		@Override
		public void setQueen(ItemStack stack) {
			this.queenStack = stack;
		}

		@Override
		public void setDrone(ItemStack stack) {
			// NO-OP
		}

		@Override
		public boolean addProduct(ItemStack product, boolean all) {
			return true; // Consume all products without doing anything.
		}
	}

	public class SwarmBeeListener extends DefaultBeeListener {

		@Override
		public void onQueenDeath() {
			super.onQueenDeath();
			setDead();
		}
	}

	public static class SwarmBeeModifier extends DefaultBeeModifier {

		public static final IBeeModifier instance = new SwarmBeeModifier();

		@Override
		public float getTerritoryModifier(IBeeGenome genome, float currentModifier) {
			return 0.5f;
		}

		@Override
		public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
			return 0.0f;
		}

		@Override
		public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float currentModifier) {
			return 1.1f;
		}

		@Override
		public float getProductionModifier(IBeeGenome genome, float currentModifier) {
			return 0.0f;
		}

		@Override
		public float getFloweringModifier(IBeeGenome genome, float currentModifier) {
			return 0.0f;
		}

		@Override
		public float getGeneticDecay(IBeeGenome genome, float currentModifier) {
			return 100f;
		}
	}
}

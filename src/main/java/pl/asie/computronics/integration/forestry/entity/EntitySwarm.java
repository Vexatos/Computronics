package pl.asie.computronics.integration.forestry.entity;

import com.mojang.authlib.GameProfile;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.integration.forestry.nanomachines.SwarmProvider;
import pl.asie.computronics.util.StringUtil;

import javax.annotation.Nullable;
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
		if(!world.isRemote) {
			if(player != null) {
				if(player.isDead || (player.world.provider.getDimension() != this.world.provider.getDimension()) || this.isInsideOfMaterial(Material.WATER)
					|| (getAttackTarget() == null
					&& ((player.getDistanceSq(this) > 2500 && !canEntityBeSeen(player)) || player.isInsideOfMaterial(Material.WATER)))) {
					this.setDead();
				}
			} else if(!aggressive || getAttackTarget() == null) {
				this.setDead();
			}
			if(!isTolerant() && world.getTotalWorldTime() % 40 == hashCode() % 40) {
				Biome biome = world.getBiome(getPosition());
				if(!(biome instanceof BiomeDesert) && (world.isRaining() || world.isThundering())) {
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
					double xPos = posX + (width / 2) + (world.rand.nextDouble() * (world.rand.nextBoolean() ? 0.5 : -0.5)),
						yPos = posY + (height / 2) + (world.rand.nextDouble() * (world.rand.nextBoolean() ? 0.5 : -0.5)),
						zPos = posZ + (width / 2) + (world.rand.nextDouble() * (world.rand.nextBoolean() ? 0.5 : -0.5));
					EntitySwarmFX member = new EntitySwarmFX(world, xPos, yPos, zPos, 0xF0F000, this);
					swarmMembers.add(member);
					Computronics.proxy.spawnSwarmParticle(member);
				}
			}*/
			//if(world.getTotalWorldTime() % 1 == hashCode() % 1) {
			int m = 10;
			int memberCountMax = getAmplifier() * m;
			memberCountMax = Math.max(Math.min(m, (int) (memberCountMax * (getHealth() / getMaxHealth()))), 1);
			for(int i = 0; i < (memberCountMax); ++i) {
				double xPos = posX /*+ (width / 2f)*/ + (world.rand.nextDouble() * (world.rand.nextBoolean() ? 0.5 : -0.5));
				double yPos = posY + (height / 2f) + (world.rand.nextDouble() * (world.rand.nextBoolean() ? 0.5 : -0.5));
				double zPos = posZ /*+ (width / 2f)*/ + (world.rand.nextDouble() * (world.rand.nextBoolean() ? 0.5 : -0.5));
				Computronics.proxy.spawnSwarmParticle(world, xPos, yPos, zPos, getColor());
			}
			//EntityBeeFX member = new EntityBeeFX(world, posX /*+ (width / 2f)*/, posY + (height / 2f), posZ /*+ (width / 2f)*/, 0f, 0f, 0f, 0xF0F000);
			//Computronics.proxy.spawnSwarmParticle(member);
			//}
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		//getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2 * getAmplifier());
		//getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2);
	}

	@Override
	protected void updateAITasks() {
		EntityLivingBase target = getAttackTarget();
		if(target != null) {
			if(target.isDead || (target == player && !aggressive) || target.isInsideOfMaterial(Material.WATER)
				|| (this.getDistanceSq(target) > 25 && !this.canEntityBeSeen(target))) {
				setAttackTarget(null);
			} else {
				double dist = moveTo(target, target.getEyeHeight(), 0.3f, 1f, 10f);

				//this.faceEntity(target, 10.0F, (float) this.getVerticalFaceSpeed());

				if(dist < 1f && !(target instanceof EntityPlayer && BeeManager.armorApiaristHelper.wearsItems(target, "computronics:swarm", true) >= 4)) {
					target.attackEntityFrom(!aggressive ? beeDamageSource : beeDamageSourceSelf, getAmplifier() + (aggressive ? 1F : 0F));
				}
			}
		} else if(player != null && (player.getDistanceSq(this) < 100 || this.canEntityBeSeen(player))) {
			if(player.isActiveItemStackBlocking()) {
				Vec3d look = player.getLookVec();
				moveTo(player.posX + look.x, player.posY + look.y, player.posZ + look.z,
					player.width / 2f, ((player.height / 2f) + player.getEyeHeight()) / 2f, 0.3f, 1f, 0.5f);
			} else {
				circle(player, 3f, 0.3f, 1f, 1f);
			}
		}
	}

	private double circle(EntityLivingBase target, float yOffset, float modifier, float xFuzzy, float radius) {
		final Vec3d direction;
		{
			Vec3d pos = getPositionVector();
			double y = pos.y;
			pos = pos.subtract(0, pos.y, 0);
			Vec3d targetPos = target.getPositionVector();
			y = targetPos.y + yOffset - y;
			if(y != 0.0D) {
				y /= Math.abs(y);
			}
			targetPos = targetPos.subtract(0, targetPos.y, 0);
			Vec3d between = pos.subtract(targetPos);
			if(between.x <= 0.02 && between.y <= 0.02) {
				between = between.add(1, 0, 0);
			}
			Vec3d betweenX = between.scale(1D / Math.abs(between.x));
			Vec3d targetRadius = betweenX.scale(radius / betweenX.length()).add(new Vec3d(0, 1, 0).crossProduct(between).normalize());
			direction = targetRadius.subtract(between).add(0, y, 0).normalize();
		}

		modifier /= 10f;

		double res = direction.x * direction.x + direction.y * direction.y + direction.z * direction.z;

		double ndeltaX = maxAbs(direction.x, Math.signum(direction.x), xFuzzy) * modifier;
		double ndeltaY = maxAbs(direction.y, Math.signum(direction.y), xFuzzy) /*vec3.y*/ * modifier;
		double ndeltaZ = maxAbs(direction.z, Math.signum(direction.z), xFuzzy) * modifier;
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
		Vec3d vec3 = new Vec3d(deltaX, deltaY, deltaZ);
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

		double ndeltaX = maxAbs(vec3.x, Math.signum(vec3.x), xFuzzy) * modifier;
		double ndeltaY = maxAbs(vec3.y, Math.signum(vec3.y), xFuzzy) /*vec3.y*/ * modifier;
		double ndeltaZ = maxAbs(vec3.z, Math.signum(vec3.z), xFuzzy) * modifier;
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
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if(!world.isRemote && this.player != null) {
			if(player == this.player && player.isSneaking() && hand == EnumHand.MAIN_HAND && player.getHeldItem(hand).isEmpty()) {
				setDead();
				SwarmProvider.swingItem(player, hand, null);
				return true;
			}
		}
		return super.processInteract(player, hand);
	}

	private static final List<String> damageTypesImmune = Arrays.asList(
		DamageSource.IN_WALL.getDamageType(),
		DamageSource.CACTUS.getDamageType(),
		DamageSource.ANVIL.getDamageType(),
		DamageSource.FALLING_BLOCK.getDamageType(),
		DamageSource.FALL.getDamageType()
	);

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return !(damageTypesImmune.contains(source.getDamageType()) || (this.player != null && source.getTrueSource() == player))
			&& super.attackEntityFrom(source, Math.min(amount, 2f));

	}

	@Override
	protected void damageEntity(DamageSource source, float amount) {
		super.damageEntity(source, amount);
	}

	private static final DataParameter<Integer> DATAMANAGER_ID_AMPLIFIER = EntityDataManager.createKey(EntitySwarm.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> DATAMANAGER_ID_COLOR = EntityDataManager.createKey(EntitySwarm.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> DATAMANAGER_ID_TOLERANCE = EntityDataManager.createKey(EntitySwarm.class, DataSerializers.BOOLEAN);

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(DATAMANAGER_ID_AMPLIFIER, 1);
		this.dataManager.register(DATAMANAGER_ID_COLOR, 0xF0F000);
		this.dataManager.register(DATAMANAGER_ID_TOLERANCE, false);
	}

	public void setAmplifier(int amplifier) {
		this.dataManager.set(DATAMANAGER_ID_AMPLIFIER, Math.max(amplifier, 1));
		//getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2 * amplifier);
	}

	public int getAmplifier() {
		return this.dataManager.get(DATAMANAGER_ID_AMPLIFIER);
	}

	public void setColor(int color) {
		this.dataManager.set(DATAMANAGER_ID_COLOR, color);
	}

	public int getColor() {
		return this.dataManager.get(DATAMANAGER_ID_COLOR);
	}

	public void setTolerant(boolean tolerant) {
		this.dataManager.set(DATAMANAGER_ID_TOLERANCE, tolerant);
	}

	public boolean isTolerant() {
		return this.dataManager.get(DATAMANAGER_ID_TOLERANCE);
	}

	public EntityPlayer getPlayer() {
		return this.player;
	}

	public void setPlayer(@Nullable EntityPlayer player) {
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
	public void setAttackTarget(@Nullable EntityLivingBase entity) {
		if(entity != this && (player == null || entity != player) && !(entity instanceof FakePlayer)) {
			super.setAttackTarget(entity);
		}
	}

	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		return getColor();
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return super.getSplashSound();
	}

	@Override
	protected SoundEvent getSwimSound() {
		return super.getSwimSound();
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected float getSoundVolume() {
		return 0;
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
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.height / 2f;
	}

	@Override
	protected void collideWithNearbyEntities() {
		/*if(!world.isRemote && player != null && player.getHeldItem() != null && player.isBlocking()) {
			super.collideWithNearbyEntities();
		}*/
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		/*if(!world.isRemote && player != null && entity != player && player.getHeldItem() != null && player.isBlocking()) {
			//super.collideWithEntity(entity);
		}*/
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if(!world.isRemote && player != null && entity != player && player.isActiveItemStackBlocking()) {
			//entity.attackEntityFrom(new BeeDamageSource(), 2 * getAmplifier());
			entity.motionX /= 2.0D;
			entity.motionY /= 2.0D;
			entity.motionZ /= 2.0D;
			double deltaX = this.posX - entity.posX;
			double deltaZ = this.posZ - entity.posZ;
			double modifier = 0.1D * (getHealth() / getMaxHealth());
			float dist = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
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
		beeLogic.readFromNBT(tag);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("swarm:amplifier", getAmplifier());
		beeLogic.writeToNBT(tag);
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
		public ITextComponent getDeathMessage(EntityLivingBase victim) {
			EntityLivingBase damager = victim.getAttackingEntity();
			String format = "death.attack." + this.damageType + (numCauses > 1 ? "." + (victim.world.rand.nextInt(this.numCauses) + 1) : "");
			String withCauseFormat = format + ".player";
			return damager != null && StringUtil.canTranslate(withCauseFormat) ?
				new TextComponentTranslation(withCauseFormat, victim.getDisplayName(), damager.getDisplayName()) :
				new TextComponentTranslation(format, victim.getDisplayName());
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
		return EnumTemperature.getFromBiome(getBiome(), world, getPosition());
	}

	@Override
	public EnumHumidity getHumidity() {
		return EnumHumidity.getFromValue(getBiome().getRainfall());
	}

	@Override
	public int getBlockLightValue() {
		return world.getLightFromNeighbors(getPosition().add(0, +1, 0));
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return world.canBlockSeeSky(getPosition().add(0, +1, 0));
	}

	@Override
	public World getWorldObj() {
		return world;
	}

	@Override
	public Biome getBiome() {
		return world.getBiome(getPosition());
	}

	@Override
	public boolean isRaining() {
		return world.isRainingAt(getPosition().up());
	}

	@Nullable
	@Override
	public GameProfile getOwner() {
		return player != null ? player.getGameProfile() : null;
	}

	@Override
	public Vec3d getBeeFXCoordinates() {
		return new Vec3d(posX, posY + 0.25, posZ);
	}

	@Override
	public IErrorLogic getErrorLogic() {
		return errorLogic;
	}

	@Override
	public BlockPos getCoordinates() {
		return getPosition();
	}

	public static class SwarmHousingInventory implements IBeeHousingInventory {

		private ItemStack queenStack = ItemStack.EMPTY;

		@Override
		public ItemStack getQueen() {
			return queenStack;
		}

		@Nullable
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

package pl.asie.computronics.util.boom;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public class SelfDestruct extends Explosion {

	protected World worldObj;
	protected float explosionSize;

	public SelfDestruct(World world, Entity exploder, double x, double y, double z, float size) {
		super(world, exploder, x, y, z, size, false, true);
		this.worldObj = world;
		this.explosionSize = size;
	}

	//Unfortunately I had to copy a lot of code for this one.
	@Override
	public void doExplosionB(boolean spawnParticles) {
		Vec3 position = getPosition();
		final double
			explosionX = position.xCoord,
			explosionY = position.yCoord,
			explosionZ = position.zCoord;

		this.worldObj.playSoundEffect(explosionX, explosionY, explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);

		for(BlockPos blockpos : this.getAffectedBlockPositions()) {
			Block block = this.worldObj.getBlockState(blockpos).getBlock();

			if(spawnParticles) {
				double d0 = (double) ((float) blockpos.getX() + this.worldObj.rand.nextFloat());
				double d1 = (double) ((float) blockpos.getY() + this.worldObj.rand.nextFloat());
				double d2 = (double) ((float) blockpos.getZ() + this.worldObj.rand.nextFloat());
				double d3 = d0 - explosionX;
				double d4 = d1 - explosionY;
				double d5 = d2 - explosionZ;
				double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
				d3 = d3 / d6;
				d4 = d4 / d6;
				d5 = d5 / d6;
				double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
				d7 = d7 * (double) (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
				d3 = d3 * d7;
				d4 = d4 * d7;
				d5 = d5 * d7;
				this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + explosionX * 1.0D) / 2.0D, (d1 + explosionY * 1.0D) / 2.0D, (d2 + explosionZ * 1.0D) / 2.0D, d3, d4, d5);
				this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
			}

			if(block.getMaterial() != Material.air) {
				if(!this.worldObj.isRemote
					&& blockpos.getX() == Math.round(Math.floor(explosionX))
					&& blockpos.getY() == Math.round(Math.floor(explosionY))
					&& blockpos.getZ() == Math.round(Math.floor(explosionZ))) {
					//This is the case.
					TileEntity tile = this.worldObj.getTileEntity(blockpos);
					if(tile != null && !tile.isInvalid() && tile instanceof IInventory) {
						IInventory inv = (IInventory) tile;
						for(int slot = 0; slot < inv.getSizeInventory(); slot++) {
							ItemStack stack = inv.getStackInSlot(slot);
							if(stack != null && stack.stackSize > 0) {
								inv.setInventorySlotContents(slot, null);
							}
						}
					}
				}
				if(block.canDropFromExplosion(this)) {
					block.dropBlockAsItemWithChance(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), 1.0F / this.explosionSize, 0);
				}

				block.onBlockExploded(this.worldObj, blockpos, this);
			}
		}
	}

	public static void goBoom(World world, BlockPos pos) {
		goBoom(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void goBoom(World world, double xPos, double yPos, double zPos) {
		SelfDestruct explosion = new SelfDestruct(world, null, xPos, yPos, zPos, 4.0F);
		explosion.doExplosionA();
		explosion.doExplosionB(false);

		int x = (int) xPos;
		int y = (int) yPos;
		int z = (int) zPos;

		for(Object playerEntity : world.playerEntities) {
			if(playerEntity instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) playerEntity;

				if(entityplayer.getDistanceSq(xPos, yPos, zPos) < 4096.0D) {
					/*try { //TODO
						Packet p = Computronics.packet.create(Packets.PACKET_COMPUTER_BOOM)
							.writeDouble(xPos)
							.writeDouble(yPos)
							.writeDouble(zPos)
							.writeFloat(4.0F);
						p.writeInt(explosion.affectedBlockPositions.size());

						{
							byte j, k, l;
							for(BlockPos pos : explosion.getAffectedBlockPositions()) {
								j = (byte) (pos.getX() - x);
								k = (byte) (pos.getY() - y);
								l = (byte) (pos.getZ() - z);
								p.writeByte(j);
								p.writeByte(k);
								p.writeByte(l);
							}
						}

						Vec3 motion = explosion.getPlayerKnockbackMap().get(entityplayer);
						float motionX = 0;
						float motionY = 0;
						float motionZ = 0;
						if(motion != null) {
							motionY = (float) motion.xCoord;
							motionX = (float) motion.yCoord;
							motionZ = (float) motion.zCoord;
						}
						p.writeFloat(motionY);
						p.writeFloat(motionX);
						p.writeFloat(motionZ);

						Computronics.packet.sendTo(p, entityplayer);
					} catch(IOException e) {
						e.printStackTrace();
					}*/
				}
			}
		}
	}
}

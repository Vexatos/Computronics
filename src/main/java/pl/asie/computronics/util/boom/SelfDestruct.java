package pl.asie.computronics.util.boom;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.lib.network.Packet;

import java.io.IOException;

/**
 * @author Vexatos
 */
public class SelfDestruct extends Explosion {

	protected World worldObj;
	protected float explosionSize;
	private boolean destroyBlocks;

	public SelfDestruct(World world, Entity exploder, double x, double y, double z, float size, boolean destroyBlocks) {
		super(world, exploder, x, y, z, size, false, true);
		this.worldObj = world;
		this.destroyBlocks = destroyBlocks;
		this.explosionSize = size;
	}

	//Unfortunately I had to copy a lot of code for this one.
	@Override
	public void doExplosionB(boolean spawnParticles) {
		Vec3d position = getPosition();
		final double
			explosionX = position.xCoord,
			explosionY = position.yCoord,
			explosionZ = position.zCoord;

		this.worldObj.playSound(null, explosionX, explosionY, explosionZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, explosionX, explosionY, explosionZ, 1.0D, 0.0D, 0.0D);

		for(BlockPos blockpos : this.getAffectedBlockPositions()) {
			IBlockState state = this.worldObj.getBlockState(blockpos);
			Block block = state.getBlock();

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

			if(block.getMaterial(state) != Material.AIR) {
				if(!this.worldObj.isRemote
					&& blockpos.getX() == Math.round(Math.floor(explosionX))
					&& blockpos.getY() == Math.round(Math.floor(explosionY))
					&& blockpos.getZ() == Math.round(Math.floor(explosionZ))) {
					//This is the case.
					TileEntity tile = this.worldObj.getTileEntity(blockpos);
					if(tile != null && !tile.isInvalid() && tile instanceof IInventory) {
						IInventory inv = (IInventory) tile;
						inv.clear();
					}
				}
				if(block.canDropFromExplosion(this)) {
					block.dropBlockAsItemWithChance(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), 1.0F / this.explosionSize, 0);
				}

				if(destroyBlocks) {
					block.onBlockExploded(this.worldObj, blockpos, this);
				}
			}
		}
	}

	public static void goBoom(World world, BlockPos pos, boolean destroyBlocks) {
		goBoom(world, pos.getX(), pos.getY(), pos.getZ(), destroyBlocks);
	}

	public static void goBoom(World world, double xPos, double yPos, double zPos, boolean destroyBlocks) {
		SelfDestruct explosion = new SelfDestruct(world, null, xPos, yPos, zPos, 4.0F, destroyBlocks);
		explosion.doExplosionA();
		explosion.doExplosionB(false);

		int x = (int) xPos;
		int y = (int) yPos;
		int z = (int) zPos;

		for(EntityPlayer playerEntity : world.playerEntities) {
			if(playerEntity instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) playerEntity;

				if(entityplayer.getDistanceSq(xPos, yPos, zPos) < 4096.0D) {
					try {
						Packet p = Computronics.packet.create(PacketType.COMPUTER_BOOM.ordinal())
							.writeDouble(xPos)
							.writeDouble(yPos)
							.writeDouble(zPos)
							.writeFloat(4.0F)
							.writeByte((byte) (destroyBlocks ? 1 : 0));
						p.writeInt(explosion.getAffectedBlockPositions().size());

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

						Vec3d motion = explosion.getPlayerKnockbackMap().get(entityplayer);
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
					}
				}
			}
		}
	}
}

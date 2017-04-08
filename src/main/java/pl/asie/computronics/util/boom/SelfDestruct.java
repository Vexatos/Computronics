package pl.asie.computronics.util.boom;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
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

	private World worldObj;
	private boolean destroyBlocks;

	public SelfDestruct(World world, Entity exploder, double x, double y, double z, float size, boolean destroyBlocks) {
		super(world, exploder, x, y, z, size);
		this.worldObj = world;
		this.destroyBlocks = destroyBlocks;
	}

	//Unfortunately I had to copy a lot of code for this one.
	@Override
	public void doExplosionB(boolean client) {
		this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if(this.explosionSize >= 2.0F && this.isSmoking) {
			this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
		} else {
			this.worldObj.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
		}

		if(this.isSmoking) {

			for(Object affectedBlockPosition : this.affectedBlockPositions) {
				ChunkPosition chunkposition = (ChunkPosition) affectedBlockPosition;
				int i = chunkposition.chunkPosX;
				int j = chunkposition.chunkPosY;
				int k = chunkposition.chunkPosZ;
				Block block = this.worldObj.getBlock(i, j, k);

				if(client) {
					double d0 = (double) ((float) i + this.worldObj.rand.nextFloat());
					double d1 = (double) ((float) j + this.worldObj.rand.nextFloat());
					double d2 = (double) ((float) k + this.worldObj.rand.nextFloat());
					double d3 = d0 - this.explosionX;
					double d4 = d1 - this.explosionY;
					double d5 = d2 - this.explosionZ;
					double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
					d7 *= (double) (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					this.worldObj.spawnParticle("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
					this.worldObj.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
				}

				if(block.getMaterial() != Material.air) {
					if(!this.worldObj.isRemote
						&& i == MathHelper.floor_double(explosionX)
						&& j == MathHelper.floor_double(explosionY)
						&& k == MathHelper.floor_double(explosionZ)) {
						// This is the case.
						TileEntity tile = this.worldObj.getTileEntity(i, j, k);
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
					if(destroyBlocks) {
						if(i != MathHelper.floor_double(explosionX)
							|| j != MathHelper.floor_double(explosionY)
							|| k != MathHelper.floor_double(explosionZ)) {
							// This is not the case.
							if(block.canDropFromExplosion(this)) {
								block.dropBlockAsItemWithChance(this.worldObj, i, j, k, this.worldObj.getBlockMetadata(i, j, k), 1.0F / this.explosionSize, 0);
							}
						}
						block.onBlockExploded(this.worldObj, i, j, k, this);
					}
				}
			}
		}
	}

	public static void goBoom(World world, double xPos, double yPos, double zPos, boolean destroyBlocks) {
		SelfDestruct explosion = new SelfDestruct(world, null, xPos, yPos, zPos, 4.0F, destroyBlocks);
		explosion.isSmoking = true;
		explosion.isFlaming = false;
		explosion.doExplosionA();
		explosion.doExplosionB(false);

		int x = (int) xPos;
		int y = (int) yPos;
		int z = (int) zPos;

		for(Object playerEntity : world.playerEntities) {
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
						p.writeInt(explosion.affectedBlockPositions.size());

						{
							byte j, k, l;
							for(Object affectedBlockPosition1 : explosion.affectedBlockPositions) {
								ChunkPosition chunkposition = (ChunkPosition) affectedBlockPosition1;
								j = (byte) (chunkposition.chunkPosX - x);
								k = (byte) (chunkposition.chunkPosY - y);
								l = (byte) (chunkposition.chunkPosZ - z);
								p.writeByte(j);
								p.writeByte(k);
								p.writeByte(l);
							}
						}

						Vec3 motion = (Vec3) explosion.func_77277_b().get(entityplayer);
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

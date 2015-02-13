package pl.asie.computronics.oc;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.ManagedEnvironment;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.lib.network.Packet;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class DriverCardBoom extends ManagedEnvironment {
	protected final EnvironmentHost container;

	public DriverCardBoom(EnvironmentHost container) {
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("self_destruct").
			create());
		if(this.node() != null) {
			initOCFilesystem();
		}
	}

	private li.cil.oc.api.network.ManagedEnvironment oc_fs;

	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(Computronics.class, Mods.Computronics, "lua/component/self_destruct"),
			"self_destruct");
		((Component) oc_fs.node()).setVisibility(Visibility.Neighbors);
	}

	@Override
	public void onConnect(final Node node) {
		if(node.host() instanceof Context) {
			node.connect(oc_fs.node());
		}
	}
	// GUI/State

	@Override
	public void onDisconnect(final Node node) {
		if(node.host() instanceof Context) {
			// Remove our file systems when we get disconnected from a
			// computer.
			node.disconnect(oc_fs.node());
		} else if(node == this.node()) {
			// Remove the file system if we are disconnected, because in that
			// case this method is only called once.
			oc_fs.node().remove();
		}
	}
	// Boom code

	private int time = -1;

	@Callback(doc = "function([time:number]):number; Starts the countdown; Will be ticking down until the time is reached. 5 seconds by default. Returns the time set")
	public Object[] start(Context context, Arguments args) {
		if(time >= 0) {
			return new Object[] { -1, "fuse has already been set" };
		}
		double fuse = args.optDouble(0, 5);
		this.time = (int) Math.round(Math.floor(fuse * 20));
		return new Object[] { fuse };
	}

	@Callback(doc = "function():number; Returns the time in seconds left", direct = true)
	public Object[] time(Context context, Arguments args) {
		if(time < 0) {
			return new Object[] { -1, "fuse has not been set" };
		}
		return new Object[] { (double) this.time / 20D };
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void load(NBTTagCompound nbt) {
		super.load(nbt);
		if(oc_fs != null && oc_fs.node() != null) {
			oc_fs.node().load(nbt.getCompoundTag("oc:fs"));
		}
		if(nbt.getBoolean("ticking")) {
			this.time = nbt.getInteger("time");
		}
	}

	@Override
	public void save(NBTTagCompound nbt) {
		super.save(nbt);
		if(oc_fs != null && oc_fs.node() != null) {
			final NBTTagCompound fsNbt = new NBTTagCompound();
			oc_fs.node().save(fsNbt);
			nbt.setTag("oc:fs", fsNbt);
		}
		if(this.time >= 0) {
			nbt.setBoolean("ticking", true);
			nbt.setInteger("time", this.time);
		} else {
			nbt.setBoolean("ticking", false);
		}
	}

	@Override
	public void update() {
		super.update();
		if(this.time < 0) {
			return;
		}
		this.time--;
		if(this.time <= 0) {
			//Bye bye.
			this.time = -1;
			//TODO goBoom();
		}
	}

	public static void clientBoom(Packet p) throws IOException {
		double
			x = p.readDouble(),
			y = p.readDouble(),
			z = p.readDouble();
		float force = p.readFloat();
		Minecraft minecraft = Minecraft.getMinecraft();
		SelfDestruct explosion = new SelfDestruct(minecraft.theWorld,
			null, x,
			y,
			z,
			force);
		int size = p.readInt();
		ArrayList<ChunkPosition> list = new ArrayList<ChunkPosition>(size);
		int i = (int) x;
		int j = (int) y;
		int k = (int) z;
		{
			int j1, k1, l1;
			for(int i1 = 0; i1 < size; ++i1) {
				j1 = p.readByte() + i;
				k1 = p.readByte() + j;
				l1 = p.readByte() + k;
				list.add(new ChunkPosition(j1, k1, l1));
			}
		}
		explosion.affectedBlockPositions = list;
		explosion.doExplosionB(true);
		minecraft.thePlayer.motionX += (double) p.readFloat();
		minecraft.thePlayer.motionY += (double) p.readFloat();
		minecraft.thePlayer.motionZ += (double) p.readFloat();
	}

	private void goBoom() {
		SelfDestruct explosion = new SelfDestruct(container.world(), null, container.xPosition(), container.yPosition(), container.zPosition(), 4.0F);
		explosion.isSmoking = true;
		explosion.isFlaming = false;
		explosion.doExplosionA();
		explosion.doExplosionB(false);

		int x = (int) container.xPosition();
		int y = (int) container.yPosition();
		int z = (int) container.zPosition();

		for(Object playerEntity : container.world().playerEntities) {
			if(playerEntity instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) playerEntity;

				if(entityplayer.getDistanceSq(container.xPosition(), container.yPosition(), container.zPosition()) < 4096.0D) {
					try {
						Packet p = Computronics.packet.create(5)
							.writeDouble(container.xPosition())
							.writeDouble(container.yPosition())
							.writeDouble(container.zPosition())
							.writeFloat(4.0F);
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

	private static class SelfDestruct extends Explosion {

		private World worldObj;

		public SelfDestruct(World world, Entity exploder, double x, double y, double z, float size) {
			super(world, exploder, x, y, z, size);
			this.worldObj = world;
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
						if(i == Math.round(Math.floor(explosionX))
							&& j == Math.round(Math.floor(explosionY))
							&& k == Math.round(Math.floor(explosionZ))) {
							//This is the case.
							this.worldObj.setBlockToAir(i, j, k);
						} else {
							block.onBlockExploded(this.worldObj, i, j, k, this);
						}
					}
				}
			}
		}
	}
}

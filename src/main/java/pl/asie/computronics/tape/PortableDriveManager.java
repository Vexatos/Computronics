package pl.asie.computronics.tape;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.item.ItemPortableTapeDrive;
import pl.asie.computronics.tile.TapeDriveState.State;
import pl.asie.lib.audio.StreamingAudioPlayer;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author Vexatos
 */
public final class PortableDriveManager {

	public static final PortableDriveManager INSTANCE = new PortableDriveManager();

	private PortableDriveManager() {
	}

	private BiMap<String, PortableTapeDrive> drivesServer = HashBiMap.create();
	private BiMap<String, PortableTapeDrive> drivesClient = HashBiMap.create();

	private BiMap<String, PortableTapeDrive> drives(boolean client) {
		return client ? drivesClient : drivesServer;
	}

	public PortableTapeDrive getOrCreate(ItemStack stack, boolean client) {
		NBTTagCompound tag = stack.getTagCompound();
		String id;
		if(tag != null && tag.hasKey("tid")) {
			id = tag.getString("tid");
		} else {
			if(tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			id = UUID.randomUUID().toString();
			tag.setString("tid", id);
		}
		PortableTapeDrive drive = drives(client).get(id);
		if(drive == null) {
			drive = new PortableTapeDrive();
			drive.load(tag);
			add(id, drive, client);
		}
		return drive;
	}

	public void add(String id, PortableTapeDrive drive, boolean client) {
		drives(client).put(id, drive);
	}

	public String getID(PortableTapeDrive drive, boolean client) {
		return drives(client).inverse().get(drive);
	}

	public PortableTapeDrive getTapeDrive(String id, boolean client) {
		return drives(client).get(id);
	}

	public boolean exists(String id, boolean client) {
		return drives(client).containsKey(id);
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(event.phase != TickEvent.Phase.END) {
			return;
		}
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if(o instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) o;
				for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
					ItemStack stack = player.inventory.getStackInSlot(i);
					if(stack != null && stack.getItem() instanceof ItemPortableTapeDrive) {
						PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, player.worldObj.isRemote);
						drive.resetTime();
						drive.updateCarrier(player, stack);
						drive.update();
					}
				}
			}
		}
		Iterator<Map.Entry<String, PortableTapeDrive>> iterator = drives(false).entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, PortableTapeDrive> entry = iterator.next();
			if(entry.getValue().time > 5) {
				entry.getValue().switchState(State.STOPPED);
				iterator.remove();
				entry.getValue().carrier = null;
			} else {
				entry.getValue().time++;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase != TickEvent.Phase.END) {
			return;
		}
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player != null) {
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(stack != null && stack.getItem() instanceof ItemPortableTapeDrive) {
					PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, player.worldObj.isRemote);
					drive.resetTime();
					drive.updateCarrier(player, stack);
					drive.update();
					if(drive.getEnumState() == State.PLAYING) {
						final int codecId = drive.getSourceIdClient();
						if(Computronics.instance.audio.exists(codecId)) {
							StreamingAudioPlayer sound = Computronics.instance.audio.getPlayer(codecId);
							sound.updatePosition("computronics:dfpwm-" + codecId, (float) player.posX, (float) player.posY + 0.5F, (float) player.posZ);
						}
					}
				}
			}
			Iterator<Map.Entry<String, PortableTapeDrive>> iterator = drives(true).entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, PortableTapeDrive> entry = iterator.next();
				if(entry.getValue().time > 5) {
					entry.getValue().switchState(State.STOPPED);
					iterator.remove();
					entry.getValue().carrier = null;
				} else {
					entry.getValue().time++;
				}
			}
		}
	}

	public void onServerStop() {
		drivesServer.clear();
		drivesClient.clear();
	}
}

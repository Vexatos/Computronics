package pl.asie.computronics.tape;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.network.PacketType;
import pl.asie.computronics.tile.TapeDriveState;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(event.phase != TickEvent.Phase.END) {
			return;
		}
		Set<String> toRemove = new HashSet<String>();
		for(Map.Entry<String, PortableTapeDrive> entry : drives(false).entrySet()) {
			if(entry.getValue().time > 5) {
				entry.getValue().switchState(TapeDriveState.State.STOPPED);
				toRemove.add(entry.getKey());
				try {
					Entity carrier = entry.getValue().carrier;
					if(carrier instanceof EntityPlayerMP) {
						Computronics.packet.sendTo(
							Computronics.packet.create(PacketType.PORTABLE_TAPE_STOP.ordinal()).writeString(entry.getKey()),
							((EntityPlayerMP) carrier)
						);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
				entry.getValue().carrier = null;
			} else {
				entry.getValue().time++;
			}
		}
		for(String s : toRemove) {
			drives(false).remove(s);
		}
	}

	@SideOnly(Side.CLIENT)
	public void stopTapeDrive(String id) {
		PortableTapeDrive drive = drives(true).get(id);
		if(drive != null) {
			drive.switchState(TapeDriveState.State.STOPPED);
			drive.updateSound();
			drive.carrier = null;
			drives(true).remove(id);
		}
	}
}

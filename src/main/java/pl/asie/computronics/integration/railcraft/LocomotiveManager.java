package pl.asie.computronics.integration.railcraft;

import com.google.common.collect.MapMaker;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import pl.asie.computronics.Computronics;

import java.util.Map;
import java.util.UUID;

/**
 * @author CovertJaguar, Vexatos
 */
public class LocomotiveManager {
	private final Map<UUID, EntityLocomotiveElectric> carts = new MapMaker().weakValues().makeMap();

	public static LocomotiveManager instance() {
		return Computronics.railcraft.manager;
	}

	public void removeLinkageId(EntityLocomotiveElectric loco) {
		this.carts.remove(this.getLinkageId(loco));
	}

	public UUID getLinkageId(EntityLocomotiveElectric loco) {
		UUID id = loco.getPersistentID();
		if(!isUnloaded(loco)) {
			this.carts.put(id, loco);
		}
		return id;
	}

	private void addLocomotive(EntityLocomotiveElectric loco) {
		UUID id = loco.getPersistentID();
		if(!isUnloaded(loco)) {
			this.carts.put(id, loco);
		}
	}

	public EntityLocomotiveElectric getCartFromUUID(UUID id) {
		EntityLocomotiveElectric cart = this.carts.get(id);
		if(cart != null && isUnloaded(cart)) {
			this.carts.remove(id);
			return null;
		} else {
			return this.carts.get(id);
		}
	}

	private boolean isUnloaded(EntityLocomotiveElectric cart) {
		if(cart == null || cart.isDead || cart.worldObj == null) {
			return true;
		}

		int x = MathHelper.floor_double(cart.posX);
		int z = MathHelper.floor_double(cart.posZ);
		boolean isForced = cart.worldObj.getPersistentChunks().containsKey(new ChunkCoordIntPair(x >> 4, z >> 4));
		byte searchRange = isForced ? (byte) 0 : 32;
		boolean isLoaded = cart.worldObj.checkChunksExist(x - searchRange, 0, z - searchRange, x + searchRange, 0, z + searchRange);
		if(!isLoaded) {
			EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(cart);
			MinecraftForge.EVENT_BUS.post(event);
			isLoaded = event.canUpdate;
		}
		return !isLoaded;
	}

	@SubscribeEvent
	public void onMinecartUpdate(MinecartUpdateEvent event) {
		EntityMinecart cart = event.minecart;
		if(!(cart instanceof EntityLocomotiveElectric)) {
			return;
		}
		EntityLocomotiveElectric loco = (EntityLocomotiveElectric) cart;
		if(loco.isDead) {
			this.removeLinkageId(loco);
		} else {
			this.addLocomotive(loco);
		}
	}
}

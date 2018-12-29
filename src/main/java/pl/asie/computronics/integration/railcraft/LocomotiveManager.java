package pl.asie.computronics.integration.railcraft;

import com.google.common.collect.MapMaker;
import mods.railcraft.api.charge.IBatteryCart;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.computronics.Computronics;

import javax.annotation.Nullable;
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

	@Nullable
	public EntityLocomotiveElectric getCartFromUUID(UUID id) {
		EntityLocomotiveElectric cart = this.carts.get(id);
		if(cart != null && isUnloaded(cart)) {
			this.carts.remove(id);
			return null;
		} else {
			return this.carts.get(id);
		}
	}

	private boolean isUnloaded(@Nullable EntityLocomotiveElectric cart) {
		if(cart == null || cart.isDead || cart.world == null) {
			return true;
		}

		int x = MathHelper.floor(cart.posX);
		int z = MathHelper.floor(cart.posZ);
		boolean isForced = cart.world.getPersistentChunks().containsKey(new ChunkPos(x >> 4, z >> 4));
		byte searchRange = isForced ? (byte) 0 : 32;
		boolean isLoaded = cart.world.isAreaLoaded(new BlockPos(x - searchRange, 0, z - searchRange), new BlockPos(x + searchRange, 0, z + searchRange));
		if(!isLoaded) {
			EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(cart);
			MinecraftForge.EVENT_BUS.post(event);
			isLoaded = event.getCanUpdate();
		}
		return !isLoaded;
	}

	@SubscribeEvent
	public void onMinecartUpdate(MinecartUpdateEvent event) {
		EntityMinecart cart = event.getMinecart();
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

	@CapabilityInject(IBatteryCart.class)
	public static Capability<IBatteryCart> CHARGE_CART_CAPABILITY;

	@Nullable
	public static IBatteryCart getCartBattery(ICapabilityProvider provider) {
		return provider.hasCapability(CHARGE_CART_CAPABILITY, null) ? provider.getCapability(CHARGE_CART_CAPABILITY, null) : null;
	}
}

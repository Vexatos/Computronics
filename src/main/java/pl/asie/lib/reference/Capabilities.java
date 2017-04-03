package pl.asie.lib.reference;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.lib.util.internal.IColorable;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class Capabilities {

	public static final Capabilities INSTANCE = new Capabilities();

	@CapabilityInject(IColorable.class)
	public static Capability<IColorable> COLORABLE_CAPABILITY;
	private static final ResourceLocation COLORABLE_KEY = new ResourceLocation("computronics:colorable");

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		CapabilityManager.INSTANCE.register(IColorable.class, new ColorableStorage(), DefaultColorable.class);
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> e) {
		final TileEntity tile = e.getObject();
		if(tile instanceof IColorable) {
			e.addCapability(COLORABLE_KEY, new ICapabilityProvider() {
				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
					return capability == COLORABLE_CAPABILITY;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
					return hasCapability(capability, facing) ? COLORABLE_CAPABILITY.<T>cast(((IColorable) tile)) : null;
				}
			});
		}
	}

	private static class ColorableStorage implements Capability.IStorage<IColorable> {

		@Override
		public NBTBase writeNBT(Capability<IColorable> capability, IColorable instance, EnumFacing side) {
			return new NBTTagInt(instance.getColor());
		}

		@Override
		public void readNBT(Capability<IColorable> capability, IColorable instance, EnumFacing side, NBTBase nbt) {
			if(nbt instanceof NBTTagInt) {
				instance.setColor(((NBTTagInt) nbt).getInt());
			}
		}
	}

	private static class DefaultColorable implements IColorable {

		private int color = 0;

		@Override
		public boolean canBeColored() {
			return false;
		}

		@Override
		public int getColor() {
			return color;
		}

		@Override
		public int getDefaultColor() {
			return 0;
		}

		@Override
		public void setColor(int color) {
			this.color = color;
		}
	}

	private Capabilities() {
	}
}

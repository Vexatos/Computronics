package pl.asie.computronics.integration.charset.wires;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.charset.api.wires.IBundledEmitter;
import pl.asie.charset.api.wires.IBundledReceiver;
import pl.asie.computronics.util.internal.IComputronicsPeripheral;
import pl.asie.lib.api.tile.IBundledRedstoneProvider;

/**
 * @author Vexatos
 */
public class ComputronicsBundledRedstoneIntegration {

	private static Capability<IBundledEmitter> CHARSET_EMITTER = null;
	private static Capability<IBundledReceiver> CHARSET_RECEIVER = null;

	@CapabilityInject(IBundledEmitter.class)
	private static void onBundledEmitterrInject(Capability<IBundledEmitter> c) {
		CHARSET_EMITTER = c;
		IntegrationCharsetWires.bundledRedstone.register();
	}

	@CapabilityInject(IBundledReceiver.class)
	private static void onBundledReceiverInject(Capability<IBundledReceiver> c) {
		CHARSET_RECEIVER = c;
		IntegrationCharsetWires.bundledRedstone.register();
	}

	public boolean isEmitter(ICapabilityProvider tile, EnumFacing side) {
		return tile.hasCapability(CHARSET_EMITTER, side);
	}

	public boolean isReceiver(ICapabilityProvider tile, EnumFacing side) {
		return tile.hasCapability(CHARSET_RECEIVER, side);
	}

	private boolean registered = false;

	private void register() {
		if(!registered) {
			MinecraftForge.EVENT_BUS.register(this);
			registered = true;
		}
	}

	private static final ResourceLocation charsetBundledRedstoneID = new ResourceLocation("computronics", "charset_bundled_rs");

	@SubscribeEvent
	public void onCapabilityAttach(AttachCapabilitiesEvent.TileEntity e) {
		if(e.getTileEntity() instanceof IComputronicsPeripheral && e.getTileEntity() instanceof IBundledRedstoneProvider) {
			e.addCapability(charsetBundledRedstoneID, new CharsetCapabilityProvider(e.getTileEntity()));
		}
	}

	public static class TileCache {

		protected final EnumFacing side;
		protected final TileEntity tile;
		protected final IBundledRedstoneProvider br;

		protected TileCache(TileEntity tile, EnumFacing side) {
			this.tile = tile;
			this.br = (IBundledRedstoneProvider) tile;
			this.side = side;
		}
	}

	public static class ComputronicsBundledEmitter extends TileCache implements IBundledEmitter {

		protected ComputronicsBundledEmitter(TileEntity tile, EnumFacing side) {
			super(tile, side);
		}

		@Override
		public byte[] getBundledSignal() {
			return br.getBundledOutput(side);
		}
	}

	public static class ComputronicsBundledReceiver extends TileCache implements IBundledReceiver {

		protected ComputronicsBundledReceiver(TileEntity tile, EnumFacing side) {
			super(tile, side);
		}

		@Override
		public void onBundledInputChange() {
			TileEntity tile = this.tile.getWorld().getTileEntity(this.tile.getPos().offset(side));
			if(tile.hasCapability(CHARSET_EMITTER, side)) {
				br.onBundledInputChange(side, tile.getCapability(CHARSET_EMITTER, side).getBundledSignal());
			}
		}
	}

	private static class CharsetCapabilityProvider implements ICapabilityProvider {

		private final TileEntity tile;
		private final IBundledRedstoneProvider br;
		private final ComputronicsBundledEmitter[] EMITTERS = new ComputronicsBundledEmitter[6];
		private final ComputronicsBundledReceiver[] RECEIVERS = new ComputronicsBundledReceiver[6];

		public CharsetCapabilityProvider(TileEntity tile) {
			this.tile = tile;
			this.br = ((IBundledRedstoneProvider) tile);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return (capability != null) &&
				((capability == CHARSET_EMITTER
					&& br.canBundledConnectToOutput(facing)) ||
					((capability == CHARSET_RECEIVER)
						&& br.canBundledConnectToInput(facing)));
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if(capability == null) {
				return null;
			}
			if(capability == CHARSET_EMITTER && br.canBundledConnectToOutput(facing)) {
				return (T) getEmitter(facing);
			} else if(capability == CHARSET_RECEIVER && br.canBundledConnectToInput(facing)) {
				return (T) getReceiver(facing);
			}
			return null;
		}

		private ComputronicsBundledEmitter getEmitter(EnumFacing facing) {
			if(EMITTERS[facing.ordinal()] == null) {
				EMITTERS[facing.ordinal()] = new ComputronicsBundledEmitter(tile, facing);
			}
			return EMITTERS[facing.ordinal()];
		}

		private ComputronicsBundledReceiver getReceiver(EnumFacing facing) {
			if(RECEIVERS[facing.ordinal()] == null) {
				RECEIVERS[facing.ordinal()] = new ComputronicsBundledReceiver(tile, facing);
			}
			return RECEIVERS[facing.ordinal()];
		}
	}
}

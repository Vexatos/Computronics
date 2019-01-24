package pl.asie.computronics.integration.charset.wires;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.redstone.IBundledRedstoneProvider;
import dan200.computercraft.shared.computer.blocks.IComputerTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pl.asie.charset.api.wires.IBundledEmitter;
import pl.asie.charset.api.wires.IBundledReceiver;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
@Optional.Interface(iface = "import dan200.computercraft.api.redstone.IBundledRedstoneProvider", modid = Mods.ComputerCraft)
public class CCBundledRedstoneIntegration implements IBundledRedstoneProvider {

	private static Capability<IBundledEmitter> CHARSET_EMITTER = null;
	private static Capability<IBundledReceiver> CHARSET_RECEIVER = null;

	@CapabilityInject(IBundledEmitter.class)
	private static void onBundledEmitterrInject(Capability<IBundledEmitter> c) {
		Computronics.log.info("Adding Charset Bundled Cable support to ComputerCraft. Bundled Cables can now transmit ComputerCraft bundled signals!");
		CHARSET_EMITTER = c;
		IntegrationCharsetWires.bundledRedstoneCC.register();
	}

	@CapabilityInject(IBundledReceiver.class)
	private static void onBundledReceiverInject(Capability<IBundledReceiver> c) {
		Computronics.log.info("Adding Charset Bundled Cable support to ComputerCraft. ComputerCraft can now read values from Bundled Cables!");
		CHARSET_RECEIVER = c;
		ComputerCraftAPI.registerBundledRedstoneProvider(IntegrationCharsetWires.bundledRedstoneCC);
		IntegrationCharsetWires.bundledRedstoneCC.register();
	}

	private boolean registered = false;

	private void register() {
		if(!registered) {
			MinecraftForge.EVENT_BUS.register(this);
			registered = true;
		}
	}

	private static final ResourceLocation charsetBundledRedstoneID = new ResourceLocation(Mods.Computronics, "charset_bundled_rs_cc");

	@SubscribeEvent
	@Optional.Method(modid = Mods.ComputerCraft)
	public void onCapabilityAttach(AttachCapabilitiesEvent<TileEntity> e) {
		if(e.getObject() instanceof IComputerTile) {
			e.addCapability(charsetBundledRedstoneID, new CharsetCapabilityProvider(e.getObject()));
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public int getBundledRedstoneOutput(World world, BlockPos pos, EnumFacing side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile != null && tile.hasCapability(CHARSET_EMITTER, side)) {
			IBundledEmitter emitter = tile.getCapability(CHARSET_EMITTER, side);
			byte[] data = emitter.getBundledSignal();
			if(data != null) {
				int out = 0;
				for(int j = 0; j < data.length; j++) {
					if(data[j] != 0) {
						out |= (1 << j);
					}
				}
				return out;
			}
		}
		return -1;
	}

	public static class TileCache {

		protected final EnumFacing side;
		protected final TileEntity tile;

		protected TileCache(TileEntity tile, EnumFacing side) {
			this.tile = tile;
			this.side = side;
		}
	}

	public static class CCBundledEmitter extends TileCache implements IBundledEmitter {

		protected CCBundledEmitter(TileEntity tile, EnumFacing side) {
			super(tile, side);
		}

		@Override
		public byte[] getBundledSignal() {
			int out = ComputerCraftAPI.getBundledRedstoneOutput(tile.getWorld(), tile.getPos(), side);
			if(out < 0) {
				return new byte[16];
			}
			byte[] data = new byte[16];
			for(int i = 0; i < 16; i++) {
				data[i] = (byte) ((out & (1 << i)) != 0 ? 15 : 0);
			}
			return data;
		}
	}

	public static class CCBundledReceiver extends TileCache implements IBundledReceiver {

		protected CCBundledReceiver(TileEntity tile, EnumFacing side) {
			super(tile, side);
		}

		@Override
		public void onBundledInputChange() {
			tile.getWorld().neighborChanged(tile.getPos(), tile.getWorld().getBlockState(tile.getPos().offset(side)).getBlock(), tile.getPos().offset(side));
		}
	}

	private static class CharsetCapabilityProvider implements ICapabilityProvider {

		private final TileEntity tile;
		private final CCBundledEmitter[] EMITTERS = new CCBundledEmitter[6];
		private final CCBundledReceiver[] RECEIVERS = new CCBundledReceiver[6];

		public CharsetCapabilityProvider(TileEntity tile) {
			this.tile = tile;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability != null &&
				(capability == CHARSET_EMITTER ||
					capability == CHARSET_RECEIVER);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if(capability == null) {
				return null;
			}
			if(capability == CHARSET_EMITTER) {
				return (T) getEmitter(facing);
			} else if(capability == CHARSET_RECEIVER) {
				return (T) getReceiver(facing);
			}
			return null;
		}

		private CCBundledEmitter getEmitter(EnumFacing facing) {
			if(EMITTERS[facing.ordinal()] == null) {
				EMITTERS[facing.ordinal()] = new CCBundledEmitter(tile, facing);
			}
			return EMITTERS[facing.ordinal()];
		}

		private CCBundledReceiver getReceiver(EnumFacing facing) {
			if(RECEIVERS[facing.ordinal()] == null) {
				RECEIVERS[facing.ordinal()] = new CCBundledReceiver(tile, facing);
			}
			return RECEIVERS[facing.ordinal()];
		}
	}
}

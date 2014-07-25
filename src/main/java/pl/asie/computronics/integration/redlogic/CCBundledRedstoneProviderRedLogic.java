package pl.asie.computronics.integration.redlogic;

import pl.asie.computronics.Computronics;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IConnectable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import dan200.computercraft.api.redstone.IBundledRedstoneProvider;

public class CCBundledRedstoneProviderRedLogic implements
		IBundledRedstoneProvider {
	@Override
	public int getBundledRedstoneOutput(World world, int x, int y, int z,
			int side) {
		TileEntity inputTE = world.getTileEntity(x, y, z);
		if(inputTE instanceof IBundledEmitter) {
			IBundledEmitter inputWire = (IBundledEmitter)inputTE;
			for(int i = -1; i < 6; i++) {
				byte[] data = inputWire.getBundledCableStrength(i, side);
				if(data != null) {
					int out = 0;
					for(int j = 0; j < 16; j++)
						if(data[j] != 0) out |= (1<<j);
					return out;
				}
			}
			return 0;
		} else return -1;
	}
}

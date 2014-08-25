package pl.asie.computronics.integration.projectred;

import dan200.computercraft.api.redstone.IBundledRedstoneProvider;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CCBundledRedstoneProviderProjectRed implements
		IBundledRedstoneProvider {
	@Override
	public int getBundledRedstoneOutput(World world, int x, int y, int z,
			int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		byte[] data = ProjectRedAPI.transmissionAPI.getBundledInput(world, x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ, side);
		if(data != null) {
			int out = 0;
			for(int j = 0; j < 16; j++)
				if(data[j] != 0) out |= (1<<j);
			return out;
		} else return -1;
	}
}

package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileIronNote;
//import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
//import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

/*@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode", modid = Mods.MFR)
})*/
public class BlockIronNote extends BlockPeripheral /*implements IRedNetInputNode*/ {

	public BlockIronNote() {
		super("iron_noteblock", Rotation.NONE);
		this.setTranslationKey("computronics.ironNoteBlock");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileIronNote();
	}

	@Override
	public boolean supportsBundledRedstone() {
		return true;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos otherPos) {
		super.neighborChanged(state, world, pos, block, otherPos);
		/*TileEntity tile = world.getTileEntity(pos);
		if(Mods.isLoaded(Mods.ProjectRed))
			((TileIronNote)tile).onProjectRedBundledInputChanged();*/
	}


	/*@Override
	@Optional.Method(modid=Mods.MFR)
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) {
		return RedNetConnectionType.PlateSingle;
	}

	@Override
	@Optional.Method(modid=Mods.MFR)
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) { }

	@Override
	@Optional.Method(modid=Mods.MFR)
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		for(int i = 0; i < 25; i++) {
			if((inputValue & 1) > 0) 
				NoteUtils.playNote(world, x, y, z, -1, i);
			inputValue >>= 1;
		}
	}*/

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileIronNote.class;
	}
}

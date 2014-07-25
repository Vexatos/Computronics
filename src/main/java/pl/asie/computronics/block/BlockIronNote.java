package pl.asie.computronics.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileCipherBlock;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.util.NoteUtils;
import pl.asie.lib.block.BlockBase;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;

@Optional.InterfaceList({
	@Optional.Interface(iface = "powercrystals.minefactoryreloaded.api.rednet.IRedNetInputNode", modid = "MineFactoryReloaded")
})
public class BlockIronNote extends BlockPeripheral implements IRedNetInputNode {
	public BlockIronNote() {
		super();
		this.setIconName("computronics:noteblock");
		this.setBlockName("computronics.ironNoteBlock");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileIronNote();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(Loader.isModLoaded("ProjRed|Core"))
			((TileIronNote)tile).onProjectRedBundledInputChanged();
	}


	@Override
	@Optional.Method(modid="MineFactoryReloaded")
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) {
		return RedNetConnectionType.PlateSingle;
	}

	@Override
	@Optional.Method(modid="MineFactoryReloaded")
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) { }

	@Override
	@Optional.Method(modid="MineFactoryReloaded")
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		for(int i = 0; i < 25; i++) {
			if((inputValue & 1) > 0) 
				NoteUtils.playNote(world, x, y, z, -1, i);
			inputValue >>= 1;
		}
	}
}

package pl.asie.computronics.block;

import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileCipherBlock;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockIronNote extends BlockPeripheral {
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
}

package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.computronics.tile.TileTapeReader;
import pl.asie.lib.block.BlockBase;

public class BlockTapeReader extends BlockBase {
	public BlockTapeReader(int id) {
		super(id, Material.iron, Computronics.instance);
		this.setUnlocalizedName("computronics.tapeReader");
		this.setGuiID(0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileTapeReader();
	}
}

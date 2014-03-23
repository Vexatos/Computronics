package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockIronNote extends BlockBase {
	public BlockIronNote() {
		super(Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.setIconName("computronics:noteblock");
		this.setBlockName("computronics.ironNoteBlock");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileIronNote();
	}
}

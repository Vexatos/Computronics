package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.tile.TileIronNote;
import pl.asie.lib.block.BlockBase;

public class BlockIronNote extends BlockBase {
	public BlockIronNote(int id) {
		super(id, Material.iron);
		this.setIconName("computronics:noteblock");
		this.setUnlocalizedName("computronics.noteblock");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileIronNote();
	}
}

package pl.asie.computronics.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.tile.TileGrogergizer;
import pl.asie.lib.block.BlockBase;

/**
 * @author Vexatos
 */
public class BlockGrogergizer extends BlockBase {
	public BlockGrogergizer() {
		super(Material.iron, Computronics.instance);
		this.setCreativeTab(Computronics.tab);
		this.setIconName("opencomputers:GenericTop");
		this.setBlockName("computronics.grogergizer");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileGrogergizer();
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
		return false;
	}
}

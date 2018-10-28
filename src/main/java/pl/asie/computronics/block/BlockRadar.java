package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileRadar;

public class BlockRadar extends BlockPeripheral {

	public BlockRadar() {
		super("radar", Rotation.NONE);
		this.setCreativeTab(Computronics.tab);
		this.setTranslationKey("computronics.radar");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState metadata) {
		return new TileRadar();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileRadar.class;
	}
}

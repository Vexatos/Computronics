package pl.asie.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.tile.TileSpeechBox;

/**
 * @author Vexatos
 */
public class BlockSpeechBox extends BlockPeripheral {

	public BlockSpeechBox() {
		super("speech_box", Rotation.FOUR);
		this.setTranslationKey("computronics.speechBox");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileSpeechBox();
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileSpeechBox.class;
	}
}

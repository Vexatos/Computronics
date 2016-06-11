package pl.asie.computronics.integration.chisel;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockColorfulLamp;
import team.chisel.api.carving.CarvingUtils;

/**
 * @author Vexatos
 */
public class IntegrationChisel {

	public void preInit() {
		CarvingUtils.getChiselRegistry().addVariation("computronics:colorful_lamp",
			Computronics.colorfulLamp.getDefaultState(), 0);
		CarvingUtils.getChiselRegistry().addVariation("computronics:colorful_lamp",
			Computronics.colorfulLamp.getDefaultState().withProperty(BlockColorfulLamp.CTM, true), 1);
	}

	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		ModelLoader.setCustomStateMapper(Computronics.colorfulLamp, new StateMapperBase() {
			private final StandardStateMapper DEFAULT = new StandardStateMapper();

			class StandardStateMapper extends DefaultStateMapper {

				@Override
				public ModelResourceLocation getModelResourceLocation(IBlockState state) {
					return super.getModelResourceLocation(state);
				}
			}

			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return state.getValue(BlockColorfulLamp.CTM) ?
					new ModelResourceLocation(
						Block.REGISTRY.getNameForObject(state.getBlock()) + "-ctm", this.getPropertyString(state.getProperties())
					)
					: DEFAULT.getModelResourceLocation(state);
			}
		});
	}
}

package pl.asie.computronics.block;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.signals.BlockSignalBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.tile.TileDigitalReceiverBox;

/**
 * @author CovertJaguar, Vexatos
 */
public class BlockDigitalReceiverBox extends BlockSignalBase {

	public static IIcon[] texturesBox;
	public static IIcon texturesBoxTop;

	public BlockDigitalReceiverBox() {
		super(RenderingRegistry.getNextAvailableRenderId());
		this.setBlockName("computronics.digitalBox");
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		texturesBox = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.box", 6);
		texturesBoxTop = iconRegister.registerIcon("computronics:signal_box_receiver");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch(side){
			case 0:
				return texturesBox[2];
			case 1:
				return texturesBoxTop;
			default:
				return texturesBox[0];
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		try {
			TileEntity tile = world.getTileEntity(x, y, z);
			if((tile instanceof TileDigitalReceiverBox)) {
				TileDigitalReceiverBox structure = (TileDigitalReceiverBox) tile;
				if((structure.getSignalType().needsSupport())
					&& (!world.isSideSolid(x, y - 1, z, ForgeDirection.UP))
					&& !(Loader.isModLoaded(Mods.OpenComputers) && world.getTileEntity(x, y - 1, z) instanceof Environment)) {
					world.func_147480_a(x, y, z, true);
				} else {
					structure.onNeighborBlockChange(block);
				}
			}
		} catch(StackOverflowError error) {
			Computronics.log.error("Error in BlockDigitalReceiverBox.onNeighborBlockChange()");
			throw error;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileDigitalReceiverBox();
	}

	public TileEntity createTileEntity(World world, int meta) {
		return this.createNewTileEntity(world, meta);
	}

	@Override
	public boolean canProvidePower() {
		return false;
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
		return false;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int i, int j, int k, int side) {
		return 0;
	}

}

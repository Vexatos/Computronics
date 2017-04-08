package pl.asie.computronics.integration.railcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.Environment;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockMachineSidedIcon;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;

/**
 * @author Vexatos
 */
public class BlockTicketMachine extends BlockMachineSidedIcon implements IBlockWithPrefix {

	private IIcon mFront;

	public BlockTicketMachine() {
		super("ticket_machine");
		this.setBlockName("computronics.ticketMachine");
		this.setGuiProvider(Computronics.railcraft.guiTicketMachine);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileTicketMachine) {
			((TileTicketMachine) tile).onBlockPlacedBy(entity, stack);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileTicketMachine();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteSideIcon(int sideNumber, int metadata) {
		return sideNumber == 2 ? mFront : super.getAbsoluteSideIcon(sideNumber, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) {
		super.registerBlockIcons(r);
		mFront = r.registerIcon("computronics:ticket_machine_front");
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileTicketMachine.class;
	}

	private static final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, int x, int y, int z) {
		return prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return prefix;
	}
}

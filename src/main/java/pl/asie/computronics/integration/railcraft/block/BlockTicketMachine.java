package pl.asie.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.integration.railcraft.tile.TileTicketMachine;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;

import java.util.Map;

/**
 * @author Vexatos
 */
public class BlockTicketMachine extends BlockPeripheral implements IBlockWithPrefix, IChargeBlock {

	public BlockTicketMachine() {
		super("ticket_machine", Rotation.FOUR);
		this.setTranslationKey("computronics.ticketMachine");
		this.setGuiProvider(Computronics.railcraft.guiTicketMachine);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileTicketMachine) {
			((TileTicketMachine) tile).onBlockPlacedBy(placer, stack);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileTicketMachine();
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileTicketMachine.class;
	}

	@Override
	public Map<Charge, ChargeSpec> getChargeSpecs(IBlockState state, IBlockAccess world, BlockPos pos) {
		return TileTicketMachine.CHARGE_SPECS;
	}

	private static final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return prefix;
	}
}

package pl.asie.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.block.BlockPeripheral;
import pl.asie.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import pl.asie.computronics.oc.manual.IBlockWithPrefix;
import pl.asie.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class BlockLocomotiveRelay extends BlockPeripheral implements IBlockWithPrefix {

	public BlockLocomotiveRelay() {
		super("locomotive_relay", Rotation.NONE);
		this.setTranslationKey("computronics.locomotiveRelay");
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileLocomotiveRelay();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileLocomotiveRelay.class;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote && player.isSneaking() && player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty()) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileLocomotiveRelay) {
				String msg;
				if(((TileLocomotiveRelay) tile).unbind()) {
					msg = "chat.computronics.relay.unbound";
				} else {
					msg = "chat.computronics.relay.notBound";
				}
				player.sendMessage(new TextComponentTranslation(msg));
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	private final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return this.prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return this.prefix;
	}
}

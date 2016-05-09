package pl.asie.lib.integration.buildcraft;

import buildcraft.api.blueprints.IBuilderContext;
import buildcraft.api.blueprints.SchematicTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.tile.TileMachine;

import java.util.List;

/**
 * @author Vexatos
 */
public class SchematicBlockBase extends SchematicTile {

	@Override
	public void rotateLeft(IBuilderContext context) {
		if(state != null && state.getBlock() instanceof BlockBase) {
			BlockBase block = (BlockBase) state.getBlock();
			switch(block.rotation) {
				case SIX: {
					switch(block.getFacingDirection(state)) {
						case DOWN:
						case UP: {
							// No rotation.
							break;
						}
						default: {
							EnumFacing face = this.state.getValue(block.rotation.FACING);
							if(face != null) {
								this.state = this.state.withProperty(block.rotation.FACING, face.rotateY());
							}
							break;
						}
					}
					break;
				}
				case FOUR: {
					EnumFacing face = this.state.getValue(block.rotation.FACING);
					if(face != null) {
						this.state = this.state.withProperty(block.rotation.FACING, face.rotateY());
					}
					break;
				}
				case NONE: {
					// No rotation.
					break;
				}
			}
		}
	}

	@Override
	public void initializeFromObjectAt(IBuilderContext context, BlockPos pos) {
		super.initializeFromObjectAt(context, pos);

		if(state != null && state.getBlock() != null && state.getBlock().hasTileEntity(state)) {
			TileEntity tile = context.world().getTileEntity(pos);
			if(tile != null && tile instanceof TileMachine) {
				((TileMachine) tile).removeFromNBTForTransfer(tileNBT);
				tileNBT = (NBTTagCompound) tileNBT.copy();
			}
		}
	}

	// Don't store the inventory.
	@Override
	public void storeRequirements(IBuilderContext context, BlockPos pos) {
		if(state != null && state.getBlock() != null) {
			List<ItemStack> req = state.getBlock().getDrops(context.world(), pos, state, 0);

			if(req != null) {
				storedRequirements = new ItemStack[req.size()];
				req.toArray(storedRequirements);
			}
		}
	}
}

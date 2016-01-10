package pl.asie.lib.integration.buildcraft;

import buildcraft.api.blueprints.IBuilderContext;
import buildcraft.api.blueprints.SchematicTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.tile.TileMachine;

import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class SchematicBlockBase extends SchematicTile {
	//private static final int[] ROTATIONS = { 4, 5, 3, 2 };
	//Do not ask me why this works.
	private static final int[] ROTATIONS = { 5, 4, 2, 3 };

	@Override
	public void rotateLeft(IBuilderContext context) {
		if(block instanceof BlockBase) {
			BlockBase block = (BlockBase) this.block;
			switch(block.getRotation()) {
				case SIX: {
					switch(block.getFrontSide(meta)) {
						case 0:
						case 1: {
							super.rotateLeft(context);
							break;
						}
						default: {
							meta = ROTATIONS[((meta & 7) - 2) & 3] | (meta & (~7));
							break;
						}
					}
					break;
				}
				case FOUR: {
					meta = ((ROTATIONS[meta & 3] - 2) & 3) | (meta & (~3));
					break;
				}
				case NONE: {
					super.rotateLeft(context);
					break;
				}
			}
		}
	}

	@Override
	public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
		super.initializeFromObjectAt(context, x, y, z);

		if(block.hasTileEntity(meta)) {
			TileEntity tile = context.world().getTileEntity(x, y, z);
			if(tile != null && tile instanceof TileMachine) {
				((TileMachine) tile).removeFromNBTForTransfer(tileNBT);
				tileNBT = (NBTTagCompound) tileNBT.copy();
			}
		}
	}

	// Don't store the inventory.
	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		if(block != null) {
			ArrayList<ItemStack> req = block.getDrops(context.world(), x,
				y, z, context.world().getBlockMetadata(x, y, z), 0);

			if(req != null) {
				storedRequirements = new ItemStack[req.size()];
				req.toArray(storedRequirements);
			}
		}
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		if(block instanceof BlockBase) {
			switch(((BlockBase) block).getRotation()) {
				case FOUR: {
					return block == context.world().getBlock(x, y, z) && (meta & (~3)) == (context.world().getBlockMetadata(x, y, z) & (~3));
				}
				case SIX: {
					return block == context.world().getBlock(x, y, z) && (meta & (~7)) == (context.world().getBlockMetadata(x, y, z) & (~7));
				}
			}
		}
		return block == context.world().getBlock(x, y, z);
	}
}

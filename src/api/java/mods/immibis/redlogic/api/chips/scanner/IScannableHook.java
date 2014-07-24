package mods.immibis.redlogic.api.chips.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Allows blocks without tile entities to be scanned.
 * Also allows mods to make scanned blocks from other mods or vanilla.
 * 
 * IScannableTile takes priority over perBlock hooks, which take priority over list/airList hooks.
 */
public interface IScannableHook {
	/**
	 * Returns the IScannedBlock to use for the given block, or null.
	 */
	public IScannedBlock getScannedBlock(IScanProcess process, World w, int x, int y, int z) throws CircuitLayoutException;
	
	/**
	 * Add IScannableHooks here to be called for non-air blocks.
	 * 
	 * Hooks in perBlock have priority over these.
	 */
	public static final List<IScannableHook> list = new ArrayList<IScannableHook>();
	
	/**
	 * Add IScannableHooks here to be called for air blocks.
	 * 
	 * Hooks in perBlock have priority over these.
	 */
	public static final List<IScannableHook> airList = new ArrayList<IScannableHook>();
	
	/**
	 * Add per-block-ID IScannableHooks here.
	 * 
	 * One per block ID, but you can manually chain them. If you are registering a block
	 * that does not belong to your mod, manual chaining is strongly recommended. (and also
	 * make sure you load after that mod)
	 */
	public static final Map<Block, IScannableHook> perBlock = new HashMap<Block, IScannableHook>();
}

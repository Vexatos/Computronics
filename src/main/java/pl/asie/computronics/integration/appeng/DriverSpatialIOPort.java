package pl.asie.computronics.integration.appeng;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.ISpatialStorageCell;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.spatial.ISpatialCache;
import appeng.api.util.WorldCoord;
import appeng.hooks.TickHandler;
import appeng.me.GridAccessException;
import appeng.me.cache.SpatialPylonCache;
import appeng.tile.spatial.TileSpatialIOPort;
import appeng.util.Platform;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pl.asie.computronics.integration.ManagedEnvironmentOCTile;
import pl.asie.computronics.integration.util.SpatialIOUtil;

import java.util.HashMap;

/**
 * @author Vexatos
 */
public class DriverSpatialIOPort extends DriverTileEntity {

	public class ManagedEnvironmentSpatialIO extends ManagedEnvironmentOCTile<TileSpatialIOPort> {
		public ManagedEnvironmentSpatialIO(TileSpatialIOPort tile) {
			super(tile, "spatial_io");
		}

		@Callback(doc = "function(); Triggers the Spatial IO Port, same effect as applying a redstone signal")
		public Object[] trigger(Context c, Arguments a) {
			if(Platform.isServer()) {
				ItemStack cell = tile.getStackInSlot(0);
				if(SpatialIOUtil.isSpatialCell(cell)) {
					TickHandler.instance.addCallable(null, tile);
				}
			}
			return new Object[] { };
		}

		/**
		 * Stolen from {@link appeng.tile.spatial.TileSpatialIOPort#call()}
		 */
		@Callback(doc = "function():boolean; Checks whether the Spatial IO Port can be triggered")
		public Object[] canTrigger(Context c, Arguments a) {
			ItemStack cell = tile.getStackInSlot(0);
			if(cell != null && (SpatialIOUtil.isSpatialCell(cell)) && (tile.getStackInSlot(1) == null)) {
				try {
					IGrid gi = tile.getProxy().getGrid();
					IEnergyGrid energy = tile.getProxy().getEnergy();
					SpatialPylonCache spc = gi.getCache(ISpatialCache.class);
					if((spc.hasRegion()) && (spc.isValidRegion())) {
						double req = spc.requiredPower();
						double pr = energy.extractAEPower(req, Actionable.SIMULATE, PowerMultiplier.CONFIG);
						if(Math.abs(pr - req) < req * 0.001D) {
							return new Object[] { true };
						}
					}
				} catch(GridAccessException e) {
					return new Object[] { false };
				}
			}
			return new Object[] { false, SpatialIOUtil.getCause(tile.getStackInSlot(0), tile.getStackInSlot(1), false) };
		}

		@Callback(doc = "function():boolean; Puts the cell in the IO Port's output slot into its input slot if possible; returns true on success")
		public Object[] swapCell(Context c, Arguments a) {
			ItemStack cell = tile.getStackInSlot(1);
			if(cell != null && SpatialIOUtil.isSpatialCell(cell) && tile.getStackInSlot(0) == null) {
				tile.setInventorySlotContents(0, cell);
				tile.setInventorySlotContents(1, null);
				return new Object[] { true };
			}
			return new Object[] { false, SpatialIOUtil.getCause(tile.getStackInSlot(0), tile.getStackInSlot(1), true) };
		}

		@Callback(doc = "function():boolean; Checks whether the cell in the IO Port's output slot can be moved into its input slot")
		public Object[] canSwapCell(Context c, Arguments a) {
			ItemStack cell = tile.getStackInSlot(1);
			if(cell != null && SpatialIOUtil.isSpatialCell(cell) && tile.getStackInSlot(0) == null) {
				return new Object[] { true };
			}
			return new Object[] { false, SpatialIOUtil.getCause(tile.getStackInSlot(0), tile.getStackInSlot(1), true) };
		}

		@Callback(doc = "function():number, number, number; Returns the size of the storage cell in the input slot (x, y and z size)")
		public Object[] getCellSize(Context c, Arguments a) {
			ItemStack cell = tile.getStackInSlot(0);
			ISpatialStorageCell sc = SpatialIOUtil.getSpatialCell(cell);
			if(sc != null) {
				WorldCoord size = sc.getStoredSize(cell);
				if(size != null) {
					return new Object[] { size.x, size.y, size.z };
				}
			}
			return new Object[] { null, SpatialIOUtil.getCause(cell, null, false) };
		}

		@Callback(doc = "function():number; Returns the maximum dimensions of the storage cell in the input slot")
		public Object[] getMaxCellSize(Context c, Arguments a) {
			ItemStack cell = tile.getStackInSlot(0);
			ISpatialStorageCell sc = SpatialIOUtil.getSpatialCell(cell);
			if(sc != null) {
				return new Object[] { sc.getMaxStoredDim(cell) };
			}
			return new Object[] { null, SpatialIOUtil.getCause(cell, null, false) };
		}

		@Callback(doc = "function():table; Returns a table with further information about the Spatial IO Port")
		public Object[] getInformation(Context c, Arguments a) {
			ItemStack cell = tile.getStackInSlot(0);
			HashMap<String, Object> map = new HashMap<String, Object>();
			try {
				IGrid gi = tile.getProxy().getGrid();
				IEnergyGrid energy = tile.getProxy().getEnergy();
				SpatialPylonCache spc = gi.getCache(ISpatialCache.class);
				if((spc.hasRegion()) && (spc.isValidRegion())) {
					double req = spc.requiredPower();
					double pr = energy.extractAEPower(req, Actionable.SIMULATE, PowerMultiplier.CONFIG);
					map.put("requiredEnergy", req);
					map.put("availableEnergy", energy.getStoredPower());
					map.put("maxEnergy", energy.getMaxStoredPower());
					map.put("efficiency", spc.currentEffiency() / 100.0F);

					map.put("canTrigger", SpatialIOUtil.isSpatialCell(cell)
						&& tile.getStackInSlot(1) == null && Math.abs(pr - req) < req * 0.001D);
				} else {
					map.put("requiredEnergy", -1.0D);
					map.put("availableEnergy", energy.getStoredPower());
					map.put("maxEnergy", energy.getMaxStoredPower());
					map.put("efficiency", -1.0F);
					map.put("canTrigger", false);
				}
			} catch(GridAccessException e) {
				map.put("requiredEnergy", -1.0D);
				map.put("availableEnergy", -1.0D);
				map.put("maxEnergy", -1.0D);
				map.put("efficiency", -1.0F);
				map.put("canTrigger", false);
			}
			map.put("hasInputCell", SpatialIOUtil.isSpatialCell(cell));
			return new Object[] { map.isEmpty() ? null : map };
		}

	}

	@Override
	public Class<?> getTileEntityClass() {
		return TileSpatialIOPort.class;
	}

	@Override
	public ManagedEnvironment createEnvironment(World world, int x, int y, int z) {
		return new ManagedEnvironmentSpatialIO((TileSpatialIOPort) world.getTileEntity(x, y, z));
	}
}

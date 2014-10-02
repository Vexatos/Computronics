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
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;
import pl.asie.computronics.integration.util.SpatialIOUtil;

import java.util.HashMap;

/**
 * @author Vexatos
 */
public class SpatialIOPortPeripheral extends CCTilePeripheral<TileSpatialIOPort> {
	public SpatialIOPortPeripheral() {
	}

	public SpatialIOPortPeripheral(TileSpatialIOPort tile, World world, int x, int y, int z) {
		super(tile, "spatial_io", world, x, y, z);
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileSpatialIOPort) {
			return new SpatialIOPortPeripheral((TileSpatialIOPort) te, world, x, y, z);
		}
		return null;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "trigger", "canTrigger", "swapCell", "canSwapCell", "getCellSize", "getMaxCellSize", "getInformation" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
		int method, Object[] arguments) throws LuaException,
		InterruptedException {
		switch(method){
			case 0:{
				if(Platform.isServer()) {
					ItemStack cell = tile.getStackInSlot(0);
					if(SpatialIOUtil.isSpatialCell(cell)) {
						TickHandler.instance.addCallable(null, tile);
					}
				}
				return new Object[] { };
			}
			case 1:{
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
			case 2:{
				ItemStack cell = tile.getStackInSlot(1);
				if(cell != null && SpatialIOUtil.isSpatialCell(cell) && tile.getStackInSlot(0) == null) {
					tile.setInventorySlotContents(0, cell);
					tile.setInventorySlotContents(1, null);
					return new Object[] { true };
				}
				return new Object[] { false, SpatialIOUtil.getCause(tile.getStackInSlot(0), tile.getStackInSlot(1), true) };
			}
			case 3:{
				ItemStack cell = tile.getStackInSlot(1);
				if(cell != null && SpatialIOUtil.isSpatialCell(cell) && tile.getStackInSlot(0) == null) {
					return new Object[] { true };
				}
				return new Object[] { false, SpatialIOUtil.getCause(tile.getStackInSlot(0), tile.getStackInSlot(1), true) };
			}
			case 4:{
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
			case 5:{
				ItemStack cell = tile.getStackInSlot(0);
				ISpatialStorageCell sc = SpatialIOUtil.getSpatialCell(cell);
				if(sc != null) {
					return new Object[] { sc.getMaxStoredDim(cell) };
				}
				return new Object[] { null, SpatialIOUtil.getCause(cell, null, false) };
			}
			case 6:{
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
		return new Object[] { };
	}
}

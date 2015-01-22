package pl.asie.computronics.cc.multiperipheral;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.api.ApiAccess;
import openperipheral.api.IAdapterFactory;
import openperipheral.api.IPeripheralBlacklist;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheralProvider;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class MultiPeripheralProvider implements IPeripheralProvider {
	ArrayList<IMultiPeripheralProvider> peripheralProviders = new ArrayList<IMultiPeripheralProvider>();

	public MultiPeripheralProvider(ArrayList<IMultiPeripheralProvider> peripheralProviders) {
		this.peripheralProviders = peripheralProviders;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		ArrayList<IMultiPeripheral> periphs = new ArrayList<IMultiPeripheral>();
		for(IMultiPeripheralProvider peripheralProvider : this.peripheralProviders) {
			IMultiPeripheral p = peripheralProvider.getPeripheral(world, x, y, z, side);
			if(p != null) {
				periphs.add(p);
			}
		}
		if(Loader.isModLoaded(Mods.OpenPeripheral) && Config.CC_OPEN_MULTI_PERIPHERAL) {
			IMultiPeripheral peripheral = getOpenPeripheral(world, x, y, z);
			if(peripheral != null) {
				periphs.add(peripheral);
			}
		}
		if(Config.CC_ALL_MULTI_PERIPHERALS) {
			getAllPeripherals(periphs, world, x, y, z, side);
		}
		if(!periphs.isEmpty()) {
			return new MultiPeripheral(periphs, world, x, y, z);
		}
		return null;
	}

	@Optional.Method(modid = Mods.OpenPeripheral)
	private IMultiPeripheral getOpenPeripheral(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && ApiAccess.isApiPresent(IAdapterFactory.class)) {
			IPeripheral peripheral = ApiAccess.getApi(IAdapterFactory.class).createPeripheral(tile);
			boolean blacklisted = false;
			if(ApiAccess.isApiPresent(IPeripheralBlacklist.class)) {
				blacklisted = ApiAccess.getApi(IPeripheralBlacklist.class).isBlacklisted(tile.getClass());
			}
			if(peripheral != null && !blacklisted) {
				return new OpenMultiPeripheral(peripheral);
			}
		}
		return null;
	}

	private List<IPeripheralProvider> ccPeripheralProviders;
	private boolean ccErrored = false;

	@SuppressWarnings("unchecked")
	private void getCCProviders() {
		if(ccErrored || ccPeripheralProviders != null) {
			return;
		}
		ccPeripheralProviders = new ArrayList<IPeripheralProvider>();
		List ccperiphs;
		try {
			Class<?> cclass = Class.forName("dan200.computercraft.ComputerCraft");
			Field cfield = cclass.getDeclaredField("peripheralProviders");
			cfield.setAccessible(true);
			ccperiphs = (List) cfield.get(null);
		} catch(IllegalAccessException e) {
			Computronics.log.error("Could not access ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		} catch(ClassNotFoundException e) {
			Computronics.log.error("Could not find ComputerCraft main class");
			ccErrored = true;
			return;
		} catch(NoSuchFieldException e) {
			Computronics.log.error("Could not find ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		} catch(ClassCastException e) {
			Computronics.log.error("Could not cast ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		} catch(Exception e) {
			Computronics.log.error("Could not wrap ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		}

		if(ccperiphs == null) {
			return;
		}
		//This is so cheaty, but it works
		if(Config.CC_ALWAYS_FIRST) {
			for(int i = 0; i < ccperiphs.size(); i++) {
				Object o = ccperiphs.get(i);
				if(o != null && o instanceof MultiPeripheralProvider) {
					ccperiphs.remove(i);
					ccperiphs.add(0, o);
					break;
				}
			}
		}
		for(Object ccperiph : ccperiphs) {
			if(ccperiph != null && ccperiph instanceof IPeripheralProvider
				&& !(ccperiph instanceof MultiPeripheralProvider)
				&& !(Loader.isModLoaded(Mods.OpenPeripheral) && isOpenPeripheral(ccperiph))) {
				ccPeripheralProviders.add((IPeripheralProvider) ccperiph);
			}
		}
	}

	private void getAllPeripherals(ArrayList<IMultiPeripheral> periphs, World world, int x, int y, int z, int side) {
		if(ccErrored) {
			return;
		}
		if(ccPeripheralProviders == null) {
			getCCProviders();
		}
		for(IPeripheralProvider peripheralProvider : ccPeripheralProviders) {
			IPeripheral peripheral = peripheralProvider.getPeripheral(world, x, y, z, side);
			if(peripheral != null) {
				periphs.add(new DefaultMultiPeripheral(peripheral));
			}
		}
	}

	public void sort() {
		if(ccPeripheralProviders == null && !ccErrored && Config.CC_ALWAYS_FIRST) {
			getCCProviders();
		}
	}

	private Class<?> openpClass;

	private boolean isOpenPeripheral(Object ccperiph) {
		if(ccperiph == null) {
			return false;
		}
		try {
			if(openpClass == null) {
				openpClass = Class.forName("openperipheral.adapter.PeripheralHandlers");
			}
			return openpClass.isInstance(ccperiph);
		} catch(Exception e) {
			return false;
		}
	}
}

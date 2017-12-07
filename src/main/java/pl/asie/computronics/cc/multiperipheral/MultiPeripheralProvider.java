package pl.asie.computronics.cc.multiperipheral;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openperipheral.api.ApiAccess;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;
import openperipheral.api.peripheral.IOpenPeripheral;
import openperipheral.api.peripheral.IPeripheralBlacklist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	public static Logger log = LogManager.getLogger(Mods.Computronics + "-multiperipheral");

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
		if(Config.CC_ALL_MULTI_PERIPHERALS) {
			getAllPeripherals(periphs, world, x, y, z, side);
		}
		if(Mods.isLoaded(Mods.OpenPeripheral) && Config.CC_OPEN_MULTI_PERIPHERAL) {
			IMultiPeripheral peripheral = getOpenPeripheral(world, x, y, z);
			if(peripheral != null) {
				periphs.add(peripheral);
			}
		}
		if(!periphs.isEmpty()) {
			return periphs.size() == 1 ? periphs.get(0) : new MultiPeripheral(periphs);
		}
		return null;
	}

	@Optional.Method(modid = Mods.OpenPeripheral)
	private IMultiPeripheral getOpenPeripheral(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		try {
			if(tile != null && ApiAccess.isApiPresent(IComputerCraftObjectsFactory.class)) {
				IPeripheral peripheral = ApiAccess.getApi(IComputerCraftObjectsFactory.class).createPeripheral(tile);
				boolean blacklisted = false;
				if(ApiAccess.isApiPresent(IPeripheralBlacklist.class)) {
					blacklisted = ApiAccess.getApi(IPeripheralBlacklist.class).isBlacklisted(tile.getClass());
				}
				if(peripheral != null && !blacklisted) {
					return new OpenMultiPeripheral(peripheral);
				}
			}
		} catch(Exception e) {
			log.debug("An exception got thrown trying to get OpenPeripheral peripherals", e);
		} catch(Throwable t) {
			log.error("An error occured trying to get OpenPeripheral peripherals", t);
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
			//I am sorry I have to do this
			Class<?> cclass = Class.forName("dan200.computercraft.ComputerCraft");
			Field cfield = cclass.getDeclaredField("peripheralProviders");
			cfield.setAccessible(true);
			ccperiphs = (List) cfield.get(null);
		} catch(IllegalAccessException e) {
			log.error("Could not access ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		} catch(ClassNotFoundException e) {
			log.error("Could not find ComputerCraft main class");
			ccErrored = true;
			return;
		} catch(NoSuchFieldException e) {
			log.error("Could not find ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		} catch(ClassCastException e) {
			log.error("Could not cast ComputerCraft peripheral provider list");
			ccErrored = true;
			return;
		} catch(Exception e) {
			log.error("Could not wrap ComputerCraft peripheral provider list");
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
				&& !(Mods.isLoaded(Mods.OpenPeripheral) && isOpenPeripheral(ccperiph))) {
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
		try {
			for(IPeripheralProvider peripheralProvider : ccPeripheralProviders) {
				if(peripheralProvider != null) {
					try {
						IPeripheral peripheral = peripheralProvider.getPeripheral(world, x, y, z, side);
						if(peripheral != null) {
							periphs.add(new DefaultMultiPeripheral(peripheral));
						}
					} catch(Exception e) {
						log.warn("An exception got thrown trying to get a peripheral from provider " + peripheralProvider.getClass().toString(), e);
					} catch(Throwable t) {
						log.error("An error occured trying to get all a peripheral from provider " + peripheralProvider.getClass().toString(), t);
					}
				}
			}
		} catch(Exception e) {
			log.warn("An exception got thrown trying to get all peripherals", e);
		} catch(Throwable t) {
			log.error("An error occured trying to get all peripherals", t);
		}
	}

	public void sort() {
		if(ccPeripheralProviders == null && !ccErrored && Config.CC_ALWAYS_FIRST) {
			getCCProviders();
		}
	}

	@Optional.Method(modid = Mods.OpenPeripheral)
	private boolean isOpenPeripheral(Object ccperiph) {
		// I guess I have to do it this way
		return ccperiph != null
			&& (ccperiph instanceof IOpenPeripheral || ccperiph.getClass().getName().startsWith("openperipheral"));
	}
}

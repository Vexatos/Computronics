package pl.asie.computronics.cc.multiperipheral;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.Settings;
import li.cil.oc.api.network.BlacklistedPeripheral;
import pl.asie.computronics.api.multiperipheral.IMultiPeripheral;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Allows having multiple peripherals merged into a single one.
 * @author Vexatos
 */
@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers)
public class MultiPeripheral implements IPeripheral, BlacklistedPeripheral {

	private ArrayList<IMultiPeripheral> peripherals;
	private IMultiPeripheral highest;
	private HashMap<String, IMultiPeripheral> methods;
	private String[] methodNames;

	public MultiPeripheral(ArrayList<IMultiPeripheral> peripherals) {
		this.initialize(peripherals);
	}

	private void initialize(ArrayList<IMultiPeripheral> peripherals) {
		this.peripherals = peripherals;
		HashMap<String, IMultiPeripheral> methods = new HashMap<String, IMultiPeripheral>();
		for(IMultiPeripheral peripheral : this.peripherals) {
			if(highest == null || highest.peripheralPriority() < peripheral.peripheralPriority()) {
				highest = peripheral;
			}
			String[] names = peripheral.getMethodNames();
			if(names != null) {
				for(String name : names) {
					if(!methods.containsKey(name) || peripheral.peripheralPriority() > methods.get(name).peripheralPriority()) {
						methods.put(name, peripheral);
					}
				}
			}
		}
		ArrayList<String> m = new ArrayList<String>();
		for(String method : methods.keySet()) {
			m.add(method);
		}
		this.methods = methods;
		this.methodNames = m.toArray(new String[m.size()]);
	}

	@Override
	public String getType() {
		return highest.getType();
	}

	@Override
	public String[] getMethodNames() {
		return methodNames.clone();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		for(String m : methodNames) {
			if(method < methodNames.length && m.equals(methodNames[method])) {
				return this.callMethod(methods.get(m), computer, context, m, arguments);
			}
		}
		return null;
	}

	private Object[] callMethod(IMultiPeripheral peripheral, IComputerAccess computer, ILuaContext context, String methodName, Object[] arguments) throws LuaException, InterruptedException {
		String[] methods = peripheral.getMethodNames();
		if(methods != null) {
			for(int i = 0; i < methods.length; i++) {
				if(methods[i].equals(methodName)) {
					return peripheral.callMethod(computer, context, i, arguments);
				}
			}
		}
		return null;
	}

	// Required stuff

	@Override
	public void attach(IComputerAccess computer) {
		for(IMultiPeripheral peripheral : peripherals) {
			peripheral.attach(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		for(IMultiPeripheral peripheral : peripherals) {
			peripheral.detach(computer);
		}
	}

	@Override
	public boolean equals(IPeripheral other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass().isInstance(other)) {
			MultiPeripheral o = this.getClass().cast(other);
			if(peripherals.size() != o.peripherals.size()) {
				return false;
			}
			for(IPeripheral p : peripherals) {
				boolean found = false;
				for(IPeripheral otherPeriph : o.peripherals) {
					if(p.equals(otherPeriph)) {
						found = true;
						break;
					}
				}

				if(!found) {
					return false;
				}
			}
		}
		for(IMultiPeripheral peripheral : peripherals) {
			if(peripheral.equals(other)) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		if(!Config.CC_ALL_MULTI_PERIPHERALS) {
			return true;
		}
		boolean blacklisted = true;
		ArrayList<IMultiPeripheral> newPeriphs = new ArrayList<IMultiPeripheral>();
		for(IMultiPeripheral peripheral : peripherals) {
			if(!isBlacklisted(peripheral)) {
				blacklisted = false;
				newPeriphs.add(peripheral);
			}
		}
		if(!blacklisted) {
			this.initialize(newPeriphs);
		}
		return blacklisted;
	}

	private static Set<Class<?>> blacklist;

	//Re-implemented from OpenComputers code
	@Optional.Method(modid = Mods.OpenComputers)
	private boolean isBlacklisted(final Object o) {
		if(o instanceof BlacklistedPeripheral) {
			return ((BlacklistedPeripheral) o).isPeripheralBlacklisted();
		}

		try {
			if(blacklist == null) {
				HashSet<Class<?>> newBlacklist = new HashSet<Class<?>>();
				for(String name : Settings.get().peripheralBlacklist()) {
					Class<?> clazz;
					try {
						clazz = Class.forName(name);
					} catch(ClassNotFoundException e) {
						clazz = null;
					}
					if(clazz != null) {
						newBlacklist.add(clazz);
					}
				}
				blacklist = newBlacklist;
			}
		} catch(Exception e) {
			blacklist = new HashSet<Class<?>>();
		}
		for(Class<?> clazz : blacklist) {
			if(clazz.isInstance(o)) {
				return true;
			}
		}
		return false;
	}
}

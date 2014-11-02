package pl.asie.computronics.integration.util;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.World;
import pl.asie.computronics.integration.CCTilePeripheral;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Allows having Multiple peripherals merged into a single one.
 * @author Vexatos
 */
public class MultiPeripheral implements IPeripheral, IPeripheralProvider {

	private ArrayList<CCMultiPeripheral> peripherals;
	private CCMultiPeripheral highest;
	private HashMap<String, CCMultiPeripheral> methods;
	private String[] methodNames;

	private World world;
	private int x, y, z;

	public MultiPeripheral(ArrayList<CCMultiPeripheral> peripherals) {
		this.peripherals = peripherals;
		HashMap<String, CCMultiPeripheral> methods = new HashMap<String, CCMultiPeripheral>();
		for(CCMultiPeripheral peripheral : this.peripherals) {
			if(highest == null || highest.priority() < peripheral.priority()) {
				highest = peripheral;
			}
			String[] names = peripheral.getMethodNames();
			if(names != null) {
				for(String name : names) {
					if(!(methods.containsKey(name) && peripheral.priority() < methods.get(name).priority())) {
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

	public MultiPeripheral(ArrayList<CCMultiPeripheral> periphs, World world, int x, int y, int z) {
		this(periphs);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		ArrayList<CCMultiPeripheral> periphs = new ArrayList<CCMultiPeripheral>();
		for(CCMultiPeripheral peripheral : this.peripherals) {
			CCMultiPeripheral p = peripheral.getPeripheral(world, x, y, z, side);
			if(p != null) {
				periphs.add(p);
			}
		}
		if(!periphs.isEmpty()) {
			return new MultiPeripheral(periphs, world, x, y, z);
		}
		return null;
	}

	@Override
	public String getType() {
		return highest.getType();
	}

	@Override
	public String[] getMethodNames() {
		return methodNames;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		for(String m : methodNames) {
			if(method < methodNames.length && m.equals(methodNames[method])) {
				return methods.get(m).callMethod(computer, context, m, arguments);
			}
		}
		return null;
	}

	// Required stuff

	@Override
	public void attach(IComputerAccess computer) {
		for(CCTilePeripheral peripheral : peripherals) {
			peripheral.attach(computer);
		}
	}

	@Override
	public void detach(IComputerAccess computer) {
		for(CCTilePeripheral peripheral : peripherals) {
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
			if(world == o.world && x == o.x && z == o.z && y == o.y) {
				return true;
			}
		}
		for(CCTilePeripheral peripheral : peripherals) {
			if(peripheral.equals(other)) {
				return true;
			}
		}
		return false;
	}
}

package pl.asie.computronics.util.cipher;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.machine.Value;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.machine.Value", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "dan200.computercraft.api.lua.ILuaObject", modid = Mods.ComputerCraft)
})
public class RSAValue implements Value, ILuaObject {

	protected Map<Integer, String> publicKey;
	protected Map<Integer, String> privateKey;
	protected Future<ArrayList<Map<Integer, String>>> task;

	protected int bitLength = 0;
	protected int p = 0;
	protected int q = 0;

	private Object[] getKeys() throws InterruptedException {
		switch(checkFinished()) {
			case -1: {
				return new Object[] { null, null, "calculation returned no key set" };
			}
			case -2: {
				return new Object[] { null, null, "an error occured during key generation" };
			}
		}

		if(publicKey != null && privateKey != null) {
			return new Object[] { publicKey, privateKey };
		} else if(task != null && !task.isDone() && !task.isCancelled()) {
			return new Object[] { null, null, "key is still being generated" };
		}
		return new Object[] { null, null, "an error occured during key generation" };
	}

	private int checkFinished() throws InterruptedException {
		if(task != null && task.isDone()) {
			try {
				ArrayList<Map<Integer, String>> result = task.get();
				if(result != null) {
					this.publicKey = result.get(0);
					this.privateKey = result.get(1);
				} else {
					return -1;
				}
			} catch(ExecutionException e) {
				return -2;
			}
		}
		return 0;
	}

	public void startCalculation() {
		if(task == null || task.isCancelled()) {
			task = Computronics.rsaThreads.submit(new RSACalculationTask());
		}
	}

	public void startCalculation(int bitLength) {
		if(task == null || task.isCancelled()) {
			this.bitLength = bitLength;
			task = Computronics.rsaThreads.submit(new RSACalculationTask(bitLength));
		}
	}

	public void startCalculation(int p, int q) {
		if(task == null || task.isCancelled()) {
			this.p = p;
			this.q = q;
			task = Computronics.rsaThreads.submit(new RSACalculationTask(p, q));
		}
	}

	@Callback(doc = "function():table,table; Returns the two generated keys, or nil if they are still being generated", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] getKeys(Context c, Arguments a) throws Exception {
		return this.getKeys();
	}

	@Callback(doc = "function():boolean; Returns whether key generation has finished", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] finished(Context c, Arguments a) throws Exception {
		switch(checkFinished()) {
			case -1: {
				return new Object[] { null, "calculation returned no key set" };
			}
			case -2: {
				return new Object[] { null, "an error occured during key generation" };
			}
		}
		return new Object[] { publicKey != null && privateKey != null };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "getKeys", "finished" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			switch(method) {
				case 0: {
					return getKeys();
				}
				case 1: {
					switch(checkFinished()) {
						case -1: {
							return new Object[] { null, "calculation returned no key set" };
						}
						case -2: {
							return new Object[] { null, "an error occured during key generation" };
						}
					}
					return new Object[] { publicKey != null && privateKey != null };
				}
			}
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void load(NBTTagCompound nbt) {
		if(nbt == null) {
			nbt = new NBTTagCompound();
		}
		NBTTagCompound base = nbt.getCompoundTag("computronics:rsa");
		if(publicKey == null && privateKey == null) {
			if(base.hasKey("public") && base.hasKey("private")) {
				NBTTagCompound pbKey = base.getCompoundTag("public");
				NBTTagCompound pvKey = base.getCompoundTag("private");
				publicKey = new LinkedHashMap<Integer, String>();
				privateKey = new LinkedHashMap<Integer, String>();
				publicKey.put(1, pbKey.getString("1"));
				publicKey.put(2, pbKey.getString("2"));
				privateKey.put(1, pvKey.getString("1"));
				privateKey.put(2, pvKey.getString("2"));
			} else if(base.hasKey("bitLength")) {
				this.startCalculation(base.getInteger("bitLength"));
			} else if(base.hasKey("p") && base.hasKey("q")) {
				this.startCalculation(base.getInteger("p"), base.getInteger("q"));
			} else {
				this.startCalculation();
			}
		}
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void save(NBTTagCompound nbt) {
		if(nbt == null) {
			nbt = new NBTTagCompound();
		}
		NBTTagCompound base = nbt.getCompoundTag("computronics:rsa");
		if(publicKey != null && privateKey != null) {
			NBTTagCompound pbKey = new NBTTagCompound();
			NBTTagCompound pvKey = new NBTTagCompound();
			pbKey.setString("1", publicKey.get(1));
			pbKey.setString("2", publicKey.get(2));
			pvKey.setString("1", privateKey.get(1));
			pvKey.setString("2", privateKey.get(2));
			base.setTag("public", pbKey);
			base.setTag("private", pvKey);
		} else if(bitLength > 0) {
			base.setInteger("bitLength", bitLength);
		} else if(p > 0 && q > 0) {
			base.setInteger("p", p);
			base.setInteger("q", q);
		}
		nbt.setTag("computronics:rsa", base);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Object apply(Context context, Arguments arguments) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void unapply(Context context, Arguments arguments) {

	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] call(Context context, Arguments arguments) {
		throw new RuntimeException("trying to call a non-callable value");
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void dispose(Context context) {
		if(this.task != null) {
			task.cancel(true);
		}
	}
}

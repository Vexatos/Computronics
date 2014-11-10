package pl.asie.computronics.tile;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.reference.Mods;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vexatos
 */
public class TileCipherBlockAdvanced extends TileEntityPeripheralBase {

	public TileCipherBlockAdvanced() {
		super("advanced_cipher", Computronics.CIPHER_ENERGY_STORAGE);
	}

	@Override
	public boolean canUpdate() {
		return Computronics.MUST_UPDATE_TILE_ENTITIES;
	}

	/**
	 * Checks whether a number is a prime number
	 * @param number the number to check
	 * @return <tt>true</tt> if the number is a prime number
	 */
	private static boolean isPrime(int number) {
		if(number % 2 == 0) {
			return false;
		}
		for(int i = 3; i * i <= number; i += 2) {
			if(number % i == 0) {
				return false;
			}
		}
		return true;
	}

	private static int checkPrime(int number, int i) {
		if(!isPrime(number)) {
			throw new IllegalArgumentException(
				String.format("bad argument #%s (prime expected, got %s)", i, number));
		} else {
			return number;
		}
	}

	private static Map<Integer, String> checkValidKey(Map map, int index) {
		if(map.get(1) != null && map.get(1) instanceof String
			&& map.get(2) != null && map.get(2) instanceof String) {
			Map<Integer, String> keyMap = new LinkedHashMap<Integer, String>();
			keyMap.put(1, (String) map.get(1));
			keyMap.put(2, (String) map.get(2));
			return keyMap;
		}
		throw new IllegalArgumentException(
			String.format("bad argument #%s (no valid RSA key)", index));
	}

	private Object[] createKeySet() {
		SecureRandom r = new SecureRandom();
		return this.createKeySet(
			new BigInteger(64, 100, r),
			new BigInteger(64 / 2, 100, r));
	}

	private Object[] createKeySet(int p, int q) {
		return this.createKeySet(
			BigInteger.valueOf(p),
			BigInteger.valueOf(q));
	}

	private Object[] createKeySet(BigInteger p, BigInteger q) {
		BigInteger cat = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		BigInteger n = p.multiply(q);
		BigInteger d = new BigInteger("3");
		final BigInteger TWO = new BigInteger("2");
		while(cat.gcd(d).intValue() != 1) {
			d = d.add(TWO);
		}
		BigInteger e = d.modInverse(cat);

		Map<Integer, String> publicKey = new LinkedHashMap<Integer, String>();
		Map<Integer, String> privateKey = new LinkedHashMap<Integer, String>();
		publicKey.put(1, n.toString());
		publicKey.put(2, d.toString());
		privateKey.put(1, n.toString());
		privateKey.put(2, e.toString());

		return new Object[] { publicKey, privateKey };
	}

	private Object[] encrypt(Map<Integer, String> publicKey, String messageString) {
		BigInteger message = new BigInteger(messageString.getBytes());
		BigInteger n = new BigInteger(publicKey.get(1));
		BigInteger d = new BigInteger(publicKey.get(2));
		return new Object[] { new String(message.modPow(d, n).toByteArray()) };
	}

	private Object[] decrypt(Map<Integer, String> privateKey, String messageString) {
		BigInteger message = new BigInteger(messageString.getBytes());
		BigInteger n = new BigInteger(privateKey.get(1));
		BigInteger e = new BigInteger(privateKey.get(2));
		return new Object[] { new String(message.modPow(e, n).toByteArray()) };
	}

	@Callback(doc = "function([num1:number, num2:number]):table, table; Creates the public and the private RSA key from the two given or random prime numbers")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] createKeySet(Context c, Arguments a) {
		Object[] result;
		if(a.count() > 0) {
			result = this.createKeySet(
				checkPrime(a.checkInteger(0), 0),
				checkPrime(a.checkInteger(1), 1));
		} else {
			result = this.createKeySet();
		}
		return this.tryConsumeEnergy(result, Computronics.CIPHER_KEY_CONSUMPTION, "createKeySet");
	}

	@Callback(doc = "function(message:string, publicKey:table):string; Encrypts the specified message using the specified public RSA key")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] encrypt(Context c, Arguments a) {
		String message = a.checkString(0);
		Object[] result = this.encrypt(
			checkValidKey(a.checkTable(1), 1),
			message);
		return this.tryConsumeEnergy(result, Computronics.CIPHER_WORK_CONSUMPTION + 0.2 * message.length(), "encrypt");
	}

	@Callback(doc = "function(message:string, privateKey:table):string; Decrypts the specified message using the specified RSA key")
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] decrypt(Context c, Arguments a) {
		String message = a.checkString(0);
		Object[] result = this.decrypt(
			checkValidKey(a.checkTable(1), 1),
			message);
		return this.tryConsumeEnergy(result, Computronics.CIPHER_WORK_CONSUMPTION + 0.2 * message.length(), "decrypt");
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private Object[] tryConsumeEnergy(Object[] result, double v, String methodName) {
		if(this.node instanceof Connector) {
			int power = this.tryConsumeEnergy(v);
			if(power < 0) {
				return new Object[] { null, null, power + ": " + methodName + ": not enough energy available: required"
					+ Computronics.CIPHER_KEY_CONSUMPTION + ", found " + ((Connector) node).globalBuffer() };
			}
		}
		return result;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private int tryConsumeEnergy(double v) {
		if(v < 0) {
			return -2;
		}
		v = -v;
		if(this.node instanceof Connector) {
			Connector connector = ((Connector) this.node);
			return connector.tryChangeBuffer(v) ? 1 : -1;

		}
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "createKeySet", "encrypt", "decrypt" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			switch(method){
				case 0:{
					Object[] result;
					if(arguments.length > 0) {
						if(!(arguments[0] instanceof Double)) {
							throw new LuaException("first argument needs to be a number, or there must not be any arguments");
						} else if(!(arguments[1] instanceof Double)) {
							throw new LuaException("second argument needs to be a number, or there must not be any arguments");
						}
						result = this.createKeySet(
							checkPrime(((Double) arguments[0]).intValue(), 0),
							checkPrime(((Double) arguments[1]).intValue(), 1));
					} else {
						result = this.createKeySet();
					}
					return result;
				}
				case 1:{
					if(!(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					} else if(!(arguments[1] instanceof Map)) {
						throw new LuaException("second argument needs to be a table");
					}
					return this.encrypt(
						checkValidKey((Map) arguments[1], 1),
						(String) arguments[0]);
				}
				case 2:{
					if(!(arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					} else if(!(arguments[1] instanceof Map)) {
						throw new LuaException("second argument needs to be a table");
					}
					return this.decrypt(
						checkValidKey((Map) arguments[1], 1),
						(String) arguments[0]);
				}
			}
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public boolean Connectable(int side) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public short busRead(int addr) {
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public void busWrite(int addr, short data) {

	}
}

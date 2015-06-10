package pl.asie.computronics.tile;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.reference.Mods;
import pl.asie.computronics.util.cipher.RSAValue;
import pl.asie.lib.util.Base64;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vexatos
 */
public class TileCipherBlockAdvanced extends TileEntityPeripheralBase {

	public TileCipherBlockAdvanced() {
		super("advanced_cipher", Config.CIPHER_ENERGY_STORAGE);
	}

	@Override
	public boolean canUpdate() {
		return Config.MUST_UPDATE_TILE_ENTITIES;
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
		} else if(map.get(1.0D) != null && map.get(1.0D) instanceof String
			&& map.get(2.0D) != null && map.get(2.0D) instanceof String) {
			Map<Integer, String> keyMap = new LinkedHashMap<Integer, String>();
			keyMap.put(1, (String) map.get(1.0D));
			keyMap.put(2, (String) map.get(2.0D));
			return keyMap;
		}
		throw new IllegalArgumentException(
			String.format("bad argument #%s (no valid RSA key)", index));
	}

	private String encodeToString(byte[] bytes) {
		return new String(bytes, Charsets.UTF_8);
	}

	private Object[] encrypt(Map<Integer, String> publicKey, String messageString) {
		return this.encrypt(publicKey, messageString.getBytes(Charsets.UTF_8));
	}

	private Object[] decrypt(Map<Integer, String> privateKey, String messageString) throws Exception {
		return this.decrypt(privateKey, messageString.getBytes(Charsets.UTF_8));
	}

	private Object[] encrypt(Map<Integer, String> publicKey, byte[] messageBytes) {
		BigInteger message = new BigInteger(messageBytes);
		BigInteger n = new BigInteger(publicKey.get(1));
		BigInteger d = new BigInteger(publicKey.get(2));
		if(n.toByteArray().length < messageBytes.length) {
			throw new IllegalArgumentException("key is too small, needs to have a bit length of at least " + messageBytes.length + ", but only has " + n.toByteArray().length);
		}
		return new Object[] { Base64.encodeBytes(message.modPow(d, n).toByteArray()) };
	}

	private Object[] decrypt(Map<Integer, String> privateKey, byte[] messageBytes) throws Exception {
		byte[] decodedBytes = Base64.decode(messageBytes);
		BigInteger message = new BigInteger(decodedBytes);
		BigInteger n = new BigInteger(privateKey.get(1));
		BigInteger e = new BigInteger(privateKey.get(2));
		if(n.toByteArray().length < decodedBytes.length) {
			throw new IllegalArgumentException("key is too small, needs to have a bit length of at least " + decodedBytes.length + ", but only has " + n.toByteArray().length);
		}
		return new Object[] { encodeToString(message.modPow(e, n).toByteArray()) };
	}

	@Callback(doc = "function([bitlength:number]):keygen; Creates the key generator from two random prime numbers (optionally with given bit length)", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] createRandomKeySet(Context c, Arguments a) {
		Object[] result;
		RSAValue val = new RSAValue();
		if(a.count() > 0) {
			val.startCalculation(a.checkInteger(0));
		} else {
			val.startCalculation();
		}
		result = new Object[] { val };
		return this.tryConsumeEnergy(result, Config.CIPHER_KEY_CONSUMPTION, "createRandomKeySet");
	}

	@Callback(doc = "function(num1:number, num2:number):keygen; Creates the key generator from the two given prime numbers", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] createKeySet(Context c, Arguments a) {
		RSAValue val = new RSAValue();
		val.startCalculation(
			checkPrime(a.checkInteger(0), 0),
			checkPrime(a.checkInteger(1), 1));
		Object[] result = new Object[] { val };
		return this.tryConsumeEnergy(result, Config.CIPHER_KEY_CONSUMPTION, "createKeySet");
	}

	@Callback(doc = "function(message:string, publicKey:table):string; Encrypts the specified message using the specified public RSA key", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] encrypt(Context c, Arguments a) {
		byte[] message = a.checkByteArray(0);
		Object[] result = this.encrypt(
			checkValidKey(a.checkTable(1), 1),
			message);
		return this.tryConsumeEnergy(result, Config.CIPHER_WORK_CONSUMPTION + 0.2 * message.length, "encrypt");
	}

	@Callback(doc = "function(message:string, privateKey:table):string; Decrypts the specified message using the specified RSA key", direct = true)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] decrypt(Context c, Arguments a) throws Exception {
		byte[] message = a.checkByteArray(0);
		Object[] result = this.decrypt(
			checkValidKey(a.checkTable(1), 1),
			message);
		return this.tryConsumeEnergy(result, Config.CIPHER_WORK_CONSUMPTION + 0.2 * message.length, "decrypt");
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private Object[] tryConsumeEnergy(Object[] result, double v, String methodName) {
		if(this.node() instanceof Connector) {
			int power = this.tryConsumeEnergy(v);
			if(power < 0) {
				return new Object[] { null, null, power + ": " + methodName + ": not enough energy available: required"
					+ v + ", found " + ((Connector) node()).globalBuffer() };
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
		if(this.node() instanceof Connector) {
			Connector connector = ((Connector) this.node());
			return connector.tryChangeBuffer(v) ? 1 : -1;

		}
		return 0;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[] { "createRandomKeySet", "createKeySet", "encrypt", "decrypt" };
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			switch(method){
				case 0:{
					RSAValue val = new RSAValue();
					if(arguments.length > 0) {
						if(!(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number, or there must not be any arguments");
						}
						val.startCalculation(((Number) arguments[1]).intValue());
						return new Object[] { val };
					} else {
						val.startCalculation();
						return new Object[] { val };
					}
				}
				case 1:{
					if(!(arguments.length >= 1 && arguments[0] instanceof Number)) {
						throw new LuaException("first argument needs to be a number");
					} else if(!(arguments.length >= 2 && arguments[1] instanceof Number)) {
						throw new LuaException("second argument needs to be a number");
					}
					RSAValue val = new RSAValue();
					val.startCalculation(checkPrime(((Number) arguments[0]).intValue(), 0),
						checkPrime(((Number) arguments[1]).intValue(), 1));
					return new Object[] { val };
				}
				case 2:{
					if(!(arguments.length >= 1 && arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					} else if(!(arguments.length >= 2 && arguments[1] instanceof Map)) {
						throw new LuaException("second argument needs to be a table");
					}
					return this.encrypt(
						checkValidKey((Map) arguments[1], 1),
						(String) arguments[0]);
				}
				case 3:{
					if(!(arguments.length >= 1 && arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					} else if(!(arguments.length >= 2 && arguments[1] instanceof Map)) {
						throw new LuaException("second argument needs to be a table");
					}
					return this.decrypt(
						checkValidKey((Map) arguments[1], 1),
						(String) arguments[0]);
				}
			}
		} catch(Exception e) {
			if(e instanceof InterruptedException) {
				throw (InterruptedException) e;
			}
			if(e instanceof LuaException) {
				throw (LuaException) e;
			}
			throw new LuaException(e.getMessage());
		}
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.NedoComputers)
	public boolean connectable(int side) {
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

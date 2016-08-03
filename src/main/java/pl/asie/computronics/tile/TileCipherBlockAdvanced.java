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
import pl.asie.computronics.util.OCUtils;
import pl.asie.computronics.util.cipher.RSAValue;
import pl.asie.computronics.util.cipher.ThreadLocals;
import pl.asie.lib.util.Base64;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
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

	@Override
	public boolean canBeColored() {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Processor,
			"Data encryption device",
			OCUtils.Vendors.Siekierka,
			"Cryptotron 6-X"
		);
	}

	/**
	 * Checks whether a number is a prime number
	 * @param number the number to check
	 * @return <tt>true</tt> if the number is a prime number
	 */
	private static boolean isPrime(int number) {
		if(number == 2) {
			return true;
		}
		if(number < 2 || number % 2 == 0) {
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
		if(map.get(1) instanceof String && map.get(2) instanceof String) {
			Map<Integer, String> keyMap = new LinkedHashMap<Integer, String>();
			keyMap.put(1, (String) map.get(1));
			keyMap.put(2, (String) map.get(2));
			if(map.get(3) instanceof String) {
				keyMap.put(3, (String) map.get(3));
			}
			return keyMap;
		} else if(map.get(1.0D) instanceof String && map.get(2.0D) instanceof String) {
			Map<Integer, String> keyMap = new LinkedHashMap<Integer, String>();
			keyMap.put(1, (String) map.get(1.0D));
			keyMap.put(2, (String) map.get(2.0D));
			if(map.get(3.0D) instanceof String) {
				keyMap.put(3, (String) map.get(3.0D));
			}
			return keyMap;
		}
		throw new IllegalArgumentException(
			String.format("bad argument #%s (no valid RSA key)", index));
	}

	private String encodeToString(byte[] bytes) {
		return new String(bytes, Charsets.UTF_8);
	}

	private Object[] encrypt(Map<Integer, String> publicKey, String messageString) throws Exception {
		return this.encrypt(publicKey, messageString.getBytes(Charsets.UTF_8));
	}

	private Object[] decrypt(Map<Integer, String> privateKey, String messageString) throws Exception {
		return this.decrypt(privateKey, messageString.getBytes(Charsets.UTF_8));
	}

	private static final ThreadLocal<KeyFactory> keyFactory = new ThreadLocals.LocalKeyFactory();
	private static final ThreadLocal<Cipher> cipher = new ThreadLocals.LocalCipher();

	private BigInteger unsigned(byte[] src) {
		byte[] unsigned = new byte[src.length + 1];
		System.arraycopy(src, 0, unsigned, 1, src.length);
		return new BigInteger(unsigned);
	}

	private Object[] encrypt(Map<Integer, String> publicKey, byte[] messageBytes) throws Exception {
		BigInteger n = unsigned(Base64.decode(publicKey.get(1)));
		BigInteger d = unsigned(Base64.decode(publicKey.get(2)));
		if(("prime").equals(publicKey.get(3))) {
			BigInteger message = new BigInteger(messageBytes);
			if(n.toByteArray().length < messageBytes.length) {
				throw new IllegalArgumentException("key is too small, needs to have a bit length of at least " + messageBytes.length + ", but only has " + n.toByteArray().length);
			}
			return new Object[] { Base64.encodeBytes(message.modPow(d, n).toByteArray()) };
		} else {
			KeyFactory factory = keyFactory.get();
			Cipher c = cipher.get();
			if(factory == null || c == null) {
				return new Object[] { null, "an error occured during encryption" };
			}
			PublicKey pubKey = factory.generatePublic(new RSAPublicKeySpec(n, d));
			c.init(Cipher.ENCRYPT_MODE, pubKey);
			return new Object[] { Base64.encodeBytes(c.doFinal(messageBytes)) };
		}
	}

	private Object[] decrypt(Map<Integer, String> privateKey, byte[] messageBytes) throws Exception {
		byte[] decodedBytes = Base64.decode(messageBytes);
		BigInteger n = unsigned(Base64.decode(privateKey.get(1)));
		BigInteger e = unsigned(Base64.decode(privateKey.get(2)));
		if(("prime").equals(privateKey.get(3))) {
			BigInteger message = new BigInteger(decodedBytes);
			if(n.toByteArray().length < decodedBytes.length) {
				throw new IllegalArgumentException("key is too small, needs to have a bit length of at least " + decodedBytes.length + ", but only has " + n.toByteArray().length);
			}
			return new Object[] { encodeToString(message.modPow(e, n).toByteArray()) };
		} else {
			KeyFactory factory = keyFactory.get();
			Cipher c = cipher.get();
			if(factory == null || c == null) {
				return new Object[] { null, "an error occured during decryption" };
			}
			PrivateKey privKey = factory.generatePrivate(new RSAPrivateKeySpec(n, e));
			c.init(Cipher.DECRYPT_MODE, privKey);
			return new Object[] { encodeToString(c.doFinal(decodedBytes)) };
		}
	}

	@Callback(doc = "function([keylength:number]):keygen; Creates the key generator from two random prime numbers (optionally with given key length)", direct = true, limit = 1)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] createRandomKeySet(Context c, Arguments a) {
		RSAValue val = new RSAValue();
		if(a.count() > 0) {
			int length = a.checkInteger(0);
			if(length <= 0 || length > 2048) {
				throw new IllegalArgumentException("bitlength must be between 1 and 2048");
			}
			val.startCalculation(length);
		} else {
			val.startCalculation();
		}
		Object[] result = this.tryConsumeEnergy(new Object[] { val }, Config.CIPHER_KEY_CONSUMPTION, "createRandomKeySet");
		c.pause(0.5);
		return result;
	}

	@Callback(doc = "function(num1:number, num2:number):keygen; Creates the key generator from the two given prime numbers", direct = true, limit = 1)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] createKeySet(Context c, Arguments a) {
		RSAValue val = new RSAValue();
		val.startCalculation(
			checkPrime(a.checkInteger(0), 0),
			checkPrime(a.checkInteger(1), 1));
		Object[] result = new Object[] { val };
		return this.tryConsumeEnergy(result, Config.CIPHER_KEY_CONSUMPTION, "createKeySet");
	}

	@Callback(doc = "function(message:string, publicKey:table):string; Encrypts the specified message using the specified public RSA key", direct = true, limit = 1)
	@Optional.Method(modid = Mods.OpenComputers)
	public Object[] encrypt(Context c, Arguments a) throws Exception {
		byte[] message = a.checkByteArray(0);
		Object[] result = this.encrypt(
			checkValidKey(a.checkTable(1), 1),
			message);
		return this.tryConsumeEnergy(result, Config.CIPHER_WORK_CONSUMPTION + 0.2 * message.length, "encrypt");
	}

	@Callback(doc = "function(message:string, privateKey:table):string; Decrypts the specified message using the specified RSA key", direct = true, limit = 1)
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
			switch(method) {
				case 0: {
					RSAValue val = new RSAValue();
					if(arguments.length > 0) {
						if(!(arguments[0] instanceof Number)) {
							throw new LuaException("first argument needs to be a number, or there must not be any arguments");
						}
						int length = ((Number) arguments[0]).intValue();
						if(length <= 0 || length > 2048) {
							throw new LuaException("bitlength must be between 1 and 2048");
						}
						val.startCalculation(length);
						return new Object[] { val };
					} else {
						val.startCalculation();
						return new Object[] { val };
					}
				}
				case 1: {
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
				case 2: {
					if(!(arguments.length >= 1 && arguments[0] instanceof String)) {
						throw new LuaException("first argument needs to be a string");
					} else if(!(arguments.length >= 2 && arguments[1] instanceof Map)) {
						throw new LuaException("second argument needs to be a table");
					}
					return this.encrypt(
						checkValidKey((Map) arguments[1], 1),
						(String) arguments[0]);
				}
				case 3: {
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
		} catch(InterruptedException ie) {
			throw ie;
		} catch(LuaException le) {
			throw le;
		} catch(Exception e) {
			throw new LuaException(e.getMessage());
		}
		return null;
	}
}

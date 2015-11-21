package pl.asie.computronics.util.cipher;

import pl.asie.lib.util.Base64;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Vexatos
 */
public class RSACalculationTask implements Callable<ArrayList<Map<Integer, String>>> {

	private int bitLength = 0;
	private int p = 0;
	private int q = 0;
	//private Map<Integer, String> publicKey;
	//private Map<Integer, String> privateKey;

	public RSACalculationTask() {
	}

	public RSACalculationTask(int bitLength) {
		this.bitLength = bitLength;
	}

	public RSACalculationTask(int p, int q) {
		this.p = p;
		this.q = q;
	}

	@Override
	public ArrayList<Map<Integer, String>> call() {
		if(bitLength > 0) {
			return this.createKeySet(bitLength);
		}
		if(p > 0 && q > 0) {
			return this.createKeySet(p, q);
		}
		return this.createKeySet();
	}

	// Random key pair creation

	private ArrayList<Map<Integer, String>> createKeySet() {
		return this.createKeySet(1024);
	}

	private static final ThreadLocal<KeyPairGenerator> gen = new ThreadLocals.LocalKeyPairGenerator();

	private ArrayList<Map<Integer, String>> createKeySet(int keylength) {
		KeyPairGenerator gen = RSACalculationTask.gen.get();
		if(gen == null) {
			return null;
		}
		gen.initialize(keylength);
		KeyPair pair = gen.generateKeyPair();
		if(pair == null || !(pair.getPublic() instanceof RSAPublicKey) || !(pair.getPrivate() instanceof RSAPrivateKey)) {
			return null;
		}
		RSAPublicKey pubKey = (RSAPublicKey) pair.getPublic();
		RSAPrivateCrtKey privKey = (RSAPrivateCrtKey) pair.getPrivate();
		Map<Integer, String> publicKey = new LinkedHashMap<Integer, String>();
		Map<Integer, String> privateKey = new LinkedHashMap<Integer, String>();
		publicKey.put(1, Base64.encodeBytes(pubKey.getModulus().toByteArray()));
		publicKey.put(2, Base64.encodeBytes(pubKey.getPublicExponent().toByteArray()));
		privateKey.put(1, Base64.encodeBytes(privKey.getModulus().toByteArray()));
		privateKey.put(2, Base64.encodeBytes(privKey.getPrivateExponent().toByteArray()));
		ArrayList<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
		list.add(publicKey);
		list.add(privateKey);
		return list;
	}

	// Non-random key pair creation

	private static final BigInteger
		ONE = BigInteger.ONE,
		TWO = new BigInteger("2"),
		SEVENTEEN = new BigInteger("17");

	private ArrayList<Map<Integer, String>> createKeySet(int p, int q) {
		return this.createKeySet(
			BigInteger.valueOf(p),
			BigInteger.valueOf(q));
	}

	private ArrayList<Map<Integer, String>> createKeySet(BigInteger p, BigInteger q) {
		BigInteger cat = p.subtract(ONE).multiply(q.subtract(ONE));
		BigInteger n = p.multiply(q);
		BigInteger d = SEVENTEEN;
		while(cat.gcd(d).intValue() != 1) {
			d = d.add(TWO);
		}
		BigInteger e = d.modInverse(cat);

		Map<Integer, String> publicKey = new LinkedHashMap<Integer, String>();
		Map<Integer, String> privateKey = new LinkedHashMap<Integer, String>();
		publicKey.put(1, Base64.encodeBytes(n.toByteArray()));
		publicKey.put(2, Base64.encodeBytes(d.toByteArray()));
		publicKey.put(3, "prime");
		privateKey.put(1, Base64.encodeBytes(n.toByteArray()));
		privateKey.put(2, Base64.encodeBytes(e.toByteArray()));
		privateKey.put(3, "prime");
		ArrayList<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
		list.add(publicKey);
		list.add(privateKey);
		return list;
	}
}

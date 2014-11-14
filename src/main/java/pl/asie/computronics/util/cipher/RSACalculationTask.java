package pl.asie.computronics.util.cipher;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Vexatos
 */
public class RSACalculationTask implements Runnable {

	private final RSAValue val;
	private int bitLength = 0;
	private int p = 0;
	private int q = 0;
	private Map<Integer, String> publicKey;
	private Map<Integer, String> privateKey;

	public RSACalculationTask(RSAValue val) {
		this.val = val;
	}

	public RSACalculationTask(RSAValue val, int bitLength) {
		this(val);
		this.bitLength = bitLength;
	}

	public RSACalculationTask(RSAValue val, int p, int q) {
		this(val);
		this.p = p;
		this.q = q;
	}

	@Override
	public void run() {
		ArrayList<Map<Integer, String>> result = this.call();
		this.publicKey = result.get(0);
		this.privateKey = result.get(1);
		val.setKeys(publicKey, privateKey);
	}

	private ArrayList<Map<Integer, String>> call() {
		if(bitLength > 0) {
			return this.createKeySet(bitLength);
		}
		if(p > 0 && q > 0) {
			return this.createKeySet(p, q);
		}
		return this.createKeySet();
	}

	private ArrayList<Map<Integer, String>> createKeySet() {
		SecureRandom r = new SecureRandom();
		return this.createKeySet(
			new BigInteger(1024, 100, r),
			new BigInteger(1024, 100, r));
	}

	private ArrayList<Map<Integer, String>> createKeySet(int bitLength) {
		SecureRandom r = new SecureRandom();
		return this.createKeySet(
			new BigInteger(bitLength, 100, r),
			new BigInteger(bitLength, 100, r));
	}

	private ArrayList<Map<Integer, String>> createKeySet(int p, int q) {
		return this.createKeySet(
			BigInteger.valueOf(p),
			BigInteger.valueOf(q));
	}

	private ArrayList<Map<Integer, String>> createKeySet(BigInteger p, BigInteger q) {
		BigInteger cat = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		BigInteger n = p.multiply(q);
		BigInteger d = new BigInteger("17");
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
		ArrayList<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
		list.add(publicKey);
		list.add(privateKey);
		return list;
	}
}

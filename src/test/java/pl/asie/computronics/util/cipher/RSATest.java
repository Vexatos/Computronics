package pl.asie.computronics.util.cipher;

import com.google.common.base.Charsets;
import pl.asie.lib.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

/**
 * Random RSA, encoding and BigInteger test
 * @author Vexatos
 */
class RSATest {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		ArrayList<Map<Integer, String>> keySet = new RSACalculationTask().call();
		long newTime = System.currentTimeMillis();
		System.err.println(newTime - time + " ms passed.");
		time = newTime;
		Map<Integer, String> publicKey = keySet.get(0);
		Map<Integer, String> privateKey = keySet.get(1);
		System.out.println(publicKey.get(1));
		try {
			System.out.println(new BigInteger(Base64.decode(publicKey.get(1))));
		} catch(IOException e) {
			e.printStackTrace();
		}
		newTime = System.currentTimeMillis();
		System.err.println(newTime - time + " ms passed.");
		time = newTime;
		System.out.println("====TEST====");
		BigInteger message = new BigInteger(("Hello World!").getBytes(Charsets.UTF_8));
		try {
			BigInteger n = new BigInteger(Base64.decode(publicKey.get(1)));
			BigInteger d = new BigInteger(Base64.decode(publicKey.get(2)));
			String encoded = Base64.encodeBytes(message.modPow(d, n).toByteArray());
			System.out.println("Encoded: " + encoded);
			newTime = System.currentTimeMillis();
			System.err.println(newTime - time + " ms passed.");
			time = newTime;
			System.out.println("---");
			message = new BigInteger(Base64.decode(encoded.getBytes(Charsets.UTF_8)));
			n = new BigInteger(Base64.decode(privateKey.get(1)));
			BigInteger e = new BigInteger(Base64.decode(privateKey.get(2)));
			System.out.println("Decoded: " + new String(message.modPow(e, n).toByteArray(), Charsets.UTF_8));
			newTime = System.currentTimeMillis();
			System.err.println(newTime - time + " ms passed.");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

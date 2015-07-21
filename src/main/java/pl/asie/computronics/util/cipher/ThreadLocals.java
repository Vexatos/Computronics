package pl.asie.computronics.util.cipher;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vexatos
 */
public class ThreadLocals {

	public static class LocalKeyPairGenerator extends ThreadLocal<KeyPairGenerator> {

		@Override
		protected KeyPairGenerator initialValue() {
			try {
				return KeyPairGenerator.getInstance("RSA");
			} catch(NoSuchAlgorithmException e) {
				return super.initialValue();
			}
		}
	}

	public static class LocalKeyFactory extends ThreadLocal<KeyFactory> {

		@Override
		protected KeyFactory initialValue() {
			try {
				return KeyFactory.getInstance("RSA");
			} catch(Exception e) {
				return super.initialValue();
			}
		}
	}

	public static class LocalCipher extends ThreadLocal<Cipher> {

		@Override
		protected Cipher initialValue() {
			try {
				return Cipher.getInstance("RSA/ECB/PKCS1Padding");
			} catch(Exception e) {
				return super.initialValue();
			}
		}
	}

}

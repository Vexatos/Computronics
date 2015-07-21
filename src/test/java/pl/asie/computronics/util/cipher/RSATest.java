package pl.asie.computronics.util.cipher;

import com.google.common.base.Charsets;
import pl.asie.lib.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Random RSA, encoding and BigInteger tests
 * @author Vexatos
 */
class RSATest {

	private static void testEncoding(final String msg) {
		System.out.println("Original: " + msg);
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
		BigInteger message = new BigInteger(msg.getBytes(Charsets.UTF_8));
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

	private static void testRSA(String msg, boolean swapKeys, int repeat) {
		int success = 0, failed = 0;
		for(int i = 0; i < repeat; i++) {
			//ArrayList<Map<Integer, String>> keySet = new RSACalculationTask().call();
			//Map<Integer, String> publicKey = keySet.get(0);
			//Map<Integer, String> privateKey = keySet.get(1);
			//msg = "{to=\"AKV8iZtovmZVTGjtf4sEbyHOAMEZBFdLc/l24JTNOFMV+Gv30tCpDz2n4Tq2ceAWZP7bx+ns9V23AyqyrqlyArQImcRPdgj/Sg5e5Q6ZBwwVZk8myRQDJ/9w9MLgz7x/5vd8FeTn6ruRpN36/WCpdf7yfqH4+asbKvhKiab1a8kdxjbNZLFy5XOlTUM7A7yiBuvzbRseBn0NkKNz7qVpxlWRFjptxo5QkVTVdNw6XiLlaW/oSgup4KUrMPHD5mVXYXza0Hq+JvBftTvjs5NQazrVANrqCqdL4XMMQ4YCPT2oC1gtrFXVWe95G//WjdhaYgtlMqvgLdJRXynbrJncwIc=:EQ==\",amount=10,comment=\"B5Vp0b/luf98z4Z9DNZWWrbQ5WPVXfXWdnsihFj6SeWcg4G1Qti2oWjEsCJsAAAAAA==\"}";
			msg = "{amount=10,comment=\"B5Vp0b/luf98z4Z9DNZWWrbQ5WPVXfXWdnsihFj6SeWcg4G1Qti2oWjEsCJsAAAAAA==\",to=\"AKV8iZtovmZVTGjtf4sEbyHOAMEZBFdLc/l24JTNOFMV+Gv30tCpDz2n4Tq2ceAWZP7bx+ns9V23AyqyrqlyArQImcRPdgj/Sg5e5Q6ZBwwVZk8myRQDJ/9w9MLgz7x/5vd8FeTn6ruRpN36/WCpdf7yfqH4+asbKv";
			Map<Integer, String> publicKey=new LinkedHashMap<Integer, String>();
			publicKey.put(1, "eJ2S+6byEJNJM8Yvj4PezSzxBMyws3TPZh8Qu1Mn7rLiAnfyd0SVVURj5xY0xFfYcpcK2YYTv/IjawhC8W9kRv71PvtvCU8De5+EJschtKeKb9b1Qi2aSPyWNd2iMT0X4DJinCxTSJkDEXY/fL+/JdfwzXCvRomX+0Fa/OPhE0y1wFKMVWd2f9s1YV7GLKOasv7t9df77CMD3kijM8yjjSb4aMADbmqQtES2oVvT/uRJkL36FuR/JaxOtg385R6jd+Tn2g+dHHttImNyHXK844Nm6bboE2H9nuOTxOyu3khtngUBkNRzC5cMw9d1KcoUbavBpkk4utdBHQiiupGIMQ==");
			publicKey.put(2, "EQ==");
			Map<Integer, String> privateKey=new LinkedHashMap<Integer, String>();
			privateKey.put(1, "eJ2S+6byEJNJM8Yvj4PezSzxBMyws3TPZh8Qu1Mn7rLiAnfyd0SVVURj5xY0xFfYcpcK2YYTv/IjawhC8W9kRv71PvtvCU8De5+EJschtKeKb9b1Qi2aSPyWNd2iMT0X4DJinCxTSJkDEXY/fL+/JdfwzXCvRomX+0Fa/OPhE0y1wFKMVWd2f9s1YV7GLKOasv7t9df77CMD3kijM8yjjSb4aMADbmqQtES2oVvT/uRJkL36FuR/JaxOtg385R6jd+Tn2g+dHHttImNyHXK844Nm6bboE2H9nuOTxOyu3khtngUBkNRzC5cMw9d1KcoUbavBpkk4utdBHQiiupGIMQ==");
			privateKey.put(2, "XDxDOOkTdhZHCXlvqftuJG2pP+fSax0XEduFQ/RLtojK8tQx4rv5uazE3ePN/454V6Ct8aLDzwR1b/dCQCgBY3ese3T6jqXVfKco0lwKt1LxZJVSI4xIzmbNOEASnh+o2J8AHRLWRpMgdsPWMjhG4LQwnRnvciz7wCLrOdtv4ZPyTSJmD1PPjYLhV7TurbaeKp9o5Lf6N8lSLH9uoozWO0wanHXl2Kd4LE7u5U0OWebq4VfKbUXzhES7iU7bEZrq+iDK9u8g9+Ocz5bnmE/XKg4Vn/28pR7YiuC2J5dFqlvR4Fn6nnmIjG3QXcBhMCiQ3Ei5Uy04Pg6J9fqCjstOxQ==");
			String realMessage = null;
			try {
				BigInteger message = new BigInteger(msg.getBytes(Charsets.UTF_8));
				BigInteger n = new BigInteger(Base64.decode(publicKey.get(1)));
				BigInteger d = new BigInteger(Base64.decode(publicKey.get(2)));
				BigInteger e = new BigInteger(Base64.decode(privateKey.get(2)));
				System.err.println(n);
				System.err.println(d);
				System.err.println(e);
				if(swapKeys){
					BigInteger od = d;
					d = e;
					e = od;
				}

				String encoded = Base64.encodeBytes(message.modPow(d, n).toByteArray());
				String wrongencoded = "HYfGuEOV8T0DcbAPZPecdUiiZ19qZ33f2UGDO4RA9Dv7mWd5abZUCVh0dElC6e3sejznBTmIY+2Th0PdxUm9tvRtWRTQ3QLQSXIQtzazkEeNSmyDqOQS7dVBTpaA5m8KEFW5uO/l13P30/jNk7UoWqU/ZBg/AVwnoPuxF8irqZO1a8zvHkaZ3+RISloCE93SN/PbzZXBNkswvtcjBLIc8Cbj+ism3/LFjwUpjyVEqKbX37vpd06WakHiODtZmuJmh+Vk52tRvQR3gu2a52tHX/J4Ek9pFL3b8sqTt3FuCe+Jl8OynGIIXTsttnoms78hg8m2n5yfjYp7StHz+0IxFw==";
				System.err.println(wrongencoded);
				System.err.println();
				System.err.println(encoded);

				realMessage = new String(new BigInteger(Base64.decode(encoded.getBytes(Charsets.UTF_8))).modPow(e, n).toByteArray(), Charsets.UTF_8);
			} catch(IOException e) {
				e.printStackTrace();
			}
			if(msg.equals(realMessage)) {
				success++;
			} else {
				failed++;
			}
			System.out.println("========");
			System.out.println("Success: " + success);
			System.out.println("Failed:  " + failed + (!msg.equals(realMessage) ? " (" + realMessage + ")" : ""));
			System.out.println(msg);
			System.out.println(realMessage);
		}
	}

	public static void main(String[] args) {
		String s1 = "Hello World!";
		String s2 = "Hello World! 1234567891 \n\täöüß♡♥❤❥{$\"%/§&%&%'()§/!&(%}";
		//testEncoding(s2);
		testRSA(s2, true, 1);
	}
}

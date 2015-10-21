package pl.asie.computronics.audio.tts.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.reference.Mods;
import sun.misc.JarFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * MaryTTS cannot load in FML's class loader, this is why we have to add it ourselves.
 * @author Vexatos
 */
public class TextToSpeechLoader {

	public static final TextToSpeechLoader INSTANCE = new TextToSpeechLoader();
	private boolean hasDoneInit = false;
	public static Logger log = LogManager.getLogger(Mods.Computronics + "-text-to-spech-loader");
	public static File ttsDir;

	public PrintWriter out;
	public BufferedReader in;
	public Socket socket;

	public boolean preInit() {
		ttsDir = new File(System.getProperty("user.dir"));
		ttsDir = new File(ttsDir, "marytts");
		if(!ttsDir.exists()) {
			log.info("No MaryTTS folder found, disable Text To Speech");
			return hasDoneInit = false;
		}
		if(!ttsDir.isDirectory()) {
			log.error("Could not read MaryTTS folder - found a file, not a directory!");
			return hasDoneInit = false;
		}
		File[] files = ttsDir.listFiles(new JarFilter());
		if(files == null || files.length <= 0) {
			log.error("Found an empty or invalid marytts directory, Text To Speech will not be initialized");
			return hasDoneInit = false;
		}
		try {
			String path = null;
			for(File file : files) {
				if(file.getName().contains("computronics")) {
					path = file.getCanonicalPath();
					break;
				}
			}
			if(path != null) {
				final ProcessBuilder pb = new ProcessBuilder(new File(System.getProperty("java.home"), "bin/java").getCanonicalPath(), "-cp",
					path, "pl.asie.computronics.audio.tts.core.TextToSpeechRelay");
				final Map<String, String> env = pb.environment();

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							log.info("Launching TTS relay.");
							ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
							env.put("TTS-Port", String.valueOf(serverSocket.getLocalPort()));
							Process p = pb.start();
							socket = serverSocket.accept();
							log.info("Launch successful.");
							out = new PrintWriter(socket.getOutputStream(), true);
							in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							out.write("Hello!");
						} catch(Exception e) {
							// NO-OP
						} finally {

						}
					}
				}, "Computronics Text-to-Speech Listener");
			}
			return true;
		} catch(Exception e) {
			log.error("Text To Speech folder initialization failed, you will not be able to hear anything", e);
			return hasDoneInit = false;
		}
	}

	public boolean hasDoneInit() {
		return this.hasDoneInit;
	}
}

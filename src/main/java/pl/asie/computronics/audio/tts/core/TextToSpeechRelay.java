package pl.asie.computronics.audio.tts.core;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.util.MaryRuntimeUtils;
import pl.asie.computronics.audio.tts.synth.SynthesizeTask;
import sun.misc.JarFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author Vexatos
 */
public class TextToSpeechRelay {
	public static ExecutorService ttsThreads;
	public static MaryInterface marytts;

	private ClasspathAdder classpathAdder;
	public static File ttsDir;

	private Logger log;

	public boolean run() {
		URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
		classpathAdder = new ClasspathAdder(classLoader);
		log = Logger.getLogger("Computronics-TTS");
		try {
			Class.forName("marytts.MaryInterface");
			Class.forName("marytts.LocalMaryInterface");
			Class.forName("marytts.server.Mary");
			log.fine("MaryTTS in classpath.");
		} catch(Exception e) {
			log.fine("No MaryTTS in classpath.");
		}
		ttsDir = new File(System.getProperty("user.dir"));
		ttsDir = new File(ttsDir, "marytts");
		if(!ttsDir.exists()) {
			log.info("No MaryTTS folder found, disable Text To Speech");
			return false;
		}
		if(!ttsDir.isDirectory()) {
			log.severe("Could not read MaryTTS folder - found a file, not a directory!");
			return false;
		}
		File[] files = ttsDir.listFiles(new JarFilter());
		if(files == null || files.length <= 0) {
			log.severe("Found an empty or invalid marytts directory, Text To Speech will not be initialized");
			return false;
		}
		Arrays.sort(files);
		for(File file : files) {
			if(file.isDirectory() || !file.exists()) {
				continue;
			}
			try {
				log.info("Found Text-to-speech file " + file.getName());
				//classLoader.addURL(file.toURI().toURL());
				classpathAdder.addFile(file);
			} catch(IOException e) {
				log.severe("Error trying to load " + file.getName());
				e.printStackTrace();
			}
		}
		//Check for marytts to be present
		try {
			//classLoader.findClass("marytts.MaryInterface");
			//classLoader.findClass("marytts.LocalMaryInterface");
			//classLoader.findClass("marytts.server.Mary");
			//classLoader.findClass("pl.asie.computronics.audio.tts.core.TextToSpeech");
			Class.forName("marytts.MaryInterface");
			Class.forName("marytts.LocalMaryInterface");
			Class.forName("marytts.server.Mary");
			//Class.forName("pl.asie.computronics.audio.tts.core.TextToSpeech");
		} catch(Exception e) {
			log.severe("Text To Speech folder initialization failed, you will not be able to hear anything");
			e.printStackTrace();
			return false;
		}

		try {
			marytts = new LocalMaryInterface();
			//Set<String> voices = marytts.getAvailableVoices();
			marytts.setStreamingAudio(true);
			//marytts.setLocale(Locale.US);
			//marytts.setVoice(voices.iterator().next());
			marytts.setOutputType("AUDIO");
			ttsThreads = Executors.newCachedThreadPool();

			Socket socket = new Socket(InetAddress.getLoopbackAddress(), 0);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));


			String inputLine, outputLine;

			// Initiate conversation with client
			out.println("Hello!");

			while ((inputLine = in.readLine()) != null) {
				ttsThreads.submit(new SynthesizeTask(inputLine));
				if(outputLine.equals("Bye."))
					break;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void main(String[] args) {
		new TextToSpeechRelay().run();
	}

	/**
	 * Useful class for dynamically changing the classpath, adding classes during runtime.
	 */
	private static class ClasspathAdder {

		private final ClassLoader classLoader;

		private ClasspathAdder(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		/**
		 * Adds a file to the classpath.
		 * @param path a String pointing to the file
		 * @throws IOException
		 */
		private void addFile(String path) throws IOException {
			File f = new File(path);
			addFile(f);
		}

		/**
		 * Adds a file to the classpath
		 * @param file the file to be added
		 * @throws IOException
		 */
		private void addFile(File file) throws IOException {
			addURL(file.toURI().toURL());
		}

		/**
		 * Adds the content pointed by the URL to the classpath.
		 * @param url the URL pointing to the content to be added
		 * @throws IOException
		 */
		private void addURL(URL url) throws IOException {
			try {
				ClassLoader parent = this.classLoader.getParent();
				URLClassLoader sysloader = parent != null && parent instanceof URLClassLoader ?
					((URLClassLoader) parent) : (URLClassLoader) ClassLoader.getSystemClassLoader();
				Class<?> sysclass = URLClassLoader.class;
				Method method = sysclass.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				method.invoke(sysloader, url);
			} catch(Throwable t) {
				t.printStackTrace();
				throw new IOException("Error, could not add URL to system classloader", t);
			}
		}
	}
}

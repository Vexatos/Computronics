package pl.asie.computronics.audio.tts;

import marytts.server.MaryProperties;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.reference.Mods;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * MaryTTS cannot load in FML's class loader, this is why we have to add it ourselves.
 * @author Vexatos
 */
public class TextToSpeechLoader {

	public static final TextToSpeechLoader INSTANCE = new TextToSpeechLoader();
	private boolean hasDoneInit = false;
	public static final Logger log = LogManager.getLogger(Mods.Computronics + "-text-to-speech-loader");
	public static File ttsDir;

	private ClasspathAdder classpathAdder;

	/*static {
		ClassLoader classLoader = TextToSpeech.class.getClassLoader();
		System.out.println("Classloader: " + classLoader.toString());
	}*/

	public boolean preInit() {
		log.info("Initializing Text To Speech");
		LaunchClassLoader classLoader = Launch.classLoader;
		ClassLoader ownClassLoader = getClass().getClassLoader();
		if(ownClassLoader instanceof LaunchClassLoader) {
			classLoader = (LaunchClassLoader) ownClassLoader;
		}
		classpathAdder = new ClasspathAdder(classLoader);
		classLoader.addClassLoaderExclusion("marytts.");
		classLoader.addClassLoaderExclusion("jtok.");
		classLoader.addClassLoaderExclusion("de.dfki.");
		// This is not forge's logger but the one mary uses.
		classLoader.addClassLoaderExclusion("org.apache.log4j.");
		//classLoader.addClassLoaderExclusion("pl.asie.computronics.audio.tts.");
		//classLoader.addClassLoaderExclusion("com.sun.org.apache.xalan.internal.xsltc.");
		try {
			Class.forName("marytts.MaryInterface");
			Class.forName("marytts.LocalMaryInterface");
			Class.forName("marytts.server.Mary");
			log.trace("MaryTTS in classpath.");
		} catch(Exception e) {
			log.trace("No MaryTTS in classpath.");
		}
		ttsDir = new File(System.getProperty("user.dir"));
		ttsDir = new File(ttsDir, "marytts");
		if(!ttsDir.exists()) {
			log.info("No MaryTTS directory found, you will not be able to use Text To Speech unless the server you are playing on has MaryTTS installed. To use TTS, install MaryTTS into the marytts directory of your minecraft instance.");
			return hasDoneInit = false;
		}
		if(!ttsDir.isDirectory()) {
			log.error("Could not load MaryTTS - found a file, not a directory!");
			return hasDoneInit = false;
		}
		File[] files = ttsDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name = name.toLowerCase(Locale.ROOT);
				return name.endsWith(".jar") || name.endsWith(".zip");
			}
		});
		if(files == null || files.length <= 0) {
			log.error("Found an empty or invalid marytts directory, Text To Speech will not be initialized");
			return hasDoneInit = false;
		}
		Arrays.sort(files);
		for(File file : files) {
			if(file.isDirectory() || !file.exists()) {
				continue;
			}
			try {
				log.info("Found Text-to-speech file " + file.getName());
				classLoader.addURL(file.toURI().toURL());
				classpathAdder.addFile(file);
			} catch(IOException e) {
				log.error("Error trying to load " + file.getName(), e);
			}
		}
		//Check for marytts to be present
		try {
			//classLoader.findClass("marytts.MaryInterface");
			//classLoader.findClass("marytts.LocalMaryInterface");
			//classLoader.findClass("marytts.server.Mary");
			//classLoader.findClass("pl.asie.computronics.audio.tts.TextToSpeech");
			Class.forName("marytts.MaryInterface");
			Class.forName("marytts.LocalMaryInterface");
			Class.forName("marytts.server.Mary");
			//Class.forName("pl.asie.computronics.audio.tts.TextToSpeech");

			// Taken from Mary.configureLogging, needs to be called manually due to the logger already being loaded by this time.
			{
				Properties logprops = new Properties();
				InputStream propIS = new BufferedInputStream(MaryProperties.needStream("log.config"));
				logprops.load(propIS);
				propIS.close();

				for(Object key : logprops.keySet()) {
					String val = (String) logprops.get(key);
					if(val.contains("MARY_BASE")) {
						String maryBase = MaryProperties.maryBase();
						if(maryBase.contains("\\")) {
							maryBase = maryBase.replaceAll("\\\\", "/");
						}

						val = val.replaceAll("MARY_BASE", maryBase);
						logprops.put(key, val);
					}
				}

				String loggerMaryttsKey = "log4j.logger.marytts";
				String loggerMaryttsValue = MaryProperties.getProperty(loggerMaryttsKey);
				if(loggerMaryttsValue != null) {
					logprops.setProperty(loggerMaryttsKey, loggerMaryttsValue);
				}

				PropertyConfigurator.configure(logprops);
			}
			return hasDoneInit = true;
		} catch(Exception e) {
			log.error("Text To Speech directory initialization failed, you will not be able to hear anything", e);
			return hasDoneInit = false;
		}
	}

	public boolean hasDoneInit() {
		return this.hasDoneInit;
	}

	/**
	 * Useful class for dynamically changing the classpath, adding classes during runtime.
	 */
	private static class ClasspathAdder {

		private final ClassLoader classLoader;

		private ClasspathAdder(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		private void addFile(String path) throws IOException {
			File f = new File(path);
			addFile(f);
		}

		private void addFile(File file) throws IOException {
			addURL(file.toURI().toURL());
		}

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

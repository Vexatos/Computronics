package pl.asie.computronics.audio.tts.core;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.computronics.reference.Mods;
import sun.misc.JarFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * MaryTTS cannot load in FML's class loader, this is why we have to add it ourselves.
 * @author Vexatos
 */
public class TextToSpeechLoader {
	public static final TextToSpeechLoader INSTANCE = new TextToSpeechLoader();
	public static Logger log = LogManager.getLogger(Mods.Computronics + "-text-to-spech-loader");
	public static File ttsDir;

	private ClasspathAdder classpathAdder;

	/*static {
		ClassLoader classLoader = TextToSpeech.class.getClassLoader();
		System.out.println("Classloader: " + classLoader.toString());
	}*/

	public boolean preInit() {
		LaunchClassLoader classLoader = Launch.classLoader;
		ClassLoader ownClassLoader = getClass().getClassLoader();
		if(ownClassLoader instanceof LaunchClassLoader) {
			classLoader = (LaunchClassLoader) ownClassLoader;
		}
		classpathAdder = new ClasspathAdder(classLoader);
		classLoader.addClassLoaderExclusion("marytts.");
		classLoader.addClassLoaderExclusion("jtok.");
		classLoader.addClassLoaderExclusion("de.dfki.");
		//classLoader.addClassLoaderExclusion("pl.asie.computronics.audio.tts.core.");
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
			log.info("No MaryTTS folder found, disable Text To Speech");
			return false;
		}
		if(!ttsDir.isDirectory()) {
			log.error("Could not read MaryTTS folder - found a file, not a directory!");
			return false;
		}
		File[] files = ttsDir.listFiles(new JarFilter());
		if(files == null || files.length <= 0) {
			log.error("Found an empty or invalid marytts directory, Text To Speech will not be initialized");
			return false;
		}
		for(File file : files) {
			if(file.isDirectory() || !file.exists()) {
				continue;
			}
			try {
				classLoader.addURL(file.toURI().toURL());
				classpathAdder.addFile(file);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		//Check for marytts to be present
		try {
			classLoader.findClass("marytts.MaryInterface");
			classLoader.findClass("marytts.LocalMaryInterface");
			classLoader.findClass("marytts.server.Mary");
			//classLoader.findClass("pl.asie.computronics.audio.tts.core.TextToSpeech");
			Class.forName("marytts.MaryInterface");
			Class.forName("marytts.LocalMaryInterface");
			Class.forName("marytts.server.Mary");
			//Class.forName("pl.asie.computronics.audio.tts.core.TextToSpeech");
			return true;
		} catch(Exception e) {
			log.error("Text To Speech folder initialization failed, you will not be able to hear anything");
			e.printStackTrace();
			return false;
		}
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

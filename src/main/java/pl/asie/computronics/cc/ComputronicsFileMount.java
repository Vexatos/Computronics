package pl.asie.computronics.cc;

import dan200.computercraft.api.filesystem.IMount;
import pl.asie.computronics.Computronics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author Vexatos
 */
public class ComputronicsFileMount implements IMount {

	private final File root;

	protected ComputronicsFileMount(File root) {
		this.root = root;
	}

	/*private File getFile(String path) {
		if(!fileCache.containsKey(path)) {
			fileCache.put(path, new File(root, path));
		}
		return fileCache.get(path);
	}*/

	@Override
	public boolean exists(String path) throws IOException {
		return new File(root, path).exists();
	}

	@Override
	public boolean isDirectory(String path) throws IOException {
		return new File(root, path).isDirectory();
	}

	@Override
	public void list(String path, List<String> contents) throws IOException {
		if(!root.exists() || !root.isDirectory()) {
			throw new IOException("Not a directory");
		}
		String[] paths = root.list();
		for(String s : paths) {
			if((new File(root, s)).exists()) {
				contents.add(s);
			}
		}
	}

	@Override
	public long getSize(String path) throws IOException {
		File file = new File(root, path);
		if(file.exists()) {
			return file.isDirectory() ? 0L : file.length();
		}
		throw new FileNotFoundException(path);
	}

	@Override
	public InputStream openForRead(String path) throws IOException {
		File file = new File(root, path);
		if(!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(path);
		}
		return new FileInputStream(file);
	}

	/**
	 * @author Sangar, Vexatos
	 */
	public static IMount createMount(Class clazz, String domain, String root) {
		try {
			String innerPath = ("assets/" + domain + "/" + root.trim()).replace("//", "/");
			String codeSource = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();

			String codeUrl;
			boolean isArchive;
			if(codeSource.contains(".zip!") || codeSource.contains(".jar!")) {
				codeUrl = codeSource.substring(0, codeSource.lastIndexOf('!'));
				isArchive = true;
			} else {
				codeUrl = codeSource;
				isArchive = false;
			}

			URL url;
			try {
				url = new URL(codeUrl);
			} catch(MalformedURLException e) {
				try {
					url = new URL("file://" + codeUrl);
				} catch(MalformedURLException e1) {
					url = null;
				}
			}
			File file;
			if(url != null) {
				try {
					file = new File(url.toURI());
				} catch(URISyntaxException e) {
					file = new File(url.getPath());
				}
			} else {
				file = new File(codeSource);
			}

			if(isArchive) {
				if(jarMount != null) {
					return (IMount) jarMount.newInstance(file, innerPath);
				}
			} else {
				if(!file.exists() || file.isDirectory()) {
					return null;
				}
				File parent = new File(new File(file.getParent()), innerPath);
				if(parent.exists()) {
					return new ComputronicsFileMount(file);
				} else {
					String[] paths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
					for(String s : paths) {
						File f = new File(new File(s), innerPath);
						if(f.exists()) {
							return new ComputronicsFileMount(f);
						}
					}
				}
			}
			return null;
		} catch(Exception e) {
			Computronics.log.error("Unable to create ComputerCraft file mount", e);
		}
		return null;
	}

	private static final Constructor<?> jarMount;

	static {
		Constructor<?> constr = null;
		try {
			constr = Class.forName("dan200.computercraft.core.filesystem.JarMount").getConstructor(File.class, String.class);
		} catch(Exception e) {
			Computronics.log.error("Unable to access ComputerCraft jar file mount", e);
		}
		jarMount = constr;
	}
}

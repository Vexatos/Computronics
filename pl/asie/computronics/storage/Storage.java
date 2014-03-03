package pl.asie.computronics.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Storage {
	private String name;
	private File file;
	private int size;
	private byte[] data;
	private int position;
	private boolean modified = false;
	
	protected Storage(String storageName, File file, int size, int position) {
		this.name = storageName;
		this.file = file;
		this.size = size;
		this.data = new byte[size];
		this.position = position;
		
		if(!file.exists()) {
			// Create new file
			try {
				file.createNewFile();
				writeFile();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				readFile();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getName() { return name; }
	public int getPosition() { return position; }
	public int getSize() { return size; }
	
	public int setPosition(int newPosition) {
		if(newPosition < 0) newPosition = 0;
		if(newPosition >= size) newPosition = size - 1;
		this.position = newPosition;
		return newPosition;
	}
	
	public int trySeek(int dir) {
		int oldPosition = position;
		int newPosition = position + dir;
		if(newPosition < 0) newPosition = 0;
		if(newPosition >= size) newPosition = size - 1;
		return newPosition - oldPosition;
	}
	public int seek(int dir) {
		int seek = trySeek(dir);
		position += seek;
		return seek;
	}
	
	public int read() {
		return (int)data[position++] & 0xFF;
	}
	public int read(byte[] v) {
		int len = v.length;
		if(position + len >= size)
			len = (size-1) - position;
		
		System.arraycopy(data, position, v, 0, len);
		position += len;
		
		return len;
	}
	
	public void write(byte v) {
		data[position++] = v;
		
		modified = true;
	}
	public int write(byte[] v) {
		int len = v.length;
		if(position+len >= size) len = (size-1) - position;
		if(len == 0) return 0;
		
		System.arraycopy(v, 0, data, position, len);
		position += len;
		
		modified = true;
		return len;
	}
	
	
	public void readFile() throws IOException {
		FileInputStream fileStream = new FileInputStream(file);
		GZIPInputStream stream = new GZIPInputStream(fileStream);
		this.data = new byte[size];
		
		int position = 0;
		while(position < this.data.length) {
			position += stream.read(this.data, position, this.data.length - position);
		}
		
		stream.close();
		fileStream.close();
	}
	
	public void writeFile() throws IOException {
		FileOutputStream fileStream = new FileOutputStream(file);
		GZIPOutputStream stream = new GZIPOutputStream(fileStream);
		
		stream.write(data);
		stream.finish();
		stream.flush();
		stream.close();
		fileStream.close();
		
		modified = false;
	}
	
	public void writeFileIfModified() throws IOException {
		if(modified) writeFile();
	}
}

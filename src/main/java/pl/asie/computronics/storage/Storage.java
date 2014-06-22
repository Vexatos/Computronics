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
		modified = true;
		return seek;
	}
	
	public int read() {
		if(position >= size) return 0;
		
		modified = true;
		return (int)data[position++] & 0xFF;
	}
	
	public int read(byte[] v, int offset, boolean simulate) {
		int len = Math.min(size - (position + offset) - 1, v.length);
		System.arraycopy(data, position + offset, v, 0, len);
		if(!simulate) {
			position += len;
			modified = true;
		}
		
		return len;
	}
	
	public int read(byte[] v, boolean simulate) {
		return read(v, 0, simulate);
	}
	
	public void write(byte v) {
		if(position >= size) return;
		
		data[position++] = v;
		
		modified = true;
	}
	
	public int write(byte[] v) {
		int len = Math.min(size - (position) - 1, v.length);
		if(len == 0) return 0;
		
		System.arraycopy(v, 0, data, position, len);
		position += len;
		
		modified = true;
		return len;
	}
	
	
	public void readFile() throws IOException {
		FileInputStream fileStream = new FileInputStream(file);
		GZIPInputStream stream = new GZIPInputStream(fileStream);
		
		int version = stream.read();
		if(version >= 1) {
			// Read position
			int b1 = stream.read() & 0xFF;
			int b2 = stream.read() & 0xFF;
			int b3 = stream.read() & 0xFF;
			int b4 = stream.read() & 0xFF;
			this.position = b1 | (b2<<8) | (b3<<16) | (b4<<24);
		}
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
		
		stream.write(1);
		stream.write(this.position & 0xFF);
		stream.write((this.position >>> 8) & 0xFF);
		stream.write((this.position >>> 16) & 0xFF);
		stream.write((this.position >>> 24) & 0xFF);
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

/*
 * Copyright (c) 2008-2010, Matthias Mann
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Matthias Mann nor the names of its contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pl.asie.computronics.audio.tts.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Matthias Mann
 */
public class TextToSpeechStreamHandler extends URLStreamHandler {

	private final ArrayList<VirtualFile> files;

	public TextToSpeechStreamHandler() {
		this.files = new ArrayList<VirtualFile>();
	}

	public void unregisterFile(VirtualFile file) {
		files.remove(file);
	}

	public void unregisterFiles(Collection<VirtualFile> file) {
		files.removeAll(file);
	}

	public void registerFile(VirtualFile file) {
		files.add(file);
	}

	public VirtualFile registerFile(final URL url) {
		VirtualFile file = new VirtualFile() {
			public String getVirtualFileName() {
				return url.getFile();
			}

			public Object getContent(Class<?> type) throws IOException {
				return url.getContent(new Class[] { type });
			}

			public InputStream openStream() throws IOException {
				return url.openStream();
			}
		};
		registerFile(file);
		return file;
	}

	protected VirtualFile findVirtualFile(String fileName) {
		for(VirtualFile file : files) {
			String vFileName = file.getVirtualFileName();
			if(vFileName.equals(fileName)) {
				return file;
			}
		}
		return null;
	}

	@Override
	protected URLConnection openConnection(final URL url) throws IOException {
		final String fileName = url.getFile();
		final VirtualFile file = findVirtualFile(fileName);
		return new URLConnection(url) {
			@Override
			public void connect() throws IOException {
				if(file == null) {
					throw new FileNotFoundException(fileName);
				}
			}

			@Override
			public Object getContent(Class[] classes) throws IOException {
				if(file != null && classes.length > 0) {
					return file.getContent(classes[0]);
				}
				return null;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				if(file == null) {
					throw new FileNotFoundException(fileName);
				}
				return file.openStream();
			}
		};
	}

	public URL getURL(String file) throws MalformedURLException {
		return new URL("computronics-tts", "local", 80, file, this);
	}
}

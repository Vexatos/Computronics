package pl.asie.computronics.audio;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.audio.StreamingPlaybackManager;
import gme.ClassicEmu;
import gme.GbsEmu;
import gme.NsfEmu;
import gme.SpcEmu;
import gme.VgmEmu;

public class GMEManager implements Runnable {
	public enum Type {
		MOD, S3M, XM, NSF, VGM, GBS
	}

	public static StreamingAudioPlayer create() {
		return new StreamingAudioPlayer(48000, false, true, 0);
	}
	
	private ClassicEmu emulator;
	private int x, y, z;
	public boolean finished;
	
	public static HashSet<GMEManager> players = new HashSet<GMEManager>();
	
	private GMEManager(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.finished = false;
	}
	
	private GMEManager(ClassicEmu emu, int x, int y, int z) {
		this(x, y, z);
		this.emulator = emu;
	}
	
	public static void stop(int x, int y, int z) {
		synchronized(players) {
			for(GMEManager m: players) {
				if(m.x == x && m.y == y && m.z == z)
					m.finished = true;
			}
		}
	}
	
	public static void play(Type type, byte[] data, int x, int y, int z, int track) {
		ClassicEmu emulator = null;
		switch(type) {
			case MOD:
				break;
			case S3M:
				break;
			case XM:
				break;
			case GBS:
				emulator = new GbsEmu();
				break;
			case NSF:
				emulator = new NsfEmu();
				break;
			case VGM:
				emulator = new VgmEmu();
				break;
			default:
				return;
		}
		if(emulator != null) {
			emulator.setSampleRate(48000);
			emulator.loadFile(data);
			emulator.startTrack(track);
			GMEManager manager = new GMEManager(emulator, x, y, z);
			(new Thread(manager)).start();
			synchronized(players) {
				players.add(manager);
			}
		}
	}

	@Override
	public void run() {
		while(!finished) {
			StreamingAudioPlayer player = this.create();
			int generated = 0;
			boolean made = false;
			try {
				long timeOld = (new Date()).getTime();
				
				made = false;
				while(!made || generated < 2) {
					byte[] output = new byte[48000 * 2];
					emulator.play(output, 48000);
					player.playPacket(output, x, y, z);
					made = true;
					generated++;
				}
				
				if(emulator.trackEnded()) finished = true;
				if(player.getDistance(x, y, z) > 64.0F) finished = true;

				long timeNew = (new Date()).getTime();
				Thread.sleep(1000 - (timeNew - timeOld));
			} catch(Exception e) { e.printStackTrace(); }
		}
		synchronized(players) {
			players.remove(this);
		}
	}
}

package pl.asie.computronics.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pl.asie.computronics.Computronics;
import pl.asie.computronics.api.audio.AudioPacketClientHandler;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.sound.AudioType;
import pl.asie.computronics.util.sound.AudioUtil;
import pl.asie.computronics.util.sound.AudioUtil.AudioProcess;
import pl.asie.computronics.util.sound.Instruction;
import pl.asie.computronics.util.sound.Instruction.*;
import pl.asie.lib.audio.StreamingAudioPlayer;
import pl.asie.lib.network.Packet;

/**
 * @author gamax92
 */
@SideOnly(Side.CLIENT)
public class SoundCardPacketClientHandler extends AudioPacketClientHandler {
	private Map<String, AudioProcess> processMap = new HashMap<String, AudioProcess>();
	private Map<String, Long> timeoutMap = new HashMap<String, Long>();
	private int sampleRate = Config.SOUND_SAMPLE_RATE;
	private int soundTimeoutMS = 250;

	@Override
	protected void readData(Packet packet, int packetId, int codecId) throws IOException {
		String address = packet.readString();
		int delay = packet.readInt();
		int size = packet.readInt();
		Queue<Instruction> buffer = new LinkedList<Instruction>();
		for (int i=0; i<size; i++) {
			int type = packet.readByte();
			switch(type) {
				case 0:
					buffer.add(new Open(packet.readByte()));
					break;
				case 1:
					buffer.add(new Close(packet.readByte()));
					break;
				case 2:
					buffer.add(new SetWave(packet.readByte(), AudioType.fromIndex(packet.readInt()), packet.readFloat()));
					break;
				case 3:
					buffer.add(new Delay(packet.readInt()));
					break;
				case 4:
					buffer.add(new SetFM(packet.readByte(), packet.readInt(), packet.readFloat()));
					break;
				case 5:
					buffer.add(new ResetFM(packet.readByte()));
					break;
				case 6:
					buffer.add(new SetAM(packet.readByte(), packet.readInt()));
					break;
				case 7:
					buffer.add(new ResetAM(packet.readByte()));
					break;
				case 8:
					buffer.add(new SetADSR(packet.readByte(), packet.readInt(), packet.readInt(), packet.readFloat(), packet.readInt()));
					break;
				case 9:
					buffer.add(new ResetEnvelope(packet.readByte()));
					break;
				case 10:
					buffer.add(new SetVolume(packet.readByte(), packet.readFloat()));
					break;
			}
		}

		if(!processMap.containsKey(address)) {
			processMap.put(address, new AudioUtil.AudioProcess(8));
			timeoutMap.put(address, System.currentTimeMillis());
		}
		AudioProcess process = processMap.get(address);
		long timeout = timeoutMap.get(address);

		ByteArrayOutputStream data = new ByteArrayOutputStream();
		while(!buffer.isEmpty() || process.delay > 0) {
			if(process.delay > 0) {
				int sampleCount = process.delay * sampleRate / 1000;
				for(int i = 0; i < sampleCount; ++i) {
					double sample = 0;
					for(AudioUtil.State state : process.states) {
						sample += state.gate.getValue(process, state)/8;
					}
					int value = ((byte) (sample * 127)) ^ 0x80;
					data.write((byte) value);
				}
				process.delay = 0;
			} else {
				Instruction inst = buffer.poll();
				inst.encounter(process);
			}
		}

		if (data.size() > 0) {
			StreamingAudioPlayer codec = Computronics.opencomputers.audio.getPlayer(codecId);
			if(System.currentTimeMillis() > timeout + soundTimeoutMS) {
				codec.stop();
				timeout = System.currentTimeMillis();
			}
			codec.setSampleRate(sampleRate);
			codec.push(data.toByteArray());
		}

		timeoutMap.put(address, timeout+delay);
	}

	@Override
	protected void playData(int packetId, int codecId, int x, int y, int z, int distance, byte volume) {
		StreamingAudioPlayer codec = Computronics.opencomputers.audio.getPlayer(codecId);

		codec.setHearing((float) distance, volume / 127.0F);
		try {
			codec.play(x, y, z);
		} catch(NullPointerException e) {
			// This exception occurs when there is no data to play, and is harmless.
		}
	}
}

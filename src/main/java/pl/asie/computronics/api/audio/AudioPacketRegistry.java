package pl.asie.computronics.api.audio;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public final class AudioPacketRegistry {
	public static final AudioPacketRegistry INSTANCE = new AudioPacketRegistry();

	private final TObjectIntMap<Class<? extends AudioPacket>> audioPacketIdMap = new TObjectIntHashMap<Class<? extends AudioPacket>>();
	private final TIntObjectMap<AudioPacketClientHandler> audioPacketHandlerMap = new TIntObjectHashMap<AudioPacketClientHandler>();
	private int nextId;

	private AudioPacketRegistry() {

	}

	public void registerType(Class<? extends AudioPacket> type) {
		audioPacketIdMap.put(type, nextId++);
	}

	public int getId(Class<? extends AudioPacket> packetClass) {
		return audioPacketIdMap.get(packetClass);
	}

	public void registerClientHandler(Class<? extends AudioPacket> packetClass, AudioPacketClientHandler handler) {
		audioPacketHandlerMap.put(getId(packetClass), handler);
	}

	public AudioPacketClientHandler getClientHandler(int id) {
		return audioPacketHandlerMap.get(id);
	}
}

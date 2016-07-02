package pl.asie.computronics.api.audio;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import pl.asie.lib.audio.StreamingPlaybackManager;

import javax.annotation.Nullable;

public final class AudioPacketRegistry {

	public static final AudioPacketRegistry INSTANCE = new AudioPacketRegistry();

	private final TObjectIntMap<Class<? extends AudioPacket>> audioPacketIdMap = new TObjectIntHashMap<Class<? extends AudioPacket>>();
	private final TIntObjectMap<AudioPacketClientHandler> audioPacketHandlerMap = new TIntObjectHashMap<AudioPacketClientHandler>();
	private final TIntObjectMap<StreamingPlaybackManager> playbackManagerMap = new TIntObjectHashMap<StreamingPlaybackManager>();
	private final TIntObjectMap<ICodec> codecMap = new TIntObjectHashMap<ICodec>();
	private int nextTypeId, nextManagerId;
	private byte nextCodecId;

	private AudioPacketRegistry() {

	}

	public void registerType(Class<? extends AudioPacket> type) {
		audioPacketIdMap.put(type, nextTypeId++);
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

	public int registerManager(StreamingPlaybackManager manager) {
		int managerId = nextManagerId++;
		playbackManagerMap.put(managerId, manager);
		return managerId;
	}

	public StreamingPlaybackManager getManager(int id) {
		return playbackManagerMap.get(id);
	}

	public int registerCodec(ICodec codec) {
		byte codecId = nextCodecId++;
		codecMap.put(codecId, codec);
		codec.setId(codecId);
		return codecId;
	}

	@Nullable
	public ICodec getCodec(int id) {
		return codecMap.get(id);
	}
}

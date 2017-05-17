package pl.asie.computronics.network;

public enum PacketType {
	AUDIO_DATA,
	AUDIO_STOP,
	TAPE_GUI_STATE,
	PARTICLE_SPAWN,
	COMPUTER_BEEP,
	COMPUTER_NOISE,
	COMPUTER_BOOM,
	TICKET_SYNC,
	TICKET_PRINT,
	PORTABLE_TAPE_STATE;

	public static final PacketType[] VALUES = values();

	public static PacketType of(int index) {
		return index >= 0 && index < VALUES.length ? VALUES[index] : null;
	}
}

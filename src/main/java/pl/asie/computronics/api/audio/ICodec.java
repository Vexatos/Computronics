package pl.asie.computronics.api.audio;

public interface ICodec {

	byte getId();

	void setId(byte id);

	void compress(byte[] dest, byte[] src, int destoffs, int srcoffs, int len);

	void decompress(byte[] dest, byte[] src, int destoffs, int srcoffs, int len);
}

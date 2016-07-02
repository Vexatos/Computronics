package pl.asie.lib.audio.codec;

import pl.asie.computronics.api.audio.ICodec;

/**
 * @author Vexatos
 */
public enum Codec implements ICodec {
	DFPWM() {
		private final pl.asie.lib.audio.codec.dfpwm.DFPWM codec = new pl.asie.lib.audio.codec.dfpwm.DFPWM();

		@Override
		public void compress(byte[] bytes, byte[] bytes1, int i, int i1, int i2) {
			codec.compress(bytes, bytes1, i, i1, i2);
		}

		@Override
		public void decompress(byte[] bytes, byte[] bytes1, int i, int i1, int i2) {
			codec.decompress(bytes, bytes1, i, i1, i2);
		}
	},
	DFPWM1a() {
		private final pl.asie.lib.audio.codec.dfpwm1a.DFPWM codec = new pl.asie.lib.audio.codec.dfpwm1a.DFPWM();

		@Override
		public void compress(byte[] bytes, byte[] bytes1, int i, int i1, int i2) {
			codec.compress(bytes, bytes1, i, i1, i2);
		}

		@Override
		public void decompress(byte[] bytes, byte[] bytes1, int i, int i1, int i2) {
			codec.decompress(bytes, bytes1, i, i1, i2);
		}
	};

	protected byte id;

	@Override
	public byte getId() {
		return this.id;
	}

	@Override
	public void setId(byte id) {
		this.id = id;
	}
}

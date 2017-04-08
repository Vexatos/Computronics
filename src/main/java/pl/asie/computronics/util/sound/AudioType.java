package pl.asie.computronics.util.sound;

/**
 * @author Vexatos
 */
public enum AudioType {
	Square {
		@Override
		public double generate(float pos) {
			return Math.signum(Sine.generate(pos)) * 0.5;
		}
	},
	Sine {
		@Override
		public double generate(float pos) {
			return Math.sin(2 * Math.PI * pos);
		}
	},
	Triangle {
		@Override
		public double generate(float pos) {
			return 1.0 - (Math.abs(pos - 0.5) * 4.0);
		}
	},
	Sawtooth {
		@Override
		public double generate(float pos) {
			return (2 * pos) - 1;
		}
	};

	public abstract double generate(float pos);

	public static final AudioType[] VALUES = values();

	public static AudioType fromIndex(int index) {
		return index >= 0 && index < VALUES.length ? VALUES[index] : Square;
	}
}

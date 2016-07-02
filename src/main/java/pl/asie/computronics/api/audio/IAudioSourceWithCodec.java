package pl.asie.computronics.api.audio;

/**
 * @author Vexatos
 */
public interface IAudioSourceWithCodec extends IAudioSource {

	ICodec getCodec();
}

package pl.asie.computronics.util.collect;

import com.google.common.collect.Multimap;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * An invertible {@link Multimap}. Changes to the inverse do affect the original map and vice-versa.
 * @author Vexatos
 */
public interface InvertibleMultimap<K, V> extends Multimap<K, V> {

	@Nonnull
	Inverse<V, K> inverse();

	/**
	 * Inverse of an {@link InvertibleMultimap}.
	 */
	interface Inverse<V, K> extends Map<V, K> {
		@Nonnull
		InvertibleMultimap<K, V> inverse();
	}
}

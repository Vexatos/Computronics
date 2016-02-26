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
	Map<V, K> inverse();
}

package pl.asie.computronics.util.collect;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Horribly inefficient {@link InvertibleMultimap} backed by a {@link HashMultimap}.
 * Returned instances of {@link Collection} or {@link Map} are always immutable.
 * @author Vexatos
 */
public class SimpleInvertibleMultimap<K, V> implements InvertibleMultimap<K, V> {

	private final Multimap<K, V> map = HashMultimap.create();
	private final Map<V, K> inverse = Maps.newHashMap();
	private Map<V, K> immutableInverse;

	private SimpleInvertibleMultimap() {

	}

	public static <K, V> SimpleInvertibleMultimap<K, V> create() {
		return new SimpleInvertibleMultimap<K, V>();
	}

	@Override
	@Nonnull
	public Map<V, K> inverse() {
		return immutableInverse == null ? immutableInverse = Collections.unmodifiableMap(inverse) : immutableInverse;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(@Nullable Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(@Nullable Object value) {
		return map.containsValue(value);
	}

	@Override
	public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
		return map.containsEntry(key, value);
	}

	@Override
	public boolean put(@Nullable K key, @Nullable V value) {
		return updateInverse(map.put(key, value));
	}

	@Override
	public boolean remove(@Nullable Object key, @Nullable Object value) {
		return updateInverse(map.remove(key, value));
	}

	@Override
	public boolean putAll(@Nullable K key, Iterable<? extends V> values) {
		return updateInverse(map.putAll(key, values));
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		return updateInverse(map.putAll(multimap));
	}

	@Override
	public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
		return updateInverse(map.replaceValues(key, values));
	}

	@Override
	public Collection<V> removeAll(@Nullable Object key) {
		return updateInverse(map.removeAll(key));
	}

	@Override
	public void clear() {
		map.clear();
		inverse.clear();
	}

	@Override
	public Collection<V> get(@Nullable K key) {
		return Collections.unmodifiableCollection(map.get(key));
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(map.keySet());
	}

	@Override
	public Multiset<K> keys() {
		return Multisets.unmodifiableMultiset(map.keys());
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(map.values());
	}

	@Override
	public Collection<Map.Entry<K, V>> entries() {
		return Collections.unmodifiableCollection(map.entries());
	}

	@Override
	public Map<K, Collection<V>> asMap() {
		return Collections.unmodifiableMap(map.asMap());
	}

	private <R> R updateInverse(R res) {
		inverse.clear();
		for(K key : map.keySet()) {
			for(V value : map.get(key)) {
				inverse.put(value, key);
			}
		}
		return res;
	}
}

package pl.asie.computronics.util.collect;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Horribly inefficient {@link InvertibleMultimap} backed by a {@link HashMultimap}.
 * Returned instances of {@link Collection} or {@link Map} are always immutable.
 * @author Vexatos
 */
public class SimpleInvertibleMultiMap<K, V> implements InvertibleMultimap<K, V> {

	private final Multimap<K, V> map = HashMultimap.create();
	private InvertibleMultimap.Inverse<V, K> inverse;

	private SimpleInvertibleMultiMap() {

	}

	public static <K, V> SimpleInvertibleMultiMap<K, V> create() {
		return new SimpleInvertibleMultiMap<K, V>();
	}

	@Override
	@Nonnull
	public InvertibleMultimap.Inverse<V, K> inverse() {
		return (inverse == null) ? inverse = updateInverse(new Inverse<V, K>(this)) : inverse;
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
		if(inverse != null) {
			inverse.clear();
		}
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
		if(inverse != null) {
			inverse.clear();
			for(Map.Entry<K, V> entry : map.entries()) {
				if(!inverse.containsKey(entry.getValue())) {
					inverse.put(entry.getValue(), entry.getKey());
				}
			}
		}
		return res;
	}

	private static final class Inverse<V, K> extends HashMap<V, K> implements InvertibleMultimap.Inverse<V, K> {

		private final InvertibleMultimap<K, V> original;

		private Inverse(InvertibleMultimap<K, V> original) {
			this.original = original;
		}

		private void updateInverse() {
			if(original != null) {
				original.clear();
				for(Map.Entry<V, K> entry : super.entrySet()) {
					if(!original.containsKey(entry.getValue())) {
						original.put(entry.getValue(), entry.getKey());
					}
				}
			}
		}

		private <R> R updateInverse(R res) {
			updateInverse();
			return res;
		}

		@Nonnull
		@Override
		public InvertibleMultimap<K, V> inverse() {
			return this.original;
		}

		@Override
		public K put(V key, K value) {
			return updateInverse(super.put(key, value));
		}

		@Override
		public K remove(Object key) {
			return updateInverse(super.remove(key));
		}

		@Override
		public void putAll(Map<? extends V, ? extends K> m) {
			super.putAll(m);
			updateInverse();
		}

		@Override
		public void clear() {
			super.clear();
			this.original.clear();
		}

		@Override
		public Set<V> keySet() {
			return Collections.unmodifiableSet(super.keySet());
		}

		@Override
		public Collection<K> values() {
			return Collections.unmodifiableCollection(super.values());
		}

		@Override
		public Set<Entry<V, K>> entrySet() {
			return Collections.unmodifiableSet(super.entrySet());
		}
	}
}

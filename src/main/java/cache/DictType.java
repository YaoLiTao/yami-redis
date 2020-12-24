package cache;

public interface DictType<K> {

    default long hashFunction(K key) {
        return 1L;
    }

}

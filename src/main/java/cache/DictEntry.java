package cache;

/**
 * 哈希节点表
 */
public class DictEntry<K, V> {
    K key;
    V value;
    DictEntry<K, V> next;
}

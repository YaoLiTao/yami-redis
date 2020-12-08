package cache;

/**
 * 哈希表
 */
public class DictHt<K, V> {
    DictEntry<K, V>[] table;
    long size;
    long sizeMask;
    long used;
}

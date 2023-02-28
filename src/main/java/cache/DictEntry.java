package cache;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 哈希节点表
 */
@NoArgsConstructor
@AllArgsConstructor
public class DictEntry<K, V> {

    public K key;
    public V value;
    public DictEntry<K, V> next;

}

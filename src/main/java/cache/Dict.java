package cache;

/**
 * 字典
 */
public class Dict<K, V> {
    DictHt<K, V>[] ht = new DictHt[2];
    DictType<K> type;
    int rehashIdx;

    Dict() {
        ht[0] = new DictHt<>();
        ht[1] = new DictHt<>();
    }

}

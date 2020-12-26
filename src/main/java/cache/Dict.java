package cache;

import java.util.Objects;

/**
 * 字典
 */
public class Dict<K, V> {

    private DictHt<K, V>[] ht = new DictHt[2];
    private DictType<K> type;
    private int rehashIdx;

    // 保证初始化后，插入第一个元素时，负载因子保持在1以下
    private static final int initHashTableSize = 8;

    public Dict() {
        this.ht[0] = new DictHt<>(initHashTableSize);
        this.ht[1] = new DictHt<>(initHashTableSize);
        this.type = new DictType<>();
        this.rehashIdx = -1;
    }

    public Dict<K, V> dictAdd(K key, V value) {
        // 现在正在rehash，先复制值
        if (rehashIdx > -1) {

        }
        return this;
    }

    private void dictAddRaw(DictHt<K, V> ht, DictEntry<K, V> dictEntry) {
        Objects.requireNonNull(dictEntry.key);
        int position = (int) (dictEntry.key.hashCode() & ht.sizeMask); // TODO 把hashCode重写为hashFunction
        if (Objects.isNull(ht.table[position])) {
            ht.table[position] = dictEntry;
        } else {

        }
    }

    public Dict<K, V> dictReplace(K key, V value) {
        return this;
    }

    public V dictFetchValue(K key) {
        return null;
    }

    public V getRandomKey() {
        return null;
    }

    public Dict<K, V> dictDelete(K key) {
        return this;
    }
}

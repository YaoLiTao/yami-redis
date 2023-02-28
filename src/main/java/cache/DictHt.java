package cache;

/**
 * 哈希表
 */
public class DictHt<K, V> {

    public DictEntry<K, V>[] table;
    public long sizeMask;
    public long used;

    public DictHt(int size) {
        this.table = new DictEntry[size];
        this.sizeMask = size - 1;
        this.used = 0;
    }

}

package cache;

import server.Server;

import java.util.Objects;

/**
 * 字典
 */
public class Dict<K, V> {

    private DictHt<K, V>[] ht;
    private DictType<K> type;
    private int rehashIdx;

    // 保证初始化后，插入第一个元素时，负载因子保持在1以下
    private static final int initHashTableSize = 8;

    public Dict() {
        this.ht = new DictHt[2];
        this.ht[0] = new DictHt<>(initHashTableSize);
        this.type = new DictType<>();
        this.rehashIdx = -1;
    }

    /**
     * 添加节点
     */
    public Dict<K, V> dictAdd(K key, V value) {
        DictEntry<K, V> dictEntry = new DictEntry<>();
        dictEntry.key = key;
        dictEntry.value = value;

        // 如果现在处于rehash状态，直接rehash
        if (rehashIdx > -1) {
            rehash();
            dictAddRaw(ht[1], dictEntry);
        } else if ((double) ((ht[0].used + 1) / ht[0].table.length) > 1 && !Server.isInBgSaveOrBgRewriteAOFCmd()
                || (double) ((ht[0].used + 1) / ht[0].table.length) > 5 && Server.isInBgSaveOrBgRewriteAOFCmd()) {
            extendDictHt();
            rehash();
            dictAddRaw(ht[1], dictEntry);
        } else {
            dictAddRaw(ht[0], dictEntry);
        }
        return this;
    }

    private void dictAddRaw(DictHt<K, V> ht, DictEntry<K, V> dictEntry) {
        Objects.requireNonNull(dictEntry.key);

        // TODO 计算位置，把hashCode重写为hashFunction
        int position = (int) (dictEntry.key.hashCode() & ht.sizeMask);
        DictEntry<K, V> entry = ht.table[position];

        // 原来没有节点
        if (Objects.isNull(entry)) {
            ht.table[position] = dictEntry;
            ht.used++;
            return;
        }

        // 直到key相同或者直到末尾
        while (true) {
            if (entry.key.equals(dictEntry.key)) {
                entry.value = dictEntry.value;
                break;
            }
            if (Objects.isNull(entry.next)) {
                entry.next = dictEntry;
                break;
            }
            entry = entry.next;
        }
        ht.used++;
    }

    /**
     * 把ht[0]的节点复制到ht[1]
     */
    private void rehash() {
        DictEntry<K, V> dictEntry;

        // 跳过hash表中的空位置
        do {
            // 超过界限，rehash完成，h[0]变为h[0]，ht[1]置为空
            if (rehashIdx >= ht[0].table.length) {
                rehashIdx = -1;
                ht[0] = ht[1];
                ht[1] = null;
                return;
            }

            // 记录当前需要rehash的节点，把原引用置为空
            dictEntry = ht[0].table[rehashIdx];
            ht[0].table[rehashIdx] = null;
            rehashIdx++;
        } while (Objects.isNull(dictEntry));

        // 移动当前位置所有的节点到ht[1]
        do {
            dictAddRaw(ht[1], dictEntry);
            dictEntry = dictEntry.next;
        } while (Objects.nonNull(dictEntry));
    }

    /**
     * 创建容量一个大于等于两倍ht[0].used且为2^n的DictHt[1]
     */
    private void extendDictHt() {
        long used = ht[0].used * 2;
        int shift = 0;
        while (used >>> (++shift) != 0) ;
        int size = 1 << shift;
        ht[1] = new DictHt<>(size);
        rehashIdx = 0; // 开启rehash
    }

    /**
     * 创建容量一个小于ht[0].used且为2^n的DictHt[1]
     */
    private void reduceDictHt() {
        long used = ht[0].used;
        int shift = 0;
        while (used >>> (++shift) != 0) ;
        int size = 1 << (shift - 1);
        ht[1] = new DictHt<>(size);
        rehashIdx = 0; // 开启rehash
    }

    public Dict<K, V> dictReplace(K key, V value) {
        return dictAdd(key, value);
    }

    public V dictFetchValue(K key) {
        return null;
    }

    public V getRandomKey() {
        return null;
    }

    public Dict<K, V> dictDelete(K key) {
        // 删除的时候小于8就不再缩小rehash
        return this;
    }
}

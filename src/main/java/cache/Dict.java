package cache;

import server.Server;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * 字典
 */
public class Dict<K, V> {

    private DictHt<K, V>[] ht;
    private DictType<K> type;
    private int rehashIdx;
    private final SecureRandom random = new SecureRandom();

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

        if (rehashIdx > -1) {
            // rehash状态需要删除原来在ht[0]已有的相同值
            dictDeleteRow(ht[0], key); // todo 防止ht[0]还有一个相同的节点，需要优化
            dictAddRaw(ht[1], dictEntry);
        } else {
            // 常规添加节点
            dictAddRaw(ht[0], dictEntry);
        }

        if (rehashIdx > -1) {
            // 处于rehash状态，直接rehash。
            rehash();
        } else if ((double) (ht[0].used / ht[0].table.length) > 1 && !Server.isInBgSaveOrBgRewriteAOFCmd()
                || (double) (ht[0].used / ht[0].table.length) > 5 && Server.isInBgSaveOrBgRewriteAOFCmd()) {
            // 不在rehash状态，满足两个负载因子条件之一，创建ht[1]，开始扩展rehash
            extendDictHt();
            rehash();
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
                ht.used++;
                break;
            }
            entry = entry.next;
        }
    }

    /**
     * 把ht[0]的节点复制到ht[1]
     */
    private void rehash() {
        DictEntry<K, V> dictEntry;

        // 跳过hash表中的空位置
        do {
            // 超过边界，rehash完成，h[0]变为h[0]，ht[1]置为空
            if (rehashIdx >= ht[0].table.length) {
                ht[0] = ht[1];
                ht[1] = null;
                rehashIdx = -1;
                return;
            }

            // 记录当前需要rehash的节点，把原引用置为空
            dictEntry = ht[0].table[rehashIdx];
            ht[0].table[rehashIdx] = null;
            rehashIdx++;
        } while (Objects.isNull(dictEntry));

        // 移动当前位置所有的节点到ht[1]
        do {
            ht[0].used--;
            dictAddRaw(ht[1], dictEntry);
            dictEntry = dictEntry.next;
        } while (Objects.nonNull(dictEntry));
    }

    /**
     * 拓展hashtable，创建容量一个大于等于两倍ht[0].used且为2^n的DictHt[1]
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
     * 缩小hashtable，创建容量一个小于ht[0].used且为2^n的DictHt[1]
     */
    private void reduceDictHt() {
        long used = ht[0].used;
        int shift = 0;
        while (used >>> (++shift) != 0) ;
        int size = 1 << (shift - 1); // TODO BUG?
        ht[1] = new DictHt<>(size);
        rehashIdx = 0; // 开启rehash
    }

    /**
     * @see Dict#dictAdd(java.lang.Object, java.lang.Object)
     */
    public Dict<K, V> dictReplace(K key, V value) {
        return dictAdd(key, value);
    }

    public V dictFetchValue(K key) {
        // 处于rehash状态，直接rehash。
        if (rehashIdx > -1) {
            rehash();
        }

        // rehash状态先从ht[1]找，找不到再从ht[0]找
        if (rehashIdx > -1) {
            V v = dictFetchValueRaw(ht[1], key);
            return Objects.nonNull(v) ? v : dictFetchValueRaw(ht[0], key);
        }
        return dictFetchValueRaw(ht[0], key);
    }

    private V dictFetchValueRaw(DictHt<K, V> ht, K key) {
        int position = (int) (key.hashCode() & ht.sizeMask);
        DictEntry<K, V> dictEntry = ht.table[position];
        while (Objects.nonNull(dictEntry) && !dictEntry.key.equals(key)) {
            dictEntry = dictEntry.next;
        }
        return Objects.isNull(dictEntry) ? null : dictEntry.value;
    }

    public V getRandomKey() {
        // todo
        return null;
    }

    public Dict<K, V> dictDelete(K key) {
        dictDeleteRow(ht[0], key);
        if (rehashIdx > -1) {
            dictDeleteRow(ht[1], key);
        }

        if (rehashIdx > -1) {
            // 处于rehash状态，直接rehash。
            rehash();
        } else if ((double) ((ht[0].used) / ht[0].table.length) < 0.1) {
            // 不在rehash状态，满足负载因子条件，创建ht[1]，开始收缩rehash
            reduceDictHt();
            rehash();
        }
        return this;
    }

    private Dict<K, V> dictDeleteRow(DictHt<K, V> ht, K key) {
        return this;
    }
}

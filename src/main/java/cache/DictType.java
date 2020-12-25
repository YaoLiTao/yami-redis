package cache;

import com.google.common.hash.Hashing;
import io.netty.util.HashingStrategy;

import java.nio.charset.StandardCharsets;

public class DictType<K> {

    private static final int c1 = 0xcc9e2d51;
    private static final int c2 = 0x1b873593;
    private static final int r1 = 15;
    private static final int r2 = 13;
    private static final int m = 5;
    private static final int n = 0xe6546b64;

    // MurmurHash_32
    public int hashFunction(byte[] key, int seed) {
        int hash = seed;
        int len = key.length;

        int remainingBytesCount = len % 4;
        int fourByteChunkCount = len - remainingBytesCount;

        // 整倍数阶段
        int i = 0;
        for (; i < fourByteChunkCount; i += 4) {
            // 小端序把每4字节转为int
            int k = 0;
            k |= key[i + 3] & 0xFF;
            k <<= 8;
            k |= key[i + 2] & 0xFF;
            k <<= 8;
            k |= key[i + 1] & 0xFF;
            k <<= 8;
            k |= key[i] & 0xFF;

            // 混淆
            k *= c1;
            k = (k << r1) | (k >>> (32 - r1));
            k *= c2;

            // 整倍数阶段计算
            hash ^= k;
            hash = (hash << r2) | (hash >>> (32 - r2));
            hash = hash * m + n;
        }

        // 余数阶段
        int r = 0;
        if (remainingBytesCount > 0) {
            for (int j = 0; j < remainingBytesCount; j++) {
                r <<= 8;
                r |= key[i + j] & 0xFF;
            }
            r *= c1;
            r = (r << r1) | (r >>> (32 - r1));
            r *= c2;

            hash ^= r;
        }

        hash ^= len;

        hash ^= (hash >>> 16);
        hash *= 0x85ebca6b;
        hash ^= (hash >>> 13);
        hash *= 0xc2b2ae35;
        hash ^= (hash >>> 16);
        return hash;
    }

}

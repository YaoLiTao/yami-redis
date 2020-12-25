package cache;

import com.google.common.hash.Hashing;
import io.netty.util.HashingStrategy;

import java.nio.charset.StandardCharsets;

public class DictType<K> {

    private static final int c1 = 0xcc9e2d51;
    private static final int c2 = 0x1b873593;
    private static final int r1 = 15;
    private static final int r2 = 15;
    private static final int m = 5;
    private static final int n = 0xe6546b64;

    // MurmurHash_32
    public int hashFunction(byte[] key, long seed) {
        long hash = seed;
        int len = key.length;

        int remainingBytesCount = len % 4;
        int fourByteChunkCount = len - remainingBytesCount;
        long k;
        int i = 0;
        for (; i < fourByteChunkCount; i += 4) {
            k = key[i];
            k <<= 8;
            k |= key[i + 1];
            k <<= 8;
            k |= key[i + 2];
            k <<= 8;
            k |= key[i + 3];

            k *= c1;
            k = (k << r1) | (k >> (32 - r2));
            k *= c2;

            hash ^= k;
            hash = (hash << r2) | (hash >> (32 - r2));
            hash = hash * m + n;
        }

        long remainingBytes = 0;
        for (int j = remainingBytesCount - 1; j >= 0; j--) {
            remainingBytes = key[i + j];
            remainingBytes <<= 8;
        }
        remainingBytes *= c1;
        remainingBytes = (remainingBytes << r1) | (remainingBytes >> (32 - r1));
        remainingBytes *= c2;

        hash ^= remainingBytes;
        hash ^= len;
        hash ^= (hash >> 16);
        hash *= 0x85ebca6b;
        hash ^= (hash >> 13);
        hash *= 0xc2b2ae35;
        hash ^= (hash >> 16);

        return (int) hash;
    }

    public static void main(String[] args) {
        byte[] bytes = "ABCDE".getBytes(StandardCharsets.US_ASCII);
        System.out.println(new DictType<>().hashFunction(bytes, 0));
        System.out.println(Hashing.murmur3_32().hashBytes(bytes).asInt());
    }

}

package cache;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DictTypeTest {

    @Test
    void hashFunction() {
        DictType<Object> objectDictType = new DictType<>();
        Random random = new Random();
        int size = 1 << 24;
        byte[] bytes = new byte[32];
        byte[] feq = new byte[size];

        HashMap<Integer, Integer> map = Maps.newHashMap();
        for (int i = 0; i < size; i++) {
            random.nextBytes(bytes);
            int i1 = objectDictType.hashFunction(bytes);
            int i2 = Hashing.murmur3_32().hashBytes(bytes).asInt();
            Integer value = map.getOrDefault(i1, 0);
            map.put(i1, value + 1);
            if (i1 != i2) {
                System.err.println("i1 != i2");
            }
            int pos = i1 & (size - 1);
            feq[pos] = (byte) (feq[pos] + 1);
        }
        Arrays.sort(feq);
        System.out.println(feq[size - 1]);
        int con = 0;
        for (int i = 0; i < size; i++) {
            if (feq[i] > 1) {
                con++;
            }
        }
        double per = (double) con / size;
        System.out.println(per);

        long count = map.values().stream().filter(item -> item > 1).count();
        per = (double) count / size;
        System.out.println(per);
    }

}

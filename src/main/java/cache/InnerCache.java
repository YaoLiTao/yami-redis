package cache;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class InnerCache {
    public static final ConcurrentHashMap<ByteBuf, ByteBuf> hash = new ConcurrentHashMap();
    public static final ArrayList<ByteBuf> list = new ArrayList<>();
    public static final TreeSet<ByteBuf> set = new TreeSet<>();
}

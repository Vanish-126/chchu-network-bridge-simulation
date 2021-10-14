package utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 自定义HashMap类，并重写toString方法
 * @param <K>
 * @param <V>
 */
public class MyHashMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = -5894887960346129860L;

    /**
     * 重写HashMap类的toString()方法
     * @return
     */
    @Override
    public String toString() {
        Set<Entry<K, V>> keyset = this.entrySet();
        Iterator<Entry<K, V>> i = keyset.iterator();
        if (!i.hasNext()) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        // buffer.append("{");//注意此程序与源代码的区别
        for (;;) {
            Entry<K, V> me = i.next();
            K key = me.getKey();
            V value = me.getValue();
            buffer.append(key.toString() + "\t");
            buffer.append(value.toString() + "\n");
            if (!i.hasNext()) {
                return buffer.toString();
            }
        }
    }
}

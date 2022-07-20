package io.github.litemq.client.producer.partitioner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round-Robin partitioner
 *
 * @author calvinit
 * @since 0.0.1
 */
public class RoundRobinPartitioner implements Partitioner {

    /**
     * topic's current partition index
     */
    private static final Map<String, AtomicInteger> currParIdxes = new ConcurrentHashMap<>();

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, int total) {
        return currParIdxes.compute(topic, (__, index) -> {
            if (index == null) {
                index = new AtomicInteger(0);
            } else if (index.incrementAndGet() >= total) {
                index.set(0);
            }
            return index;
        }).get();
    }
}

package io.github.litemq.client.producer.partitioner;

import java.util.Random;

/**
 * Random partitioner
 *
 * @author calvinit
 * @since 0.0.1
 */
public class RandomPartitioner implements Partitioner {

    private static final Random random = new Random(System.currentTimeMillis());

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, int total) {
        return random.nextInt(total);
    }
}

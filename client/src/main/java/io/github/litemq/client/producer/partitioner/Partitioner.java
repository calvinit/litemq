package io.github.litemq.client.producer.partitioner;

/**
 * MQ 分区
 *
 * @author calvinit
 * @since 0.0.1
 */
public interface Partitioner {
    int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, int total);
}

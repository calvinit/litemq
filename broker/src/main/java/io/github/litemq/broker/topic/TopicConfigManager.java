package io.github.litemq.broker.topic;

import io.github.litemq.common.config.TopicConfig;
import io.github.litemq.common.util.FileUtils;
import io.github.litemq.common.util.JSONUtils;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Topic 配置管理器
 *
 * @author calvinit
 * @since 0.0.1
 */
public class TopicConfigManager {

    private static final long LOCK_TIMEOUT_MILLIS = 3000;

    private transient final Lock topicConfigRegistryLock = new ReentrantLock();

    private final ConcurrentMap<String, TopicConfig> topicConfigRegistry = new ConcurrentHashMap<>(1024);

    public void createTopicIfAbsent(TopicConfig topicConfig) {
        if (topicConfig == null) {
            throw new IllegalArgumentException("TopicConfig");
        }
        String topicName = topicConfig.getTopicName();
        if (topicName == null || topicName.isBlank()) {
            throw new IllegalArgumentException("TopicName");
        }
        int partionNum = topicConfig.getPartionNum();
        if (partionNum < 1) {
            throw new IllegalArgumentException("PartionNumber");
        }

        try {
            if (topicConfigRegistryLock.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    String topicDirPath = "topic";
                    if (!topicDirPath.endsWith(File.separator)) {
                        topicDirPath += File.separator;
                    }

                    if (!FileUtils.checkExists(topicDirPath)) {
                        FileUtils.createDirs(topicDirPath);
                    }

                    if (!topicConfigRegistry.containsKey(topicName)) {
                        if (FileUtils.writeFile(topicDirPath + topicName + ".txt",
                                Collections.singletonList(JSONUtils.toJSONString(topicConfig)))) {
                            topicConfigRegistry.put(topicName, topicConfig);
                        }
                    }
                } finally {
                    topicConfigRegistryLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void deleteTopicIfPresent(String topicName) {
        topicConfigRegistry.remove(topicName);

        String topicDirPath = "topic";
        if (!topicDirPath.endsWith(File.separator)) {
            topicDirPath += File.separator;
        }
        FileUtils.deleteFile(topicDirPath + topicName + ".txt");
    }
}

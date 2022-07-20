package io.github.litemq.common.config;

/**
 * Topic 配置
 *
 * @author calvinit
 * @since 0.0.1
 */
public class TopicConfig {
    private String topicName;
    private int partionNum;

    public TopicConfig() {}

    public TopicConfig(String topicName) {
        this.topicName = topicName;
    }

    public TopicConfig(String topicName, int partionNum) {
        this.topicName = topicName;
        this.partionNum = partionNum;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getPartionNum() {
        return partionNum;
    }

    public void setPartionNum(int partionNum) {
        this.partionNum = partionNum;
    }
}

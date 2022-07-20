package io.github.litemq.client.admin;

/**
 * MQ 管理者
 *
 * @author calvinit
 * @since 0.0.1
 */
public interface Admin {
    void createTopic(String name, int partionNum);
}

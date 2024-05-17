package com.gci.schedule.driverless.service.server;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisService {
    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 对于 Redis 集群则无法使用
     * 支持重复，线程安全
     *
     * @param lockKey  加锁键
     * @param clientId 加锁客户端唯一标识(采用UUID)
     * @param seconds  锁过期时间
     * @return
     */
    Boolean tryLock(String lockKey, String clientId, long seconds);

    /**
     * 与 tryLock 相对应，用作释放锁
     *
     * @param lockKey
     * @param clientId
     * @return
     */
    Boolean releaseLock(String lockKey, String clientId);

    Boolean expire(String key, Long timeout, TimeUnit timeUnit);

    Boolean persist(String key);

    Long getExpire(String key, TimeUnit timeUnit);

    Long opsForListLeftPush(String key, String value);

    List opsForListRange(String key, long l, long l1);

    Long opsForListRemove(String key, long l, String name);

    Boolean delete(Object key);

    Boolean hasKey(String key);

    Set<String> keys(String pattern);

    Boolean opsForHashContainsName(String key, String name);

    Boolean opsForHashPut(String key, String hk, String hv);

    Boolean opsForHashPutAll(String key, Map<String, String> map);

    Boolean opsForHashDelete(String key, String hk);

    String opsForHashGet(String key, String name);

    Map<String, String> opsForHashEntries(String key);

    Boolean opsForHashHasKey(String key, String hashKey);

    void opsForValueSet(String key, String value);

    String opsForValueGet(String key);

    void convertAndSend(String channel, String message);
}

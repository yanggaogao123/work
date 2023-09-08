package com.gci.schedule.driverless.service.server.impl;

import com.gci.schedule.driverless.service.server.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "EX";
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Boolean tryLock(String lockKey, String clientId, long seconds) {
        Boolean lock = (Boolean) redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            String result = jedis.set(lockKey, clientId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, seconds);
            if (LOCK_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        });
        log.warn("[REDISSERVICE] tryLock, lockKey:{}, clientId:{}, seconds:{}, lock:{}", lockKey, clientId, seconds, lock);
        return lock;
    }

    @Override
    public Boolean releaseLock(String lockKey, String clientId) {
        Boolean releaseResult = (Boolean) redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Jedis jedis = (Jedis) redisConnection.getNativeConnection();
            Object result = jedis.eval(RELEASE_LOCK_SCRIPT, Collections.singletonList(lockKey),
                    Collections.singletonList(clientId));
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
            return false;
        });
        log.warn("[REDISSERVICE] releaseLock, lockKey:{}, clientId:{}, releaseResult:{}", lockKey, clientId, releaseResult);
        return releaseResult;
    }

    @Override
    public Boolean expire(String key, Long timeout, TimeUnit timeUnit) {
        Boolean result = redisTemplate.expire(key, timeout, timeUnit);
        log.warn("[REDISSERVICE] expire success key:{}, timeout:{}, result:{}", key, timeout, result);
        return result;
    }

    @Override
    public Boolean persist(String key) {
        Boolean result = redisTemplate.persist(key);
        log.warn("[REDISSERVICE] persist success key:{}, result:{}", key, result);
        return result;
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    @Override
    public Long opsForListLeftPush(String key, String value) {
        Long result = redisTemplate.opsForList().leftPush(key, value);
        log.warn("[REDISSERVICE] opsForListLeftPush success key:{} ,value:{} ,count:{}", key, value, result);
        return result;
    }

    @Override
    public List opsForListRange(String key, long l, long l1) {
        List result = redisTemplate.opsForList().range(key, l, l1);
        log.debug("[REDISSERVICE] opsForListRange result key:{} ,result:{}", key, result);
        return result;
    }

    @Override
    public Long opsForListRemove(String key, long l, String name) {
        Long num = redisTemplate.opsForList().remove(key, l, name);
        log.warn("[REDISSERVICE] opsForListRemove success key:{} ,name:{} ,count:{}", key, name, num);
        return num;
    }

    @Override
    public Boolean delete(Object key) {
        log.warn("[REDISSERVICE] delete key:{}", key);
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Set<String> keys(String pattern) {
        Set<String> keys = (Set<String>) redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(pattern).count(1000).build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });
        log.warn("[REDISSERVICE] keys pattern:{} {}", pattern, keys);
        return keys;
    }

    @Override
    public Boolean opsForHashContainsName(String key, String name) {
        if (!redisTemplate.opsForHash().hasKey(key, name)) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean opsForHashPut(String key, String hk, String hv) {
        redisTemplate.opsForHash().put(key, hk, hv);
        log.warn("[REDISSERVICE] opsForHashPut success key:{} ,hk:{} ,hv:{}", key, hk, hv);
        return true;
    }

    @Override
    public Boolean opsForHashPutAll(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
        return true;
    }

    @Override
    public Boolean opsForHashDelete(String key, String hk) {
        Long count = redisTemplate.opsForHash().delete(key, hk);
        log.warn("[REDISSERVICE] opsForHashDelete success key:{} ,hk:{} ,count:{}", key, hk, count);
        return count > 0;
    }

    @Override
    public String opsForHashGet(String key, String name) {
        return (String) redisTemplate.opsForHash().get(key, name);
    }

    @Override
    public Map<String, String> opsForHashEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean opsForHashHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    @Override
    public void opsForValueSet(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String opsForValueGet(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void convertAndSend(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }
}

package com.yupi.springbootinit.utils;
//
//import cn.hutool.core.util.BooleanUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Function;
//
//
//@Component
//@Slf4j
public class CacheClient {
//    private StringRedisTemplate stringRedisTemplate;
//
//    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
//
//    public CacheClient(StringRedisTemplate stringRedisTemplate) {
//        this.stringRedisTemplate = stringRedisTemplate;
//    }
//
//    public void set(String key, Object value, Long time, TimeUnit unit) {
//        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
//    }
//
//    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
//        // 设置逻辑过期
//        RedisData redisData = new RedisData();
//        redisData.setData(value);
//        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
//
//        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData), time, unit);
//    }
//
//
//    /**
//     * 缓存穿透解决方案
//     * Function<ID, R>的第一个泛型是参数类型，第二个是返回值类型
//     *
//     * @param keyPrefix
//     * @param id
//     * @param returnType
//     * @param dbFallback
//     * @param time
//     * @param unit
//     * @param <R>
//     * @param <ID>
//     * @return
//     */
//    public <R, ID> R queryWithPassThrough(
//            String keyPrefix,
//            ID id,
//            Class<R> returnType,
//            Function<ID, R> dbFallback,
//            Long time, TimeUnit unit
//    ) {
//        String key = keyPrefix + id;
//        // todo 从redis查询商户缓存
//        String json = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
//        // todo 判断redis中是否存在对应数据
//        if (StrUtil.isNotBlank(json)) {
//            // null,"","\t\n"都判断为空
//            // todo 存在，直接返回
//            return JSONUtil.toBean(json, returnType);
//        }
//        // ------------缓存穿透解决方案-----------
//        // 若命中的是空字符串
//        if (json != null) {
//            // 命中空字符串
//            return null;
//        }
//        // ------------缓存穿透解决方案-----------
//
//        // todo 不存在，根据商户id查询数据库
//        R r = dbFallback.apply(id);
//
//        // TODO 数据库中不存在，穿透逻辑
//        if (r == null) {
//            // ------------缓存穿透解决方案-----------
//            // 将空值写入 redis，但是有效期更短
//            stringRedisTemplate.opsForValue().set(
//                    CACHE_SHOP_KEY + id, "",
//                    CACHE_NULL_TTL, TimeUnit.MINUTES);
//            // ------------缓存穿透解决方案-----------
//
//            return null;
//        }
//
//        // todo 数据库中存在，写入redis
//        this.setWithLogicalExpire(key, r, time, unit);
//
//        // todo 返回数据
//        return r;
//    }
//
//
//    public <ID, R> R queryWithLogicExpire(
//            String keyPrefix,
//            ID id,
//            Class<R> returnType,
//            Function<ID, R> dbFallback,
//            Long time, TimeUnit unit
//    ) {
//        String key = keyPrefix + id;
//        // todo 从redis查询商户缓存
//        String json = stringRedisTemplate.opsForValue().get(key);
//        // todo 判断redis中是否存在对应数据
//        if (StrUtil.isBlank(json)) {
//            // null,"","\t\n"都判断为空
//            // todo 不存在，直接返回
//            return null;
//        }
//
//
//        // todo 存在，json 反序列化并判断是否过期
//        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
//        // redisData 的 data 数据是 Object类型，得到的实际是 JSONObject 类型，便于数据转换
//        R r = JSONUtil.toBean((JSONObject) redisData.getData(), returnType);
//        if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
//            // todo 未过期，直接返回数据
//            return r;
//
//        }
//        // todo A 已过期，缓存重建
//        // todo A.1 获取并判断互斥锁
//        String lockKey = stringRedisTemplate.opsForValue().get(LOCK_SHOP_KEY + id);
//        boolean getLock = tryLock(lockKey);
//        if (getLock) {
//            // todo A.2 成功获取锁，开启独立线程，重建缓存
//            // double check缓存是否过期
//            // 因为在从判断是否过期到获取锁之间，可能有线程已经重建好内存
//            redisData = JSONUtil.toBean(json, RedisData.class);
//            // redisData 的 data 数据是 Object类型，得到的实际是 JSONObject 类型，便于数据转换
//            r = JSONUtil.toBean((JSONObject) redisData.getData(), returnType);
//            if (redisData.getExpireTime().isAfter(LocalDateTime.now())) {
//                // todo 未过期，直接返回数据
//                return r;
//            }
//
//            CACHE_REBUILD_EXECUTOR.submit(() -> {
//                try {
//                    R r1 = dbFallback.apply(id);
//                    this.saveDate2Redis(key, time, unit, r1);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    unLock(LOCK_SHOP_KEY + id);
//                }
//            });
//        }
//        // todo 返回数据，可为过期数据，也可为缓存重建好的未过期数据
//        return r;
//    }
//
//    private boolean tryLock(String key) {
//        Boolean absent = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
//
//        // 这里之所以用工具类，是因为 absent 是一个包装类，方法返回一个基本类型
//        // 因此会自动拆箱，这个过程有可能出现空指针
//        return BooleanUtil.isTrue(absent);
//    }
//
//    private void unLock(String key) {
//        stringRedisTemplate.delete(key);
//    }
//
//    public <R> void saveDate2Redis(String key, long time, TimeUnit unit, R data) {
//        // 封装逻辑过期
//        RedisData redisData = new RedisData();
//        redisData.setData(data);
//        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
//        stringRedisTemplate
//                .opsForValue()
//                .set(key, JSONUtil.toJsonStr(redisData));
//        System.out.println("----------成功");
//    }
}

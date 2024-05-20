package com.yupi.springbootinit.service.impl;

import com.yupi.springbootinit.constant.BSEConstant;
import com.yupi.springbootinit.constant.RedisConstant;
import com.yupi.springbootinit.model.dto.bse.BSEAlgorithmParameterRequest;
import com.yupi.springbootinit.model.py4jInterface.BSEClient;
import com.yupi.springbootinit.service.BSEService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import py4j.GatewayServer;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


@Service
public class BSEServiceImpl implements BSEService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

//    @Resource
//    GatewayServer gatewayServer;

    public void test() {
        GatewayServer gatewayServer = new GatewayServer();
        gatewayServer.start();
        BSEClient entryPoint = (BSEClient) gatewayServer.
                getPythonServerEntryPoint(new Class[]{BSEClient.class});
        try {
            entryPoint.helloBSE();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gatewayServer.shutdown();
    }

    public String transferAlgorithmParameter(BSEAlgorithmParameterRequest bseAlgorithmParameterRequest) {

        // Asynchronously wait for the key to disappear, that is, the bse operation ends
//        CompletableFuture<Void> future = waitForKeyDisappearance(RedisConstant.BSE1_IS_RUNNING);


        // validate the parameter
        validateParameter(bseAlgorithmParameterRequest);

        AtomicReference<String> callStateString = new AtomicReference<>("");

        // interact with python BSE
        GatewayServer gatewayServer = new GatewayServer();
        GatewayServer.GatewayServerBuilder gatewayServerBuilder = new GatewayServer.GatewayServerBuilder();
//            gatewayServerBuilder.authToken(Py4jConstant.AUTH_TOKEN);
        gatewayServer.start();
        BSEClient entryPoint = (BSEClient) gatewayServer.
                getPythonServerEntryPoint(new Class[]{BSEClient.class});
//            new FutureTask<>()
        try {
            String message = entryPoint.transferAlgorithmParameter(bseAlgorithmParameterRequest);
            callStateString.set(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        gatewayServer.shutdown();


        // csv filename
        return callStateString.get();
    }

    public void validateParameter(BSEAlgorithmParameterRequest bseAlgorithmParameterRequest) {
        // default value
        // start_time 0
        // end_time 600
        // seller_range (80, 310)
        // buyer_range (250, 490)
        // time_mode periodic
        // step_mode fixed
        // order_interval 30
        if (bseAlgorithmParameterRequest.getStartTime() < 0)
            bseAlgorithmParameterRequest.setStartTime(BSEConstant.ALGORITHM_START_TIME);

        if (bseAlgorithmParameterRequest.getEndTime() <= 0) {
            int startTime = bseAlgorithmParameterRequest.getStartTime();
            bseAlgorithmParameterRequest.setEndTime(startTime < BSEConstant.ALGORITHM_END_TIME ?
                    BSEConstant.ALGORITHM_END_TIME : startTime + 100);
        }

        if (bseAlgorithmParameterRequest.getSellerRangeFrom() < 0)
            bseAlgorithmParameterRequest.setSellerRangeFrom(BSEConstant.SELLER_RANGE_FROM);

        if (bseAlgorithmParameterRequest.getSellerRangeTo() <= 0) {
            int sellerRangeFrom = bseAlgorithmParameterRequest.getSellerRangeFrom();
            bseAlgorithmParameterRequest.setSellerRangeTo(sellerRangeFrom < BSEConstant.SELLER_RANGE_TO ?
                    BSEConstant.SELLER_RANGE_TO : sellerRangeFrom + 100);
        }

        if (bseAlgorithmParameterRequest.getBuyerRangeFrom() < 0)
            bseAlgorithmParameterRequest.setBuyerRangeFrom(BSEConstant.BUYER_RANGE_FROM);

        if (bseAlgorithmParameterRequest.getBuyerRangeTo() <= 0) {
            int buyerRangeFrom = bseAlgorithmParameterRequest.getBuyerRangeFrom();
            bseAlgorithmParameterRequest.setBuyerRangeTo(buyerRangeFrom < BSEConstant.BUYER_RANGE_TO ?
                    BSEConstant.BUYER_RANGE_TO : buyerRangeFrom + 100);
        }

        if (bseAlgorithmParameterRequest.getOrderInterval() <= 0)
            bseAlgorithmParameterRequest.setOrderInterval(BSEConstant.ORDER_INTERVAL);

        if (bseAlgorithmParameterRequest.getStepMode() == null || "".equals(bseAlgorithmParameterRequest.getStepMode()))
            bseAlgorithmParameterRequest.setStepMode(BSEConstant.STEP_MODE);
        if (bseAlgorithmParameterRequest.getTimeMode() == null || "".equals(bseAlgorithmParameterRequest.getTimeMode()))
            bseAlgorithmParameterRequest.setTimeMode(BSEConstant.TIME_MODE);

        // checking the sellers_spec and buyers_spec may conduct at front-end
    }

    public CompletableFuture<Void> waitForKeyDisappearance(String key) {
        return CompletableFuture.runAsync(() -> {
            while (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
                try {
                    // 每隔1秒检查一次
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting for Redis key to disappear", e);
                }
            }
        });
    }
}

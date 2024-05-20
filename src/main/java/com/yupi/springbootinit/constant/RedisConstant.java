package com.yupi.springbootinit.constant;

public interface RedisConstant {

    /**
     * if BSE on port 25333 is running
     */
    String BSE1_IS_RUNNING = "lock:bse1";

    /**
     * if BSE on port 25334 is running
     */
    String BSE2_IS_RUNNING = "lock:bse2";

    /**
     * if BSE on port 25335 is running
     */
    String BSE3_IS_RUNNING = "lock:bse3";

}

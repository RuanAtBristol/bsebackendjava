package com.yupi.springbootinit.constant;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BSEConstant {

    /**
     * algorithm default start_time
     */
    int ALGORITHM_START_TIME = 0;

    /**
     * algorithm default end_time
     */
    int ALGORITHM_END_TIME = 600;

    /**
     * algorithm default seller_range_from
     */
    int SELLER_RANGE_FROM = 80;

    /**
     * algorithm default seller_range_to
     */
    int SELLER_RANGE_TO = 310;

    /**
     * algorithm default buyer_range_from
     */
    int BUYER_RANGE_FROM = 250;

    /**
     * algorithm default buyer_range_to
     */
    int BUYER_RANGE_TO = 490;

    /**
     * algorithm default order_interval
     */
    int ORDER_INTERVAL = 30;

    /**
     * algorithm default step_mode
     */
    String STEP_MODE = "fixed";

    /**
     * algorithm default time_mode
     */
    String TIME_MODE = "periodic";

    /**
     * default algorithm name
     */
    List<String> algorithmNames = Stream.of(
            "ZIC", "SHVR", "ZIP", "PRDE", "PRSH", "ZIPSH", "GVWY", "PRZI", "SNPR") // may need ZIPDE
            .collect(Collectors.toList());

    /**
     * BSE dump filename
     */
    String BSE_FILENAME = "market_session_experiment_result.csv";

    /**
     * BSE port
     */
    int[] BSE_PORTS = {25333, 25334, 25335};

    /**
     * BSE avg_balance.csv filepath
     */
    String BSE_AVG_BALANCE_CSV_FILEPATH_PREFIX = "/Users/yhj19/developer/MY_Python/pythonProject/controller/";
}

package com.yupi.springbootinit.model.py4jInterface;


import com.yupi.springbootinit.model.dto.bse.BSEAlgorithmParameterRequest;

public interface BSEClient {
    void helloBSE();

    String transferAlgorithmParameter(BSEAlgorithmParameterRequest bseAlgorithmParameterRequest);

}

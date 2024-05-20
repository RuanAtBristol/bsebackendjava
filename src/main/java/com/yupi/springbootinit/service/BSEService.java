package com.yupi.springbootinit.service;


import com.yupi.springbootinit.model.dto.bse.BSEAlgorithmParameterRequest;

public interface BSEService {
    String transferAlgorithmParameter(BSEAlgorithmParameterRequest bseAlgorithmParameterRequest);
}

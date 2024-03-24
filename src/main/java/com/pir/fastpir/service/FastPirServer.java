package com.pir.fastpir.service;


import com.alibaba.fastjson2.JSONObject;
import com.pir.fastpir.common.AjaxResult;

import java.math.BigInteger;
import java.util.List;

public interface FastPirServer {
    void init(int maxNumber);
    JSONObject genPubKeys(int size, int keyLength);
    AjaxResult<List<BigInteger>> queryResult(String sessionId, String dataUrl, BigInteger px, List<String> queryIds);

}

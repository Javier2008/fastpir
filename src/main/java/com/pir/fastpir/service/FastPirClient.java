package com.pir.fastpir.service;

import com.pir.fastpir.common.AjaxResult;

public interface FastPirClient {
    /**
     *
     * @param id        id标识
     * @param fakeRate      混淆比
     * @param remoteHost  对端地址
     * @param chiperUrl  数据url
     * @param initToken  server的token
     * @param min  type为 int标识最小id； type为string标识id字符最小长度
     * @param max  type为 int标识最大id；type为string标识id字符最大长度
     * @param keyType  0, int, 1 32-md5, 2 string, 3 64-md5
     * @param keyFormat [0:{  };  *:{0-9,a-z,A-Z,_,'};  1 - 5 : {a - z};  3:{ }]
     */
    void init(Long id, int fakeRate, String remoteHost,  String chiperUrl, String initToken,
              Long min, Long max, Integer keyType, String keyFormat);

    AjaxResult<String> SinglePirExec2(Long id, String key);

    void close(Long id);
}

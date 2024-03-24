package com.pir.fastpir.controller;

import com.alibaba.fastjson2.JSONObject;
import com.pir.fastpir.common.AjaxResult;
import com.pir.fastpir.service.FastPirClient;
import com.pir.fastpir.service.FastPirServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Api(value = "PIR", tags = "隐私求交计算")
@RestController
@RequestMapping("/pir")
@Slf4j
public class PirTaskController {

    //private final PirTaskService pirTaskService;
    @Value("${pir.processTimeOut:1800000}")
    private long processTimeOut;


    @Autowired
    private FastPirServer fastPirServer;

    @Autowired
    private FastPirClient fastPirClient;

    /*public PirTaskController(PirTaskService pirTaskService) {
        this.pirTaskService = pirTaskService;
    }*/
    @ApiOperation("客户端查询接口")
    @PostMapping("/query")
    public AjaxResult<String> query(@RequestParam(value = "rate", required = false, defaultValue = "10") int rate,
        @RequestParam(value = "remote", defaultValue = "127.0.0.1:8080") String remoteHost,
        @RequestParam(value = "data", required = false,defaultValue = "data.csv") String dataUrl,
        @RequestParam(value = "searchId") String key) {
        Random r = new Random();
        long id = r.nextLong();

        if (StringUtils.isEmpty(remoteHost) || StringUtils.isEmpty(dataUrl) || StringUtils.isEmpty(key)){
            return AjaxResult.error("输入参数缺失");
        }

        if (rate <= 0){
            return AjaxResult.error("混淆比不能小于1");
        }


        fastPirClient.init(id, rate, remoteHost,
            dataUrl, "", 18L, 18L, 2, "[*:{0-9}]");

        return fastPirClient.SinglePirExec2(id, key);

    }


    @GetMapping("/getPubKey/{size}/{length}")
    public AjaxResult<JSONObject> initSession(@PathVariable("size") int size, @PathVariable("length") int length) {
        log.info("获取public key {} {}", size, length);
        JSONObject jsonObject = fastPirServer.genPubKeys(size, length);

        return AjaxResult.success(jsonObject);

    }

    @PostMapping("/query/{sessionId}")
    public AjaxResult<List<BigInteger>> queryResult(@PathVariable("sessionId") String uuid, @RequestBody JSONObject pxJson) {
        String datasetUrl = pxJson.get("dataset").toString();
        String px = pxJson.get("px").toString();
        BigInteger bigInteger = new BigInteger(px);
        List<String> idList = pxJson.getJSONArray("idList").toJavaList(String.class);
        log.info("请求执行OT操作: session {}， dataurl {} key {}, query size {}", uuid, datasetUrl, px, idList.size());
        return fastPirServer.queryResult(uuid, datasetUrl, bigInteger, idList);
    }

}

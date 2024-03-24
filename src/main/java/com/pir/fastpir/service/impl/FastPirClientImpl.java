package com.pir.fastpir.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.pir.fastpir.common.AjaxResult;
import com.pir.fastpir.common.CacheConstants;
import com.pir.fastpir.common.Constants;
import com.pir.fastpir.component.PirSessionCache;
import com.pir.fastpir.component.PirTaskInfo;
import com.pir.fastpir.component.PubKeyInfo;
import com.pir.fastpir.service.FastPirClient;
import com.pir.fastpir.service.PirQueryID;
import com.pir.fastpir.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.*;
@Slf4j
@Component
public class FastPirClientImpl implements FastPirClient {
    Random rand = new Random();

    @Autowired
    private PirSessionCache pirSessionCache;

    //@Autowired
    //private PrivpyBaseConfigure baseConfigure;
    @Value("${pir.ssl:false}")
    Boolean sslEnable;


    private final static  String prefix = "Pir";

    //随机数的长度要小于密钥的长度（不能等于）
    private final int keyLength = 500;

    //最大等待10分钟服务端响应
    private static final int MAXWAITTIME = 600 * 1000;

    /**
     *
     * @param id        id标识
     * @param rate      混淆比
     * @param remoteHost  对端地址
     * @param chiperUrl  数据url
     * @param initToken  server的token
     * @param min  type为 int标识最小id； type为string标识id字符最小长度
     * @param max  type为 int标识最大id；type为string标识id字符最大长度
     * @param keyType  0, int, 1 32-md5, 2 string, 3 64-md5
     * @param keyFormat [0:{  };  *:{0-9,a-z,A-Z,_,'};  1 - 5 : {a - z};  3:{ }]
     */
    @Override
    public void init(Long id, int rate, String remoteHost, String chiperUrl, String initToken,
                     Long min, Long max, Integer keyType, String keyFormat) {
        PirTaskInfo taskInfo = new PirTaskInfo();

        taskInfo.setId(id);
        if (rate == 0){
            taskInfo.setFakeRate(1);
        }else {
            taskInfo.setFakeRate(rate);
        }

        PirQueryID pirQueryID = new PirQueryIDDefault();
        pirQueryID.init(keyType == null ? 0 : keyType, min == null ? 0L : min, max == null ? 10000L : max, keyFormat);
        taskInfo.setPirQueryIDFactory(pirQueryID);

        String host = remoteHost.trim();
        if (!host.endsWith("/")){
            host = host + "/";
        }

        taskInfo.setGetKeysUrl(host + "pir/getPubKey");
        taskInfo.setGetResultUrl(host + "pir/query");
        //初始化，获取token
        if (null == initToken) {
            taskInfo.setToken("");
        }else{
            taskInfo.setToken(initToken);
        }
        taskInfo.setChiperUrl(chiperUrl);
        List<PubKeyInfo> keyInfos = new ArrayList<>();
        taskInfo.setRemoteSession(getKeyInfo(taskInfo, keyInfos));
        taskInfo.setKeyInfos(keyInfos);

        pirSessionCache.saveTask(taskInfo);
        log.info("init pir success with taskid {}", taskInfo.getId());
    }

    public AjaxResult<String> SinglePirExec2(Long id, String key) {
        BigInteger x = new BigInteger(keyLength, rand);

        PirTaskInfo taskInfo = pirSessionCache.getTaskInfo(id);
        if (null == taskInfo){
            log.info("fast PIR 还未初始化");
            return AjaxResult.error("failed, 请先初始化任务");
        }
        PirQueryID pirQueryIDDefault = taskInfo.getPirQueryIDFactory();
        pirQueryIDDefault.generateFakeID(key, taskInfo.getFakeRate());

        List<String> idList = pirQueryIDDefault.getQueryIDs();
        int idIndex = pirQueryIDDefault.getRealIdIndex();

        //
        List<PubKeyInfo> keyInfos = taskInfo.getKeyInfos();
        if (keyInfos == null || keyInfos.size() <= idIndex){
            return AjaxResult.error( "failed, 获取密钥失败，请重置密钥");
        }
        PubKeyInfo keyInfo = taskInfo.getKeyInfos().get(idIndex);

        BigInteger px = calcRSA(x,
                new BigInteger(keyInfo.getPubE()),
                new BigInteger(keyInfo.getPubN()));

        return queryResult(taskInfo, idList, idIndex, px, x);
    }


    @Override
    public void close(Long id) {
        pirSessionCache.removeTask(id);
    }

    private AjaxResult<String> queryResult(PirTaskInfo taskInfo, List<String> ids, int realIndex, BigInteger px, BigInteger x){
        Map<String, String> header = new HashMap<>();
        header.put(CacheConstants.HEADER, taskInfo.getToken());
        header.put("Content-type", MediaType.APPLICATION_JSON_VALUE);

        String url = (sslEnable ? Constants.HTTPS : Constants.HTTP) + taskInfo.getGetResultUrl() + String.format("/%s", taskInfo.getRemoteSession());

        Map<String, Object> paramObject = new HashMap<>();
        paramObject.put("px", px.toString());
        paramObject.put("dataset", taskInfo.getChiperUrl());
        paramObject.put("idList", ids);
        //log.info("querey data with size {}, random number {}", ids.size(), x.toString());
        String params = JSON.toJSONString(paramObject);
        try {
            HttpClientUtils.HttpClientResult<AjaxResult<JSONArray>> httpClientResult = HttpClientUtils.doPostWithJson(url, header, params, MAXWAITTIME, sslEnable);
            if (httpClientResult.isSuccess()) {
                AjaxResult<JSONArray> strResult = httpClientResult.get(AjaxResult.class);
                JSONArray jsonArray = strResult.getData();

                //若返回是保序的
                String ret = jsonArray.get(realIndex).toString();
                String r = parseResult(x, ret);
                if (r.startsWith(prefix)) {
                    log.info("获取OT执行结果成功， 结果{}", r);
                    return AjaxResult.success(r.substring(prefix.length()));
                }

                log.warn("解析失败,session id {} 位置{}，x {} 返回内容{} {}", taskInfo.getRemoteSession(), realIndex, x, ret, r);
            } else {
                log.error("OT解密失败！{}", httpClientResult.getContent());
                return AjaxResult.error(httpClientResult.getContent());
            }
        } catch (SocketTimeoutException se){
            log.warn("查询超时！{}", se.toString());
            return AjaxResult.error("查询超时！请调整数据为数据库方式且保证数据库查询效率。");
        } catch (Exception e) {
            log.warn("OT解密失败！{}", e.toString());
            return AjaxResult.error(e.toString());
        }

        return AjaxResult.error("解密失败");
    }

    private String getKeyInfo(PirTaskInfo taskInfo, List<PubKeyInfo> resultKeys){
        Map<String, String> header = new HashMap<>();
        header.put(CacheConstants.HEADER, taskInfo.getToken());
        header.put("Content-type", MediaType.APPLICATION_JSON_VALUE);

        String url = (sslEnable? Constants.HTTPS : Constants.HTTP) + taskInfo.getGetKeysUrl() + String.format("/%d/%d", taskInfo.getFakeRate() + 2, taskInfo.getEncrpyLen());

        try {
            HttpClientUtils.HttpClientResult<AjaxResult> httpClientResult = HttpClientUtils.doGet(url, header, null, sslEnable);
            if (httpClientResult.isSuccess()) {
                AjaxResult strResult = httpClientResult.get(AjaxResult.class);
                JSONObject resultJson = JSONObject.parseObject(strResult.getData().toString());
                String sessionNum = resultJson.getString("sessionId");
                JSONArray keysArray = resultJson.getJSONArray("keyList");

                resultKeys.addAll(keysArray.toJavaList(PubKeyInfo.class));

                return sessionNum;
            } else {
                log.error("提交OT请求失败！{}", httpClientResult.getContent());
                return "";
            }
        } catch (Exception e) {
            log.error("提交OT请求失败！{}", e.toString());
            return "";
        }

    }
    public BigInteger calcRSA(BigInteger num, BigInteger k1, BigInteger m) {
        return num.modPow(k1, m);
    }

    private static String  parseResult(BigInteger x, String result){
        int xBytes = x.bitLength() / 8 + (x.bitLength() % 8 == 0 ? 0 : 1);
        BigInteger resInteger = new BigInteger(result);
        int dataLength = resInteger.bitLength() / 8 + (resInteger.bitLength() % 8 == 0 ? 0 : 1);
        byte[] dataBytes = resInteger.toByteArray();
        if(xBytes >= dataLength){
            return new String(x.xor(resInteger).toByteArray());
        }else {

            //byte[] byteResult = new byte[dataBytes.length];
            byte[] byteX = x.toByteArray();
            for (int m = 0; m < dataBytes.length; ) {
                for (byte b : byteX) {
                    dataBytes[m] = (byte) (dataBytes[m] ^ b);
                    m++;
                    if (m >= dataBytes.length) {
                        break;
                    }
                }
            }

            return new String(dataBytes);
        }
    }

    public static void main(String[] args) {
        String x =   "137184914163570521870997444369902607526990553738990763556139713376326127893410311070066798653669712811047299056486634127357782651418487551993101624941";
        String ret = "137184914163570521870997444369902607526990553738990763556139713376326127893410311070066798653677384803242424859884289280611890832014894919927301540446";

        String test = "testttttttt";
        byte[] testb = test.getBytes();
        BigInteger b = new BigInteger(x);
        BigInteger c = b.xor(new BigInteger(testb));
        System.out.println(c.toString());

        System.out.println(new String(b.xor(c).toByteArray()));

        BigInteger xxx  = new BigInteger(ret);
        BigInteger ddddd = b.xor(xxx);
        String s = new String(ddddd.toByteArray());
        System.out.println(s);

        s = FastPirClientImpl.parseResult(b, ret);
        System.out.println(s);
    }
}

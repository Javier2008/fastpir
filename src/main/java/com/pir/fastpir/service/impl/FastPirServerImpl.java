package com.pir.fastpir.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.pir.fastpir.common.AjaxResult;
import com.pir.fastpir.component.KeyInfo;
import com.pir.fastpir.component.PirSessionCache;
import com.pir.fastpir.component.PubKeyInfo;
import com.pir.fastpir.component.Reader4Pir;
import com.pir.fastpir.service.FastPirServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FastPirServerImpl implements FastPirServer {
    Random rand = new Random();
    @Autowired
    private PirSessionCache keyMapper;
    final int MAX_SESSION_LEN = 1000;

    @Autowired
    Reader4Pir reader;

    private String defaultSession;

    private static  String prefix = "Pir";

    @PostConstruct
    public void init2(){
        //init(10005);
        log.info("finish init");
    }
    @Override
    public void init(int maxNumber) {
        //初始化Key池，作为后续数据使用
        defaultSession = genAndSaveKeys(Math.max(maxNumber, 100), 512);
    }

    @Override
    public JSONObject genPubKeys(int size, int keyLength){
        JSONObject jsonObject = new JSONObject();

        String id = genAndSaveKeys2(size, keyLength);

        List<KeyInfo> newKeys = keyMapper.getAllKeys(id);
        List<PubKeyInfo> keys = newKeys.stream().map(KeyInfo::getPubKey).collect(Collectors.toList());

        jsonObject.put("sessionId", id);
        jsonObject.put("keyList", keys);

        return jsonObject;
    }
    private String genAndSaveKeys2(int size, int keyLength) {
        List<KeyInfo> keys = keyMapper.getAllKeys(defaultSession);
        String uuid = UUID.randomUUID().toString();
        List<KeyInfo> newKeys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            newKeys.add(keys.get(i));
        }
        keyMapper.saveKeys(uuid, newKeys);
        return uuid;
    }
    private String genAndSaveKeys(int size, int keyLength) {
        String uuid = UUID.randomUUID().toString();

        List<KeyInfo> newKeys = new ArrayList<>();
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            for (int i = 0; i < size; i++) {
                KeyPair kp = kpg.genKeyPair();
                RSAPublicKey puk = (RSAPublicKey) kp.getPublic();
                RSAPrivateKey prk = (RSAPrivateKey) kp.getPrivate();

                KeyInfo keyInfo = new KeyInfo();

                keyInfo.setPriD(prk.getPrivateExponent().toString());
                keyInfo.setPriN(prk.getModulus().toString());
                keyInfo.setPubE(puk.getPublicExponent().toString());
                keyInfo.setPubN(puk.getModulus().toString());

                newKeys.add(keyInfo);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyMapper.saveKeys(uuid, newKeys);

        return uuid;
    }

    private List<String> getData(String dataUrl, List<String> keys){
        List<String> data = new ArrayList<>();

        Map<String, String> dataMap = reader.read(dataUrl, keys);
        if (null == dataMap){
            //若没有找到数据，构建空数据
            dataMap = new HashMap<>();
        }

        for (String id : keys){
            String row = dataMap.get(id);
            //log.info("query data key {}, data {}", id, row);
            if (null == row){
                data.add(prefix);
            }else {
                data.add(prefix + row);
            }
        }
        return data;
    }

    @Override
    public AjaxResult<List<BigInteger>> queryResult(String sessionId, String dataUrl, BigInteger px, List<String> queryIds) {
        List<BigInteger> results = new ArrayList<>();

        List<KeyInfo> sessionKeys = keyMapper.getAllKeys(sessionId);
        if (null == sessionKeys || sessionKeys.size() < queryIds.size()){
            log.warn("get key failed");
            return AjaxResult.error("get keys failed, please reset the session");
        }

        List<String> data = getData(dataUrl, queryIds);
        log.info("get data size {}", data.size());

        for (int i = 0; i < queryIds.size(); i++) {
            KeyInfo key = sessionKeys.get(i);
            String person = data.get(i);
            BigInteger priD = new BigInteger(key.getPriD());
            BigInteger priN = new BigInteger(key.getPriN());
            BigInteger x = calcRSA(px, priD, priN);

            //log.info("x = {}", x);
            byte[] dataBytes = person.getBytes(StandardCharsets.UTF_8);

            int xBytes = x.bitLength() / 8 + (x.bitLength() % 8 == 0 ? 0 : 1);
            if(xBytes >= dataBytes.length){
                //log.info("{} {} {} {}",sessionId,  i, x, x.xor(new BigInteger(dataBytes)));
                results.add(x.xor(new BigInteger(dataBytes)));
            }else{
                byte[] byteResult = new byte[dataBytes.length];
                byte[] byteX = x.toByteArray();
                for (int m = 0; m < dataBytes.length; ){
                    for (byte b : byteX) {
                        byteResult[m] = (byte) (dataBytes[m] ^ b);
                        m++;
                        if (m >= dataBytes.length){
                            break;
                        }
                    }
                }
                results.add(new BigInteger(byteResult));
            }
        }
        // log.info(String.format("time consumed on calc keys: %d %d %d" , time1, time2, (System.currentTimeMillis()-t1)));
        return AjaxResult.success(results);
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedDelay = 24 * 3600 * 1000L)
    public void ageKeys(){
        keyMapper.ageKeys(24 * 3600 * 1000);
    }
    public static BigInteger calcRSA(BigInteger num, BigInteger k1, BigInteger m) {
        return num.modPow(k1, m);
    }


}

package com.pir.fastpir.component;


import com.pir.fastpir.service.PirQueryID;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PirTaskInfo {
    long id;
    long createTime;
    //查询方
    int fakeRate = 10;
    int encrpyLen = 1024;

    PirQueryID pirQueryIDFactory;
    String remoteUrlOtQuery = null;
    String remoteUrlOtresult = null;

    String getKeysUrl = null;
    String getResultUrl = null;

    String token = null;
    String chiperUrl;
    String remoteSession;
    List<PubKeyInfo> keyInfos;

    private static volatile AtomicInteger uid = new AtomicInteger(0);
    public PirTaskInfo(){
        id = uid.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFakeRate() {
        return fakeRate;
    }

    public void setFakeRate(int fakeRate) {
        this.fakeRate = fakeRate;
    }

    public int getEncrpyLen() {
        return encrpyLen;
    }

    public void setEncrpyLen(int encrpyLen) {
        this.encrpyLen = encrpyLen;
    }

    public PirQueryID getPirQueryIDFactory() {
        return pirQueryIDFactory;
    }

    public void setPirQueryIDFactory(PirQueryID pirQueryIDFactory) {
        this.pirQueryIDFactory = pirQueryIDFactory;
    }

    public String getRemoteUrlOtQuery() {
        return remoteUrlOtQuery;
    }

    public void setRemoteUrlOtQuery(String remoteUrlOtQuery) {
        this.remoteUrlOtQuery = remoteUrlOtQuery;
    }

    public String getRemoteUrlOtresult() {
        return remoteUrlOtresult;
    }

    public void setRemoteUrlOtresult(String remoteUrlOtresult) {
        this.remoteUrlOtresult = remoteUrlOtresult;
    }

    public String getGetKeysUrl() {
        return getKeysUrl;
    }

    public void setGetKeysUrl(String getKeysUrl) {
        this.getKeysUrl = getKeysUrl;
    }

    public String getGetResultUrl() {
        return getResultUrl;
    }

    public void setGetResultUrl(String getResultUrl) {
        this.getResultUrl = getResultUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getChiperUrl() {
        return chiperUrl;
    }

    public void setChiperUrl(String chiperUrl) {
        this.chiperUrl = chiperUrl;
    }

    public String getRemoteSession() {
        return remoteSession;
    }

    public void setRemoteSession(String remoteSession) {
        this.remoteSession = remoteSession;
    }

    public List<PubKeyInfo> getKeyInfos() {
        return keyInfos;
    }

    public void setKeyInfos(List<PubKeyInfo> keyInfos) {
        this.keyInfos = keyInfos;
    }

    public static AtomicInteger getUid() {
        return uid;
    }

    public static void setUid(AtomicInteger uid) {
        PirTaskInfo.uid = uid;
    }
}

package com.pir.fastpir.component;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionInfo {
    long id;
    long createTime;
    List<KeyInfo> priKeys;

    //数据方
    List<String> data;

    private static volatile AtomicInteger uid = new AtomicInteger(0);
    public SessionInfo(){
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

    public List<KeyInfo> getPriKeys() {
        return priKeys;
    }

    public void setPriKeys(List<KeyInfo> priKeys) {
        this.priKeys = priKeys;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public static AtomicInteger getUid() {
        return uid;
    }

    public static void setUid(AtomicInteger uid) {
        SessionInfo.uid = uid;
    }
}

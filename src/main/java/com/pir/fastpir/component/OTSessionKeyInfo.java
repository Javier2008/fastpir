package com.pir.fastpir.component;


import java.util.ArrayList;
import java.util.List;

public class OTSessionKeyInfo {
    private long sessionNum;
    private List<PubKeyInfo> keys = new ArrayList<>();

    public long getSessionNum() {
        return sessionNum;
    }

    public void setSessionNum(long sessionNum) {
        this.sessionNum = sessionNum;
    }

    public List<PubKeyInfo> getKeys() {
        return keys;
    }

    public void setKeys(List<PubKeyInfo> keys) {
        this.keys = keys;
    }
}

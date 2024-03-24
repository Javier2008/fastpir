package com.pir.fastpir.component;


public class PubKeyInfo {
    String pubN;
    String pubE;
    //查询的ID信息
    //String id;

    public String getPubE() {
        return pubE;
    }

    public void setPubE(String pubE) {
        this.pubE = pubE;
    }

    public String getPubN() {
        return pubN;
    }

    public void setPubN(String pubN) {
        this.pubN = pubN;
    }
}

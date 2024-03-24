package com.pir.fastpir.service;

import com.pir.fastpir.service.impl.PirQueryIDDefault;

import java.util.List;

public interface PirQueryID {
    void init(int type, long min, long max, String rule);
    void generateFakeID(String realId, int numbers);
    List<String> getQueryIDs();
    int getRealIdIndex();

    String getIdRules();

    static String formatRule(Integer maxLen, String rule){
        if (null == maxLen || null == rule){
            return "";
        }
        return PirQueryIDDefault.formatRuleStr(maxLen, rule);
    }
}

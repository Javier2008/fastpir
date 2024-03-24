package com.pir.fastpir.service.impl;

import com.pir.fastpir.service.PirQueryID;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PirQueryIDDefault implements PirQueryID {
    private long maxValue;
    private long minValue;
    // 0, int, 1 32-md5, 2 string, 3 64-md5
    private int type;
    private List<Integer>[] rules = null;

    private final Random rand = new Random();
    private List<String> idList = null;
    private List<Integer> realIdIndex = null;

    @Override
    public String getIdRules(){
        return genRulesStr(rules);
    }

    public static String formatRuleStr(int maxlen, String str){
        List<Integer>[] ruleList;
        try {
            ruleList = translateRules(maxlen, 1, str);
        }catch (Exception e){
            log.warn("Pir Query Rule format Error {}", e.getMessage());
            return "";
        }
        return genRulesStr(ruleList);
    }
    public static String genRulesStr(List<Integer>[] rules){
        StringBuilder stringBuilder = new StringBuilder();
        if (rules == null || rules.length == 0){
            return "";
        }
        stringBuilder.append('[');

        int preIndex = 0;
        String preRule = genRuleStr(rules[0]);
        for (int i =0; i< rules.length; i++){
            String rule = genRuleStr(rules[i]);
            if (rule.equals(preRule)){
                continue;
            }

            //前后规则不相同，需要将之前的规则写到结果中
            if (preIndex + 1 < i) {
                stringBuilder.append(preIndex);
                stringBuilder.append('-');
                stringBuilder.append(i-1);
                stringBuilder.append(":");
                stringBuilder.append(preRule);
                stringBuilder.append("; ");

                preIndex = i;
                preRule = rule;
            }else{
                stringBuilder.append(preIndex);
                stringBuilder.append(":");
                stringBuilder.append(preRule);
                stringBuilder.append("; ");

                preIndex = i;
                preRule = rule;
            }
        }

        //最后一个规则
        if (preIndex + 1 < rules.length) {
            stringBuilder.append(preIndex);
            stringBuilder.append('-');
            stringBuilder.append(rules.length-1);
            stringBuilder.append(":");
            stringBuilder.append(preRule);
        }else{
            stringBuilder.append(preIndex);
            stringBuilder.append(":");
            stringBuilder.append(preRule);
        }

        stringBuilder.append(']');
        return stringBuilder.toString();
    }
    public static String genRuleStr(List<Integer> rule){
        if (null == rule || rule.size() == 0){
            return "{}";
        }

        StringBuilder strRule = new StringBuilder();
        strRule.append("{");
        char firstChar =  (char)rule.get(0).shortValue();
        int firstIndex = 0;
        int tmp;
        char preChar = firstChar;

        for (int i = 1; i < rule.size(); i++){
            tmp = rule.get(i);

            if ((preChar + 1) == tmp){
                //若是连续的，直接跳过处理
                preChar = (char)tmp;
                continue;
            }

            if (firstIndex + 1 == i){
                //前后不连续，上一个字符区间的结束
                strRule.append(firstChar);
                strRule.append(",");

                //新的字符区间开始
                firstChar = (char)tmp;
                firstIndex = i;
            }else{
                //至少跳跃了一个字符
                strRule.append(firstChar);
                strRule.append('-');
                tmp = rule.get(i -1);
                strRule.append((char)tmp);
                strRule.append(",");

                //分段结束了,新的分段开始
                tmp = rule.get(i);
                firstChar = (char)tmp;
                firstIndex = i;
            }

            preChar = (char)tmp;
        }
        //最后一个分段
        if (firstIndex + 1 < rule.size()){
            strRule.append(firstChar);
            strRule.append('-');
            tmp = rule.get(rule.size() -1);
            strRule.append((char)tmp);
        }else{
            strRule.append(firstChar);
        }
        strRule.append("}");
        return strRule.toString();
    }
    /*rule支持规则
    *    [0:{  };  *:{0-9,a-z,A-Z,_,'};  1 - 5 : {a - z};  3:{ }]
    *    id中不能包含 : ; , 特殊字符
    * */
    @Override
    public void init(int type, long min, long max, String rule) {
        maxValue = max;
        minValue = min;
        this.type = type;
        if (null == rule || rule.isEmpty()){
            rules = null;
        }
        if (type == 2) {
            if (max < 255 && min >=1 ){
                rules = translateRules((int) max, (int) min, rule);
            }else{
                rules = null;
            }
        }
        if (null != idList){
            idList.clear();
        }else {
            idList = new ArrayList<>();
        }

        if (null != realIdIndex){
            realIdIndex.clear();
        }else {
            realIdIndex = new ArrayList<>();
        }
    }

    @Override
    public void generateFakeID(String realId, int numbers) {
        switch (type){
            case 0: generateIntFakeID(realId, numbers);
                break;
            case 1: generateFakeMD5ID(realId, numbers, 32);
                break;
            case 3:
                generateFakeMD5ID(realId, numbers, 64);
                break;
            case 2:
            default:
                generateFakeStrID(realId, numbers);
                break;
        }
    }

    public void generateIntFakeID(String realId, int numbers) {
        idList.clear();
        realIdIndex.clear();

        if (numbers == 0){
            return;
        }

        Map<Long, Integer> tmpRes = new HashMap<>();
        while (tmpRes.size() < numbers){
            long r = rand.nextLong();
            r = (r < 0) ? -r : r;
            //可能会溢出？
            r = r % (maxValue - minValue) + minValue;

            //避免和readid重复
            if (!realId.equals(String.valueOf(r))) {
                tmpRes.put(r, 0);
            }
        }
        idList.addAll(tmpRes.keySet().stream().map(Object::toString).collect(Collectors.toList()));
        int addIndex = rand.nextInt(numbers);
        idList.add(addIndex, realId);
        realIdIndex.add(addIndex);
    }

    private String randomMD5(int len){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i< len/8; i++) {
            int num = rand.nextInt();
            stringBuilder.append(String.format("%08X", num));
        }

        return stringBuilder.toString();
    }

    public void generateFakeMD5ID(String realId, int numbers, int len) {
        idList.clear();
        realIdIndex.clear();

        if (numbers == 0){
            return;
        }

        Map<String, Integer> tmpRes = new HashMap<>();
        while (tmpRes.size() < numbers){
            String md5 = randomMD5(len);
            //避免和readid重复
            if (!realId.equals(md5)) {
                tmpRes.put(md5, 0);
            }
        }
        idList.addAll(tmpRes.keySet().stream().map(Object::toString).collect(Collectors.toList()));

        int addIndex = rand.nextInt(numbers);
        idList.add(addIndex, realId);
        realIdIndex.add(addIndex);
    }

    public static List<Integer>[] translateRules(int maxlen, int minlen, String rule){
        if (null == rule || rule.isEmpty() || maxlen <= 0){
            return null;
        }
        List<Integer>[] rules = new List[maxlen];
        for (int i =0; i < maxlen ; i ++){
            rules[i] = new ArrayList<>();
        }
        rule =  rule.trim();
        //去掉首尾括号，；表示规则分隔
        rule = rule.substring(1, rule.length()-1);
        String[] colRules = rule.split(";");
        for (String colRule : colRules){
            String[] item = colRule.split(":");
            if (item.length != 2){
                log.warn("id format error, lost :");
                continue;
            }
            item[1] = item[1].trim();
            if (!item[1].startsWith("{") || !item[1].endsWith("}")){
                log.warn("id format value should bengin with { end with }");
                continue;
            }
            item[1] = item[1].substring(1, item[1].length()-1);
            String[] cols = item[0].split(",");
            String[] values = item[1].split(",");

            //将取值规则翻译
            //特殊规则处理,若规则为空，则赋予默认规则
            List<Integer> avaliabelValue = new ArrayList<>();
            if (values.length == 1){
                String value = values[0].trim();
                if (value.isEmpty()){
                    values = new String[3];
                    values[0] = "0-9";
                    values[1] = "a-z";
                    values[2] = "A-Z";
                }
            }
            //翻译规则
            for (String value : values) {
                value = value.trim();
                if (value.isEmpty()){
                    log.warn("id format error, lost rule");
                    continue;
                }
                if (value.contains("-")) {
                    String[] range = value.split("-");
                    if (range.length != 2) {
                        log.warn("id format error!");
                        continue;
                    }
                    for (int i = range[0].getBytes()[0]; i <= range[1].trim().getBytes()[0]; i++) {
                        avaliabelValue.add(i);
                    }
                } else {
                    avaliabelValue.add((int) value.getBytes()[0]);
                }
            }

            for (String col : cols){
                col = col.trim();
                if (col.isEmpty()){
                    log.warn("id format error");
                    continue;
                }
                if (col.contains("-")){
                    String[] range = col.split("-");
                    if (range.length != 2){
                        log.warn("id format error");
                        continue;
                    }
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    if (start < 0 || end > maxlen){
                        log.warn("id format error");
                        continue;
                    }
                    for(int i = start; i <= end; i++){
                        rules[i] = avaliabelValue;
                    }
                }else if (col.equals("*")){
                    for(int i = 0; i < maxlen; i++){
                        rules[i]= avaliabelValue;
                    }
                }else {
                    int index = Integer.parseInt(col.trim());
                    if (index < 0 || index > maxlen){
                        log.warn("id format error");
                        continue;
                    }
                    rules[index]= avaliabelValue;
                }
            }
        }

        return rules;
    }

    private String randomStr(int maxlen, int minlen, List<Integer>[] rules){
        int len;
        if (maxlen > minlen) {
            len = rand.nextInt(maxlen - minlen) + minlen;
        }else{
            len = maxlen;
        }
        char[] strID = new char[len];

        if (null == rules){
            for (int i = 0; i < len; i++){
                int r = rand.nextInt(52);
                if (r < 10){
                    strID[i] = (char)('0' + r);
                }else if (r < 36){
                    strID[i] = (char)('A' + r - 10);
                }else{
                    strID[i] = (char)('a' + r - 36);
                }
            }
            return String.valueOf(strID);
        }

        //不能申请超过规则的字符串长度
        len = Math.min(len, rules.length);

        for (int i = 0; i < len; i++){
            List<Integer> rule = rules[i];
            if ((null == rule) || rule.size() == 0){
                int r = rand.nextInt(52);
                if (r < 10){
                    strID[i] = (char)('0' + r);
                }else if (r < 36){
                    strID[i] = (char)('A' + r - 10);
                }else{
                    strID[i] = (char)('a' + r - 36);
                }
            }else if (rule.size() == 1){
                strID[i] = (char)rule.get(0).byteValue();
            }else{
                int r = rand.nextInt(rule.size());
                strID[i] = (char)rule.get(r).byteValue();
            }
        }

        return  String.valueOf(strID);
    }


    public void generateFakeStrID(String realId, int numbers) {
        idList.clear();
        realIdIndex.clear();
        if (numbers == 0){
            return;
        }

        Map<String, Integer> tmpRes = new HashMap<>();
        while (tmpRes.size() < numbers){
            String strID = randomStr((int)maxValue, (int)minValue, rules);
            //避免和readid重复
            if (!realId.equals(strID)) {
                tmpRes.put(strID, 0);
            }
        }
        idList.addAll(tmpRes.keySet().stream().map(Object::toString).collect(Collectors.toList()));

        int addIndex = rand.nextInt(numbers);
        idList.add(addIndex, realId);
        realIdIndex.add(addIndex);
    }


    @Override
    public List<String> getQueryIDs() {
        return idList;
    }

    @Override
    public int getRealIdIndex() {
        return realIdIndex.get(0);
    }


}

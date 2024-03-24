package com.pir.fastpir.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class PirSessionCacheImpl implements  PirSessionCache{
    Map<Long, SessionInfo> cacheSession = new HashMap<>();
    Map<Long, PirTaskInfo> cacheTask = new HashMap<>();
    Map<String, CacheKeyInfo> keyInfos = new HashMap<>();

    public static class CacheKeyInfo{
        Date createTime;
        List<KeyInfo> keys;

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public List<KeyInfo> getKeys() {
            return keys;
        }

        public void setKeys(List<KeyInfo> keys) {
            this.keys = keys;
        }
    }
    public List<KeyInfo> getAllKeys(String uuid){
        CacheKeyInfo keyInfo = keyInfos.get(uuid);
        if (null != keyInfo){
            return keyInfo.getKeys();
        }
        return null;
    }
    public void saveKeys(String uuid, List<KeyInfo> keys){
        Date date = new Date();
        CacheKeyInfo keyInfo = new CacheKeyInfo();
        keyInfo.setCreateTime(date);
        keyInfo.setKeys(keys);
        keyInfos.put(uuid, keyInfo);
    }
    public void ageKeys(long interval){
        Date currentTime = new Date();
        List<String> deleteList = new ArrayList<>();
        for (Map.Entry<String, CacheKeyInfo> mapEntry :  keyInfos.entrySet()){
            CacheKeyInfo cacheKeyInfo = mapEntry.getValue();
            if (currentTime.getTime() - cacheKeyInfo.getCreateTime().getTime() > interval){
                deleteList.add(mapEntry.getKey());
            }
        }

        for(String key : deleteList){
            keyInfos.remove(key);
        }
    }
    @Override
    public void saveTask(PirTaskInfo info) {
        if (null == info){
            return;
        }
        PirTaskInfo taskInfo = cacheTask.get(info.getId());
        if (taskInfo != null){
            cacheTask.remove(info.getId());
        }

        cacheTask.put(info.getId(), info);
    }

    @Override
    public PirTaskInfo getTaskInfo(long id) {
        return cacheTask.get(id);
    }

    @Override
    public void removeTask(long id) {
        cacheTask.remove(id);
    }


}

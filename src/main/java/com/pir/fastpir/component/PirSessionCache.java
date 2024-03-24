package com.pir.fastpir.component;

import java.util.List;

public interface PirSessionCache {
    //被查询方信息
    List<KeyInfo> getAllKeys(String uuid);
    void saveKeys(String uuid, List<KeyInfo> keys);
    void ageKeys(long interval);

    //查询方信息, 查询方信息需要入库，否则重启后创建的任务没有办法执行了
    void saveTask(PirTaskInfo info);
    PirTaskInfo getTaskInfo(long id);
    void removeTask(long id);
}

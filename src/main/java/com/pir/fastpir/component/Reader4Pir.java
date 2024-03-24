package com.pir.fastpir.component;

import com.csvreader.CsvReader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class Reader4Pir {
    //private final SourceDbService sourceDbService;



    public static char separator = ',';
    public static String separatorStr = ",";

    Map<String, String> data;
    /**
     * 通过构造器注入
     *
     */
    public Reader4Pir() {
        data = new HashMap<>();

        CsvReader reader = null;
        try {
            reader = new CsvReader("data.csv", separator, StandardCharsets.UTF_8);
            reader.readHeaders();

            while (reader.readRecord()) {
                data.put(reader.get(0), reader.getRawRecord());
                //指定列中 保存数据
            }
        } catch (Exception e) {
            log.error("CSV文件读取抽样数据失败 e = ", e);
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
    }

    public Map<String, String> read(String csvFile, List<String> keys) {
        log.info("从CSV获取数据");
        return csv(csvFile, 0, keys);

    }

//    private Map<String, String> db(Long sourceId, String columnsStr, String tableName, String datasetType, int idIndex, List<String> keys) {
//        //获取source_db实体
//        SourceDb sourceDb = sourceDbService.queryById(sourceId);
//        if (sourceDb == null) {
//            log.info("未查询到数据库相关信息{}", sourceId);
//            return null;
//        }
//
//        StringBuilder sqlBuiler = new StringBuilder();
//        if (keys.size() > 0) {
//            String[] headers = columnsStr.split(",");
//            sqlBuiler.append(headers[idIndex]).append(" in ('");
//            for (String key : keys) {
//                sqlBuiler.append(key).append("','");
//            }
//            sqlBuiler.delete(sqlBuiler.length() - 2, sqlBuiler.length());
//            sqlBuiler.append(")");
//        }
//
//        log.info("当前表名：{}", tableName);
//        log.info("当前查询列：{}", columnsStr);
//        log.info("当前数据类型：{}", datasetType);
//
//        //sql解析
//        String sql;
//        if (StringUtils.isNotEmpty(sourceDb.getDbSql())) {
//            sql = sourceDb.getDbSql();
//            log.info("用户指定sql：{}", sql);
//        } else {
//
//            //解析sql
//            if (sqlBuiler.length() == 0) {
//                sql = new JOOQUtil(sourceDb).dsl
//                        .select(JOOQUtil.fetchFields(columnsStr, sourceDb.getDbType())).from(tableName)
//                        .getSQL();
//            }else{
//                sql = new JOOQUtil(sourceDb).dsl
//                        .select(JOOQUtil.fetchFields(columnsStr, sourceDb.getDbType())).from(tableName).where(sqlBuiler.toString())
//                        .getSQL();
//            }
//
//
//            log.info("后端生成sql：{}", sql);
//        }
//
//        //获取数据
//        List<Object[]> list = JdbcUtil.selectByColumn(sql, columnsStr, datasetType, sourceDb);
//        if (CollectionUtils.isEmpty(list)) {
//            log.info("数据库查询未查到数据");
//            return null;
//        }
//
//        //数据加工
//        log.info("数据库数据长度 list size = {}", list.size());
//        Map<String, String> result = new HashMap<>();
//
//        for (Object[] objects : list){
//            String a = StringUtils.join(objects, ",");
//            result.put(objects[idIndex].toString(), a);
//        }
//        return result;
//    }


    private Map<String, String> csv(String fileName, int idIndex, List<String> keys) {
        CsvReader reader = null;
        Map<String, String> result = new HashMap<>();
        if (data != null){
            List<String> keyList = new ArrayList<>(keys);
            Iterator<String> it =  keyList.iterator();
            while (it.hasNext()){
                String key = it.next();
                if (data.get(key) != null){
                    it.remove();
                    result.put(key, data.get(key));
                    break;
                }
            }
            if  (keyList.size() == 0){
                return result;
            }
            return result;
        }
        try {
            reader = new CsvReader(fileName, separator, StandardCharsets.UTF_8);
            reader.readHeaders();

            List<String> keyList = new ArrayList<>(keys);
            while (reader.readRecord()) {
                //指定列中 保存数据
                Iterator<String> it =  keyList.iterator();
                while (it.hasNext()){
                    String key = it.next();
                    if (key.equals(reader.get(idIndex))){
                        it.remove();
                        result.put(key, reader.getRawRecord());
                        break;
                    }
                }
                if  (keyList.size() == 0){
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("CSV文件读取抽样数据失败 e = ", e);
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
        // 获取数据
        return result;
    }
}

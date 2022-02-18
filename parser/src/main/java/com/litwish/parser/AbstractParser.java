package com.litwish.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Date: 2022/2/18 10:40
 * @Authror: Xiaoming Zhang
 */
public abstract class AbstractParser implements Parser {


    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Map<String, String> headMapping = new HashMap<String,String>();
    protected Map<Integer,String> columnIndexMapping = new LinkedHashMap<Integer,String>();

    protected void intHead(Connection connection) throws SQLException {
        String className = this.getClass().getSimpleName();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select file_col,db_col from ibms_core.file_mapping where class_name=\"" + className+"\"");
        while (resultSet.next()) {
            headMapping.put(resultSet.getString("file_col"), resultSet.getString("db_col"));
        }
        logger.info("获取到head映射:{}",headMapping.toString());
    }

    protected String getPath(String... paths){
        String filePath="";
        for (String path :paths) {
            filePath+=path==null?"":(path.endsWith("/") ? path : path + "/");
        }
       return filePath;
    }

    protected void oncePs(ArrayList<String> oneData, PreparedStatement ps) throws SQLException {
        for (int i = 0; i < oneData.size(); i++) {
            String columnValue = oneData.get(i);
            ps.setString(i+1,columnValue==""?null:columnValue);
        }
        ps.addBatch();
    }

    protected String getPrepareSQL(String tableName){
        String buildSql = "insert into %s (%s) values(%s);";
        StringBuilder sqlPreBuilder = new StringBuilder();
        StringBuilder sqlBehBuilder = new StringBuilder();
        for (String value : columnIndexMapping.values()) {
            sqlPreBuilder.append(","+value);
            sqlBehBuilder.append(",?");
        }
        String sql = String.format(buildSql,tableName, sqlPreBuilder.toString().replaceFirst(",",""),
                sqlBehBuilder.toString().replaceFirst(",",""));
        return sql;
    }

}

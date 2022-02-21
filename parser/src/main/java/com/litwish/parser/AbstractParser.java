package com.litwish.parser;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    /**
     * 文件字段与数据库字段映射
     */
    protected Map<String, String> headMapping = new HashMap<String, String>();
    /**
     * 数据库与一行数据index映射
     */
    protected Map<String, Integer> columnIndexMapping = new LinkedHashMap<String, Integer>();
    /**
     * 临时表后缀
     */
    protected static final String TEMP_TABLE_SUFFIX = "_temp";

//    abstract void parse(Connection connection, File... files) throws Exception;
//
//    abstract boolean check(Connection connection, File... files) throws Exception  ;



    /**
     * 初始化文件字段与数据库字段映射
     *
     * @param connection
     * @throws SQLException
     */
    protected void intHead(Connection connection) throws SQLException {
        String className = this.getClass().getSimpleName();
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select file_col,db_col from ibms_core.file_mapping where class_name=\"" + className + "\"");
        ) {
            while (resultSet.next()) {
                headMapping.put(resultSet.getString("file_col"), resultSet.getString("db_col"));
            }
        }
        logger.info("获取到head映射:{}", headMapping.toString());
    }

    /**
     * 拼接不同文件路径,获取文件的相对路径
     *
     * @param paths
     * @return
     */
    protected String getPath(String... paths) {
        String filePath = "";
        for (String path : paths) {
            filePath += path == null ? "" : (path.endsWith("/") ? path : path + "/");
        }
        return filePath;
    }

    /**
     * 插入一条数据
     *
     * @param oneData
     * @param ps
     * @throws SQLException
     */
    protected void oncePs(ArrayList<String> oneData, PreparedStatement ps) throws SQLException {
        int i =1;
        for (Integer index : columnIndexMapping.values()) {
            String value = oneData.get(index);
            ps.setString(i++, value == "" ? null : value);
        }
        ps.addBatch();
    }

    /**
     * 创建出preparedStatement的insert into的sql语句
     *
     * @param tableName
     * @return
     */
    protected String getPrepareSQL(String tableName) {
        String buildSql = "insert into %s (%s) values(%s);";
        StringBuilder sqlPreBuilder = new StringBuilder();
        StringBuilder sqlBehBuilder = new StringBuilder();
        for (String key : columnIndexMapping.keySet()) {
            sqlPreBuilder.append("," + key);
            sqlBehBuilder.append(",?");
        }
        String sql = String.format(buildSql, tableName, sqlPreBuilder.toString().replaceFirst(",", ""),
                sqlBehBuilder.toString().replaceFirst(",", ""));
        return sql;
    }

    /**
     * 临时表中数据插入到最终表...
     *
     * @param connection
     * @throws SQLException
     */
    protected void copyDataToFinalTable(Connection connection, String tableName) throws SQLException {
        connection.setAutoCommit(false);
        String sql = String.format("insert into %s  select * from %s", tableName, tableName + TEMP_TABLE_SUFFIX);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.execute();
            connection.commit();
        }
    }

    /**
     * 清空中间表
     *
     * @param connection
     * @throws SQLException
     */
    protected void truncateTempTable(Connection connection, String tableName) throws SQLException {
        connection.setAutoCommit(false);
        String sql = String.format("truncate table %s", tableName);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.execute();
            connection.commit();
        }
    }



}

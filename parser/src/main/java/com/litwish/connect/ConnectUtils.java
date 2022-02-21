package com.litwish.connect;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Description: TODO
 * @Date: 2022/2/17 15:28
 * @Authror: Xiaoming Zhang
 */
public class ConnectUtils {

    private static String url ;
    private static String user ;
    private static String password ;


    static {
        InputStream resourceAsStream = ConnectUtils.class.getClassLoader().getResourceAsStream("database.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
        Class.forName(properties.getProperty("driver.name"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        url=properties.getProperty("jdbc.utl");
        user=properties.getProperty("user.name");
        password=properties.getProperty("user.password");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
}

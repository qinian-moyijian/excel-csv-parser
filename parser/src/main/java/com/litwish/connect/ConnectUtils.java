package com.litwish.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
         url="jdbc:mysql://localhost:3306/ibms_core?serverTimezone=UTC";
         user="root";
        password="root";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
}

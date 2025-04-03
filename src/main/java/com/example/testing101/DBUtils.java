package com.example.testing101;

import java.sql.*;

public class DBUtils {
    private static String url = "jdbc:mysql://localhost:3306/ssw_project";
    private static String appUsername = "root";  //here i changed from root to user
    private static String appPassword = ""; //insecure practice but i put a password also and am hardcoding here but its for dev purposes

    public static Connection establishConnection(){
        Connection con = null;
        try{
            con = DriverManager.getConnection(url, appUsername, appPassword);
            //this is just like web2 in connecting to the dbms
            System.out.println("Connection Successful");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return con;
    }
    public static void closeConnection(Connection con,Statement stmt){
        try{
            stmt.close(); //statement is this
            con.close();
            System.out.println("Connection is closed");
        }catch(SQLException e){
            e.getMessage();
        }
    }
}



package com.example.theagora;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConSql {
    Connection con;

    @SuppressLint("NewApi")
    public Connection conclass() {
        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        String connectURL = null;

        try {
            Log.d("ConSql", "Loading jTDS driver...");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Log.d("ConSql", "Driver loaded successfully. Preparing connection URL...");
            connectURL = "jdbc:sqlserver://agoraserver.database.windows.net:1433;database=TheAgoraDB;user=NK@agoraserver;password=p@ssword123;encrypt=false;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

            Log.d("ConSql", "Connecting to database with URL: " + connectURL);
            con = DriverManager.getConnection(connectURL);

            Log.d("ConSql", "Connection established successfully.");
        } catch (ClassNotFoundException e) {
            Log.e("ConSql", "Error loading driver: " + e.getMessage());
        } catch (Exception e) {
            Log.e("ConSql", "Error connecting to database: " + e.getMessage(), e);
        }
        return con;
    }
}

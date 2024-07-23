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
        String ip = "192.168.0.103", port = "1433", db = "TheAgoraDB", username = "Alvaro", password = "a1234b";
        StrictMode.ThreadPolicy a = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(a);
        String connectURL = null;

        try {
            Log.d("ConSql", "Loading SQL Server driver...");
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            Log.d("ConSql", "Driver loaded successfully. Preparing connection URL...");
            connectURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + db + ";user=" + username + ";" + "password=" + password + ";";

            Log.d("ConSql", "Connecting to database...");
            con = DriverManager.getConnection(connectURL);

            Log.d("ConSql", "Connection established successfully.");
        } catch (ClassNotFoundException e) {
            Log.e("ConSql", "Error loading driver: " + e.getMessage());
        } catch (Exception e) {
            Log.e("ConSql", "Error connecting to database: " + e.getMessage());
        }
        return con;
    }
}

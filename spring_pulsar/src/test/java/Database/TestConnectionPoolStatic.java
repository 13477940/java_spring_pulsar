package Database;

import framework.database.datasource.TomcatDataSource;
import framework.database.interfaces.ConnectionPool;

public class TestConnectionPoolStatic {

    private static ConnectionPool instance = null;

    public static ConnectionPool getInstance() {
        if(null == instance) {
            try {
                // you should insert database login info right here.
                instance = new TomcatDataSource.Builder()
                        .setIP("db_ip")
                        .setPort("db_port")
                        .setDatabaseType("db_type") // mariadb, mysql, postgresql, mssql or etc.
                        .setAccount("db_acc") // account
                        .setPassword("db_pwd") // password
                        .setDatabaseName("db_name") // target database name
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                instance = null;
            }
        }
        return instance;
    }

}

package org.example;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBConfig {
    private static Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("src/main/resources/db.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception properly
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}

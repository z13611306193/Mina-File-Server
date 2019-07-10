package com.fudian.mina.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zyg
 * Initialize server parameters
 */
public class Init {

    private static Properties props = new Properties();

    static {
        InputStream is = null;
        try {
            is = new FileInputStream("src/main/java/serverConfig.properties");
            //Load into memory
            props.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Configuration file failed to load!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("The profile stream failed to load!");
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("File stream close failed!");
            }
        }
    }

    //Provide methods for external access and get value from the key passed in
    public static String getProperty(String key){
        return props.getProperty(key);
    }

}

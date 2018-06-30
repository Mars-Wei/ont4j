package indi.ylins.ont4j.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Yue Lin
 * @since 2018-06-30
 */
public class Mediator {

    private String owlPath;
    private File dbDir;

    public Mediator() throws IOException {
        getProperty();
    }

    private void getProperty() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        properties.load(in);
        this.owlPath = properties.getProperty("owlPath");
        this.dbDir = new File(properties.getProperty("dbDir"));
    }



}

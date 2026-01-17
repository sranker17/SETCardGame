package com.example.setcardgame.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private final Context context;
    private final Properties properties;
    private static PropertyReader pr;

    private PropertyReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public static PropertyReader getInstance(Context context) {
        if (pr == null) return new PropertyReader(context);
        else return pr;
    }

    public Properties getProperties(String fileName) {
        try {
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(fileName);
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e("PropertiesReader", e.toString());
        }
        return properties;
    }
}

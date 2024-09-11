package com.nsoz.server;

import com.nsoz.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import lombok.Getter;

public class Language {

    @Getter
    private String lang;
    private HashMap<String, String> texts;

    public Language(String lang, File file) {
        try {
            this.lang = lang;
            this.texts = new HashMap<>();
            FileInputStream input = new FileInputStream(file);
            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            props.forEach((t, u) -> {
                texts.put(t.toString(), u.toString());
            });
        } catch (IOException ex) {
            Log.error("load config language err", ex);
        }
    }

    public String getString(String key) {
        String text = texts.get(key);
        if (text != null) {
            return text;
        } else {
            return key;
        }
    }
}

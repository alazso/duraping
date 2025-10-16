package sh.redkey.mc.duraping.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File file() {
        return new File("./config/duraping.json");
    }

    public static void load() {
        try {
            var f = file();
            if (!f.exists()) { save(); return; }
            try (var r = new FileReader(f)) {
                var cfg = GSON.fromJson(r, DuraPingConfig.class);
                if (cfg == null) cfg = new DuraPingConfig();
                DuraPingConfig.set(cfg);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void save() {
        try {
            var f = file();
            f.getParentFile().mkdirs();
            try (var w = new FileWriter(f)) {
                GSON.toJson(DuraPingConfig.get(), w);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}


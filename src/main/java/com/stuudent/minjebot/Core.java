package com.stuudent.minjebot;

import com.stuudent.minjebot.listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Core {

    public static JDA api;
    public static YamlConfiguration cf;

    public static void main(String[] args) throws LoginException {
        saveDefaultConfig();
        cf = getConfig();
        JDA api = JDABuilder.createDefault(cf.getString("TOKEN")).build();
        api.addEventListener(new CommandListener());
    }

    public static void saveDefaultConfig() {
        File file = new File("config.yml");
        if(!file.exists()) {
            ClassLoader loader = Core.class.getClassLoader();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("config.yml"), StandardCharsets.UTF_8));
                YamlConfiguration cf = YamlConfiguration.loadConfiguration(br);
                cf.save("config.yml");
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                YamlConfiguration cf = YamlConfiguration.loadConfiguration(file);
                cf.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static YamlConfiguration getConfig() {
        File file = new File("config.yml");
        if(file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            ClassLoader loader = Core.class.getClassLoader();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("config.yml"), StandardCharsets.UTF_8));
                YamlConfiguration cf = YamlConfiguration.loadConfiguration(br);
                br.close();
                return cf;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }





}

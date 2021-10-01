package com.stuudent.minjebot.data;

import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllData {

    public List<File> getUploads(User user, long startTime, long lastTime) {
        List<File> fileList = new ArrayList<>();
        File path = new File("uploads/" + user.getId());
        for(File listFile : path.listFiles()) {
            if(listFile.lastModified() >= startTime && listFile.lastModified() <= lastTime) {
                fileList.add(listFile);
            }
        }
        return fileList;
    }

}

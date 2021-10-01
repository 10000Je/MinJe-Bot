package com.stuudent.minjebot.data;

import com.stuudent.minjebot.API;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.util.List;

public class UserData {

    public User user;
    public AllData allData;

    public UserData(User user) {
        this.user = user;
        this.allData = API.getAllData();
    }

    public User getUser() {
        return this.user;
    }

    public List<File> getUploads(long startTime, long lastTime) {
        return allData.getUploads(this.user, startTime, lastTime);
    }

}

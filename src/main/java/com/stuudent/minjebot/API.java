package com.stuudent.minjebot;

import com.stuudent.minjebot.data.AllData;
import com.stuudent.minjebot.data.UserData;
import net.dv8tion.jda.api.entities.User;

public class API {

    public static AllData getAllData() {
        return new AllData();
    }

    public static UserData getUserData(User user) {
        return new UserData(user);
    }

    public static UserData getUserData(String id) {
        return new UserData(Core.api.getUserById(id));
    }

}

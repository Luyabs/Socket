package data;

import pojo.User;

import java.util.HashSet;
import java.util.Set;

public class UserData {
    private Set<String> database;

    public UserData() {
        database = new HashSet<>();
        test();
    }

    public boolean insert(User user) {
        return database.add(user.toString());
    }

    public boolean contains(User user) {
        return database.contains(user.toString());
    }

    public boolean contains(String userInfo) {
        return database.contains(userInfo);
    }

    private void test() {
        insert(new User("Peter", "123456"));
        insert(new User("Pigger", "654321"));
        insert(new User("Pizza", "qwerty"));
    }
}

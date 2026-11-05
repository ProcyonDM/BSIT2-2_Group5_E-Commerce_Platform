/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.eccomerceplatform;

import java.util.HashMap;

public class SessionManager {
    private static final HashMap<String, User> sessions = new HashMap<>();
    private static int counter = 0;

    public static String login(User user) {
        counter++;
        String sessionId = "S" + counter + "-U" + user.getId();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public static User getUser(String sessionId) {
        return sessions.get(sessionId);
    }

    public static void logout(String sessionId) {
        sessions.remove(sessionId);
    }

    public static int activeCount() {
        return sessions.size();
    }
}


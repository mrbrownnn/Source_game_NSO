
package com.nsoz.server;

import com.nsoz.model.Char;
import com.nsoz.model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author PC
 */
public class ServerManager {

    public static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Char> chars = new ArrayList<>();
    private static ArrayList<String> ips = new ArrayList<>();
    public static HashMap<String, Integer> countAttendanceByIp = new HashMap<>();
    public static HashMap<String, Integer> countUseGiftCodeByIp = new HashMap<>();
    private static ReadWriteLock lockChar = new ReentrantReadWriteLock();
    private static ReadWriteLock lockSession = new ReentrantReadWriteLock();
    private static ReadWriteLock lockUser = new ReentrantReadWriteLock();

    public static List<Char> getChars() {
        return (List<Char>) chars.clone();
    }

    public static List<User> getUsers() {
        return (List<User>) users.clone();
    }

    public static int getNumberOnline() {
        return chars.size();
    }

    public static int frequency(String ip) {
        lockSession.readLock().lock();
        try {
            return Collections.frequency(ips, ip);
        } finally {
            lockSession.readLock().unlock();
        }
    }

    public static void add(String ip) {
        lockSession.writeLock().lock();
        try {
            ips.add(ip);
        } finally {
            lockSession.writeLock().unlock();
        }
    }

    public static void remove(String ip) {
        lockSession.writeLock().lock();
        try {
            ips.remove(ip);
        } finally {
            lockSession.writeLock().unlock();
        }
    }

    public static User findUserByUserID(int id) {
        lockUser.readLock().lock();
        try {
            for (User user : users) {
                if (user.id == id) {
                    return user;
                }
            }
        } finally {
            lockUser.readLock().unlock();
        }
        return null;
    }

    public static long count(long id) {
        lockUser.readLock().lock();
        try {
            return users.stream().filter(u -> u.id == id).count();
        } finally {
            lockUser.readLock().unlock();
        }
    }

    public static User findUserByUsername(String username) {
        lockUser.readLock().lock();
        try {
            for (User user : users) {
                if (user.username.equals(username)) {
                    return user;
                }
            }
        } finally {
            lockUser.readLock().unlock();
        }
        return null;

    }

    public static Char findCharById(int id) {
        lockChar.readLock().lock();
        try {
            for (Char _char : chars) {
                if (_char.id == id) {
                    return _char;
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return null;

    }

    public static Char findCharByName(String name) {
        lockChar.readLock().lock();
        try {
            for (Char _char : chars) {
                if (_char != null && !_char.isCleaned && _char.name.equals(name)) {
                    if (_char.clone != null && !_char.clone.isNhanBan) {
                        return _char.clone;
                    } else {
                        return _char;
                    }
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return null;
    }

    public static void addUser(User user) {
        lockUser.writeLock().lock();
        try {
            users.add(user);
        } finally {
            lockUser.writeLock().unlock();
        }
    }

    public static void addChar(Char _char) {
        lockChar.writeLock().lock();
        try {
            chars.add(_char);
        } finally {
            lockChar.writeLock().unlock();
        }
    }

    public static void removeUser(User user) {
        lockUser.writeLock().lock();
        try {
            users.removeIf(u -> {
                if (u.id == user.id) {
                    return true;
                }
                return false;
            });
        } finally {
            lockUser.writeLock().unlock();
        }
    }

    public static void removeChar(Char _char) {
        lockChar.writeLock().lock();
        try {
            chars.removeIf(c -> {
                if (c.id == _char.id) {
                    return true;
                }
                return false;
            });
        } finally {
            lockChar.writeLock().unlock();
        }
    }
}

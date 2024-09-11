
package com.nsoz.party;

import com.nsoz.map.world.World;
import com.nsoz.map.zones.NymozCave;
import com.nsoz.model.Char;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 *
 * @author PC
 */
public class Group {

    public boolean isLock;
    public ArrayList<MemberGroup> memberGroups;
    public boolean isOpenPB;
    @Getter
    private GroupService groupService;
    private ReadWriteLock lock;

    public Group() {
        this.isLock = false;
        this.groupService = new GroupService(this);
        this.memberGroups = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public boolean isLeader(int id) {
        return memberGroups.get(0).charId == id;
    }

    public int getNumberMember() {
        lock.readLock().lock();
        try {
            return memberGroups.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void removeParty(int index) {
        lock.writeLock().lock();
        try {
            this.memberGroups.remove(index);
        } finally {
            lock.writeLock().unlock();
        }
        groupService.playerInParty();
        if (index == 0) {
            changeLeader(0);
        }
    }

    public MemberGroup findMember(int id) {
        lock.readLock().lock();
        try {
            for (MemberGroup memberGroup : memberGroups) {
                if (memberGroup.charId == id) {
                    return memberGroup;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }

    public boolean isCheckLeader(Char _char) {
        return getIndexById(_char.id) == 0;
    }

    public int getIndexById(int id) {
        lock.readLock().lock();
        try {
            int size = this.memberGroups.size();
            for (int i = 0; i < size; i++) {
                MemberGroup p = this.memberGroups.get(i);
                if (p != null && p.charId == id) {
                    return i;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return -1;
    }

    public List<MemberGroup> getMemberGroup() {
        lock.readLock().lock();
        try {
            return this.memberGroups.stream().collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void changeLeader(int index) {
        MemberGroup party = null;
        lock.readLock().lock();
        try {
            if (memberGroups.size() > 0) {
                party = this.memberGroups.get(index);
            }
        } finally {
            lock.readLock().unlock();
        }
        if (party != null) {
            try {
                MemberGroup party2 = this.memberGroups.get(0);
                lock.writeLock().lock();
                try {
                    this.memberGroups.set(0, party);
                    this.memberGroups.set(index, party2);
                } finally {
                    lock.writeLock().unlock();
                }
                groupService.changeLeader(index);
            } catch (Exception ex) {
                Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<Char> getCharsInMap(int mapId) {
        Vector<Char> list = new Vector<>();
        List<Char> chars = getChars();
        for (Char _char : chars) {
            if (_char != null && _char.mapId == mapId) {
                list.add(_char);
            }
        }
        return list;
    }

    public List<Char> getCharsInZone(int mapId, int zoneId) {
        Vector<Char> list = new Vector<>();
        List<Char> inMap = getCharsInMap(mapId);
        for (Char _char : inMap) {
            if (_char != null && _char.zone.id == zoneId) {
                list.add(_char);
            }
        }
        return list;
    }

    public List<Char> getChars() {
        Vector<Char> list = new Vector<>();
        lock.readLock().lock();
        try {
            for (MemberGroup party : memberGroups) {
                Char _char = party.getChar();
                if (_char != null) {
                    list.add(_char);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return list;
    }

    public void addWorld(World world) {
        lock.readLock().lock();
        try {
            for (MemberGroup p : this.memberGroups) {
                p.setWorld(world);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setNymozCave(NymozCave nymozCave) {
        lock.readLock().lock();
        try {
            for (MemberGroup p : this.memberGroups) {
                p.setNymozCave(nymozCave);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void add(MemberGroup party) {
        if (memberGroups.size() < 6) {
            lock.writeLock().lock();
            try {
                memberGroups.add(party);
            } finally {
                lock.writeLock().unlock();
            }
            groupService.playerInParty();
        }
    }

}

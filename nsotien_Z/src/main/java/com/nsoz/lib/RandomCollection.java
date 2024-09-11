package com.nsoz.lib;

import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) {
            return this;
        }
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public HashMap<E, Integer> test(int times) {
        HashMap<E, Integer> hashmap = new HashMap<>();
        for (int i = 0; i < times; i++) {
            E value = next();
            if (hashmap.containsKey(value)) {
                int quantity = hashmap.get(value);
                hashmap.put(value, quantity + 1);
            } else {
                hashmap.put(value, 1);
            }
        }
        return hashmap;
    }
}

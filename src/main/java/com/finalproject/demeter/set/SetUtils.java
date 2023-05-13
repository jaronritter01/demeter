package com.finalproject.demeter.set;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {
    /**
     * Set Intersection Operation.
     * @param firstSet set 1.
     * @param secondSet set 2.
     * @return A new Set containing the items common to both sets.
     * */
    public static <T> Set<T> intersection(Set<T> firstSet, Set<T> secondSet) {
        Set<T> returnSet = new HashSet<>();
        Set<T> smallerSet = firstSet.size() > secondSet.size() ? secondSet : firstSet;
        Set<T> largerSet = firstSet.size() > secondSet.size() ? firstSet : secondSet;

        for (T item : smallerSet) {
            if (largerSet.contains(item)) {
                returnSet.add(item);
            }
        }

        return returnSet;
    }

    /**
     * Set Difference Operation.
     * @param firstSet set 1.
     * @param secondSet set 2.
     * @return A new Set containing the items only in the first set.
     * */
    public static <T> Set<T> difference(Set<T> firstSet, Set<T> secondSet) {
        Set<T> returnSet = new HashSet<>(firstSet);
        returnSet.removeAll(secondSet);
        return returnSet;
    }

    /**
     * Set Union Operation.
     * @param firstSet set 1.
     * @param secondSet set 2.
     * @return A new Set containing all the unique items between the sets.
     * */
    public static <T> Set<T> union(Set<T> firstSet, Set<T> secondSet) {
        Set<T> smallerSet = firstSet.size() > secondSet.size() ? secondSet : firstSet;
        Set<T> largerSet = firstSet.size() > secondSet.size() ? firstSet : secondSet;

        Set<T> returnSet = new HashSet<>(largerSet);

        for (T item : smallerSet) {
            if (!largerSet.contains(item)) {
                returnSet.add(item);
            }
        }

        return returnSet;
    }
}

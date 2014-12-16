package edu.depaul.csc472.kuraszj_fieldme;

import java.util.Comparator;

/**
 * Created by jasekurasz on 11/20/14.
 */
public class ParkComparator implements Comparator<Park> {

    @Override
    public int compare(Park p1, Park p2) {
        if (p1.getDistToLoc() > p2.getDistToLoc())
            return 1;
        else if (p1.getDistToLoc() < p2.getDistToLoc())
            return -1;
        else
            return 0;
    }
}

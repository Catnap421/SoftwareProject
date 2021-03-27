package hw2;

import java.util.*;

/**
 * This class stores the basic state necessary for the A* algorithm to compute a
 * path across a map.  This state includes a collection of "open waypoints" and
 * another collection of "closed waypoints."  In addition, this class provides
 * the basic operations that the A* pathfinding algorithm needs to perform its
 * processing.
 **/
public class AStarState
{
    /** This is a reference to the map that the A* algorithm is navigating. **/
    private Map2D map;
    private Map<Location, Waypoint> openWaypoints;
    private Map<Location, Waypoint> closedWaypoints;

    /**
     * Initialize a new state object for the A* pathfinding algorithm to use.
     **/
    public AStarState(Map2D map)
    {
        if (map == null)
        throw new NullPointerException("map cannot be null");

        this.map = map;
        this.openWaypoints = new HashMap<>();
        this.closedWaypoints = new HashMap<>();
    }

    /** Returns the map that the A* pathfinder is navigating. **/
    public Map2D getMap()
    {
        return map;
    }

    /**
     * This method scans through all open waypoints, and returns the waypoint
     * with the minimum total cost.  If there are no open waypoints, this method
     * returns <code>null</code>.
     **/
    public Waypoint getMinOpenWaypoint()
    {
        if(numOpenWaypoints() == 0)
            return null;

        List<Map.Entry<Location, Waypoint>> entries = new LinkedList<>(openWaypoints.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Location, Waypoint>>() {
            @Override
            public int compare(Map.Entry<Location, Waypoint> o1, Map.Entry<Location, Waypoint> o2) {
                return o1.getValue().getTotalCost() - o2.getValue().getTotalCost() < 0 ? -1 : 1;
            }
        });

        for(Map.Entry<Location, Waypoint> entry : entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());

        }

        return entries.get(0).getValue();

    }

    /**
     * This method adds a waypoint to (or potentially updates a waypoint already
     * in) the "open waypoints" collection.  If there is not already an open
     * waypoint at the new waypoint's location then the new waypoint is simply
     * added to the collection.  However, if there is already a waypoint at the
     * new waypoint's location, the new waypoint replaces the old one ONLY
     * IF the new waypoint's "previous cost" value is less than the current
     * waypoint's "previous cost" value.
     **/
    public boolean addOpenWaypoint(Waypoint newWP)
    {
        Waypoint current = openWaypoints.get(newWP.getLocation());
        if (current != null && current.getPreviousCost() < newWP.getPreviousCost()) {
            return false;
        }
        openWaypoints.put(newWP.getLocation(), newWP);

        return true;
    }


    /** Returns the current number of open waypoints. **/
    public int numOpenWaypoints()
    {
        return openWaypoints.size();
    }


    /**
     * This method moves the waypoint at the specified location from the
     * open list to the closed list.
     **/
    public void closeWaypoint(Location loc)
    {
        Waypoint removed = openWaypoints.remove(loc);
        closedWaypoints.put(loc, removed);
    }

    /**
     * Returns true if the collection of closed waypoints contains a waypoint
     * for the specified location.
     **/
    public boolean isLocationClosed(Location loc)
    {
        if(closedWaypoints.containsKey(loc)) return true;

        return false;
    }
}

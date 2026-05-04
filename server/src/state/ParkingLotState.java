package state;

import java.util.HashMap;
import java.util.Map;

public class    ParkingLotState {
    private final Map<Integer, SpotState> spotStates = new HashMap<>();

    public ParkingLotState(int totalSpaces) {
        for (int i = 1; i <= totalSpaces; i++) {
            spotStates.put(i, SpotState.UNKNOWN);
        }
    }

    public synchronized void setSpotState(int spotId, SpotState state) {
        spotStates.put(spotId, state);
    }

    public synchronized SpotState getSpotState(Integer spotId) {
        return spotStates.get(spotId);
    }

    public synchronized int getTotalSpaces() {
        return spotStates.size();
    }

    public synchronized int getFreeSpaces() {
        int free = 0;
        for (SpotState state : spotStates.values()) {
            if (state == SpotState.FREE) free++;
        }
        return free;
    }
}

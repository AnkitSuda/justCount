package app.androidgrid.justcount;

/**
 * Created by SUDA on 13-09-2017.
 */

public class LapModel {

    public String lapPosition;
    public String lapTime;
    public String lapPlusTime;

    public LapModel() {

    }

    public LapModel(String lapPosition, String lapTime, String lapPlusTime) {
        this.lapPosition = lapPosition;
        this.lapTime = lapTime;
        this.lapPlusTime = lapPlusTime;
    }

    public String getLapPosition() {
        return lapPosition;
    }

    public void setLapPosition(String lapPosition) {
        this.lapPosition = lapPosition;
    }

    public String getLapTime() {
        return lapTime;
    }

    public void setLapTime(String lapTime) {
        this.lapTime = lapTime;
    }

    public String getLapPlusTime() {
        return lapPlusTime;
    }

    public void setLapPlusTime(String lapPlusTime) {
        this.lapPlusTime = lapPlusTime;
    }
}

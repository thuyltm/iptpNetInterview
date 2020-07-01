import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class DcTrackingTime {
    String dcName;
    String dataCenter;
    List<IntervalWT> workListTime;
    List<IntervalWT> noWorkingTime;

    public DcTrackingTime(String dcName, String dataCenter, LocalDateTime earlyStart, LocalDateTime lateEnd) {
        this.dcName = dcName;
        this.dataCenter = dataCenter;
        this.workListTime = new ArrayList<>();
        workListTime.add(new IntervalWT(earlyStart, lateEnd));
    }

    public void addWorkList(LocalDateTime startTime, LocalDateTime endTime) {
        int i = 0;
        int size = workListTime.size();
        while (i < size) {
            IntervalWT boundTime = workListTime.get(i);
            LocalDateTime boundStartTime = boundTime.getStartTime();
            LocalDateTime boundEndTime = boundTime.getEndTime();
            if (startTime.isAfter(boundStartTime) && endTime.isBefore(boundEndTime)) {
                return;
            }
            if (startTime.compareTo(boundStartTime) == 0 && endTime.compareTo(boundEndTime) == 0) {
                return;
            }
            if (startTime.isAfter(boundStartTime) && startTime.isBefore(boundEndTime)) {
                workListTime.remove(i);
                workListTime.add(i, new IntervalWT(boundStartTime, endTime));
                return;
            }
            if (endTime.isAfter(boundStartTime) && endTime.isBefore(boundEndTime)) {
                workListTime.remove(i);
                workListTime.add(i, new IntervalWT(startTime, boundEndTime));
                workListTime.sort((x,y)->x.startTime.compareTo(y.startTime));
                return;
            }
            i++;
        }
        workListTime.add(new IntervalWT(startTime, endTime));
        workListTime.sort((x,y)->x.startTime.compareTo(y.startTime));
    }

    public List<IntervalWT> getWorkListTime() {
        return workListTime;
    }

    public String getDcName() {
        return dcName;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public void createNonWorkListTime() {
        int length = workListTime.size();
        if (length == 1) {
            return;
        }
        this.noWorkingTime = new ArrayList<>();
        for (int i = 0; i < length - 1; i++) {
            noWorkingTime.add(new IntervalWT(workListTime.get(i).getEndTime(), workListTime.get(i+1).getStartTime()));
        }
    }


}

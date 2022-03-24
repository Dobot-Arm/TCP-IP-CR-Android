package cc.dobot.crtcpdemo.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by x on 2018/8/6.
 */

public class AlarmData {
    String id = "";
    String type = "";
    String level = "";
    String reason = "";
    String solution = "";

    public AlarmData() {
    }

    public AlarmData(String id) {
        this.id=id;
    }

    public AlarmData(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public AlarmData(String id, String type, String level, String reason, String solution) {
        this.id = id;
        this.type = type;
        this.level = level;
        this.reason = reason;
        this.solution = solution;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String[] getStringArrays() {
        return new String[]{
                id,
                type,
                level,
                reason,
                solution
        };
    }

    @Override
    public String toString() {
        return "TimeStamp=" +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"\n"+
                "id=" + id + '\n' +
                "type=" + type + '\n' +
                "level=" + level + '\n' +
                "reason=" + reason + '\n' +
                "solution=" + solution + '\n';
    }
}

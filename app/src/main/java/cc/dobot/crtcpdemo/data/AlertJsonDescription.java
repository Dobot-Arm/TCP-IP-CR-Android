package cc.dobot.crtcpdemo.data;

/**
 * Created by x on 2018/8/7.
 */

public class AlertJsonDescription {
    String description;
    String cause;
    String solution;

    public AlertJsonDescription() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}

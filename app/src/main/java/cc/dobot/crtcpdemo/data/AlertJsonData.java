package cc.dobot.crtcpdemo.data;

/**
 * Created by x on 2018/8/7.
 */

public class AlertJsonData {
    String id;
    String level;
    AlertJsonDescription en;
    AlertJsonDescription zh_CN;

    public AlertJsonData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public AlertJsonDescription getEn() {
        return en;
    }

    public void setEn(AlertJsonDescription en) {
        this.en = en;
    }

    public AlertJsonDescription getZh_CN() {
        return zh_CN;
    }

    public void setZh_CN(AlertJsonDescription zh_CN) {
        this.zh_CN = zh_CN;
    }
}

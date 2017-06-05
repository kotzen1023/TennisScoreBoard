package com.seventhmoon.tennisscoreboard.Data;

public class CurrentStatItem {
    private String title;
    private String statUp;
    private String statDown;
    private int valueUp;
    private int valueDown;

    public CurrentStatItem(String title, String statUp, String statDown, int valueUp, int valueDown)
    {
        super();
        this.title = title;
        this.statUp = statUp;
        this.statDown = statDown;
        this.valueUp = valueUp;
        this.valueDown = valueDown;
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatUp() {
        return statUp;
    }

    public void setStatUp(String statUp) {
        this.statUp = statUp;
    }

    public String getStatDown() {
        return statDown;
    }

    public void setStatDown(String statDown) {
        this.statDown = statDown;
    }

    public int getValueUp() {
        return valueUp;
    }

    public void setValueUp(int valueUp) {
        this.valueUp = valueUp;
    }

    public int getValueDown() {
        return valueDown;
    }

    public void setValueDown(int valueDown) {
        this.valueDown = valueDown;
    }
}

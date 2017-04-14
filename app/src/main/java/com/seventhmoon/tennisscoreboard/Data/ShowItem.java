package com.seventhmoon.tennisscoreboard.Data;



public class ShowItem {
    private String textTitle;
    private String textShow;
    private float floatShow;

    public ShowItem(String textTitle,String textShow, float floatShow)
    {
        super();
        this.textTitle  = textTitle;
        this.textShow = textShow;
        this.floatShow  = floatShow;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getTextShow() {
        return textShow;
    }

    public void setTextShow(String textShow) {
        this.textShow = textShow;
    }

    public float getFloatShow() {
        return floatShow;
    }

    public void setFloatShow(float floatShow) {
        this.floatShow = floatShow;
    }
}

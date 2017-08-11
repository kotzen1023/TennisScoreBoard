package com.seventhmoon.tennisscoreboard.Data;



public class RecordItem {
    private String title;
    private String filename;
    private boolean fileExist;
    private boolean selected;

    public RecordItem(String title, String filename) {
        this.title = title;
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isFileExist() {
        return fileExist;
    }

    public void setFileExist(boolean fileExist) {
        this.fileExist = fileExist;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

package ai.chadda.myruns_4.model;

public class ManualEntryModel {
    private String title;
    private String data;

    public ManualEntryModel(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) { this.data = data; }
}

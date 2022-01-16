package com.future.bean;

/**
 * @author ：shenmegui
 * @date ：Created in 2021/12/13 10:12
 */
public class Event {
    private int id;
    private String endDate;
    private String content;

    public Event() {

    }

    public Event(int id, String endDate, String content) {
        this.id = id;
        this.endDate = endDate;
        this.content = content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Event{");
        sb.append("id=").append(id);
        sb.append(", endDate=").append(endDate);
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

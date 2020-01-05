package sysRestaurante.etc;

public class LogData {
    private int idLog;
    private int userId;
    private int dateLog;
    private String content;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDateLog() {
        return dateLog;
    }

    public void setDateLog(int dateLog) {
        this.dateLog = dateLog;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }
}

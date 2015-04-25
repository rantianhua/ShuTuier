package weike.data;

/**
 * Created by Rth on 2015/3/2.
 */
public class CommentData {
    private String bookId;
    private String content;
    private String sendTime;
    private String uid;

    private static CommentData commentData;

    public static CommentData getInstance() {
        if(commentData == null) {
            commentData = new CommentData();
        }
        return commentData;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public static  void clear() {
        if(commentData != null) {
            commentData = null;
        }
    }
}

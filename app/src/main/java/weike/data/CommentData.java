package weike.data;

/**
 * Created by Rth on 2015/3/2.
 */
public class CommentData {
    private int bookId;
    private String content;
    private String sendTime;

    private static CommentData commentData;

    public static CommentData getInstance() {
        if(commentData == null) {
            commentData = new CommentData();
        }
        return commentData;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
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

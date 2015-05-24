package weike.data;

/**
 * Created by Rth on 2015/3/28.
 * 该类收集用户发布书籍时的信息
 */
public class CommitBookData {

    private String bookName;
    private String bookAuthor;
    private String publisher;
    private String category;
    private String bookNumber;
    private int howOld;
    private String oPrice;
    private String sPrice;
    private String status;
    private String sendCondition;
    private String description;
    //private String remark;
    private String coverUrl;
    private String isbn;
    private String uid;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.bookAuthor);
        builder.append("--");
        builder.append(this.bookName);
        builder.append("--");
        builder.append(this.publisher);
        builder.append("--");
        builder.append(this.category);
        builder.append("--");
        builder.append(this.bookNumber);
        builder.append("--");
        builder.append(this.howOld);
        builder.append("--");
        builder.append(this.oPrice);
        builder.append("--");
        builder.append(this.sPrice);
        builder.append("--");
        builder.append(this.status);
        builder.append("--");
        builder.append(this.sendCondition);
        builder.append("--");
        builder.append(this.description);
//        builder.append("--");
//        builder.append(this.remark);
        builder.append("--");
        builder.append(this.coverUrl);
        builder.append("--");
        builder.append(this.isbn);
        return builder.toString();
    }

    private static CommitBookData commitBookData = null;

    public String getBookName() {
        if(bookName == null) {
            return "";
        }else {
            return bookName;
        }
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        if(bookAuthor == null) {
            return "";
        }else {
            return bookAuthor;
        }
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getPublisher() {
        if(publisher == null) {
            return "";
        }else {
            return publisher;
        }
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        if(category == null) {
            return "";
        }else {
            return category;
        }
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getBookNumber() {
        return bookNumber == null ? "" : this.bookNumber;
    }

    public int getHowOld() {
        return howOld;
    }

    public void setHowOld(int howOld) {
        this.howOld = howOld;
    }

    public String getoPrice() {
        if(oPrice == null) {
            return "";
        }else {
            return oPrice;
        }
    }

    public void setoPrice(String oPrice) {
        this.oPrice = oPrice;
    }

    public String getsPrice() {
        if(sPrice == null) {
            return "";
        }else {
            return sPrice;
        }
    }

    public void setsPrice(String sPrice) {
        this.sPrice = sPrice;
    }

    public String getStatus() {
        if(status == null) {
            return "";
        }else {
            return status;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendCondition() {
        if(sendCondition == null) {
            return "";
        }else {
            return sendCondition;
        }
    }

    public void setSendCondition(String sendCondition) {
        this.sendCondition = sendCondition;
    }

    public String getDescription() {
        if(description == null) {
            return "";
        }else {
            return description;
        }
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getIsbn() {
        if(isbn == null) {
            return "";
        }else {
            return isbn;
        }
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public static CommitBookData getInstance() {
        if(commitBookData == null) {
            commitBookData = new CommitBookData();
        }
        return commitBookData;
    }


    public static void clear() {
        if(commitBookData != null) {
            commitBookData = null;
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}

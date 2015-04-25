package weike.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rth on 2015/2/28.
 */
public class BookOtherData {

    private String headUrl;
    private String ISBN;
    private String ownerName;
    private String wxNumber;
    private String qqNumber;
    private String teleNumber;
    private String mail;
    private String shareNumber;
    private String markNumber;

    private List<Map<String,String>> list;  //评论列表

    private static BookOtherData data = null;

    private BookOtherData() {
        list = new ArrayList<>();
    }

    public String getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(String shareNumber) {
        this.shareNumber = shareNumber;
    }

    public String getMarkNumber() {
        return markNumber;
    }

    public void setMarkNumber(String markNumber) {
        this.markNumber = markNumber;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getWxNumber() {
        return wxNumber;
    }

    public void setWxNumber(String wxNumber) {
        this.wxNumber = wxNumber;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getTeleNumber() {
        return teleNumber;
    }

    public void setTeleNumber(String teleNumber) {
        this.teleNumber = teleNumber;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Map<String, String>> getList() {
        return list;
    }

    public void setList(List<Map<String, String>> list) {
        this.list = list;
    }

    public static BookOtherData getInstance(){
        if(data == null) {
            data = new BookOtherData();
        }
        return data;
    }

}

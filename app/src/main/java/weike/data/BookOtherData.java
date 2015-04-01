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

    private List<Map<String,String>> list;  //评论列表

    private static BookOtherData data = null;

    private BookOtherData() {
        list = new ArrayList<>();
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

package weike.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rth on 2015/5/5.
 */
public class BookMessageListData {

    private static  BookMessageListData messageListData;

    private List<MessageBookData> list;

    private BookMessageListData() {
        list = new ArrayList<>();
    }

    public static BookMessageListData getInstance() {
        if(messageListData == null) {
            messageListData = new BookMessageListData();
        }
        return messageListData;
    }

    public List<MessageBookData> getList() {
        return list;
    }
}

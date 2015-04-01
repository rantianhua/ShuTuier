package weike.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weike.util.Constants;

/**
 * Created by Rth on 2015/2/23.
 */
public class ListBookData {

    private  List<BookItem> list; //每个item的数据源
    private static ListBookData data_1,data_2,data_3,data_4,data_5;   //5个单一实例


    private ListBookData() {
        list = new ArrayList<>();
    }

    //根据类型返回不同的静态变量
    public static ListBookData getInstance(final String type) {
        ListBookData data = null;
        switch (type) {
            case Constants.TYPE_1:
                if(data_1 == null) {
                    data_1 = new ListBookData();
                }
                data = data_1;
                break;
            case Constants.TYPE_2:
                if(data_2 == null) {
                    data_2 = new ListBookData();
                }
                data = data_2;
                break;
            case Constants.TYPE_3:
                if(data_3 == null) {
                    data_3 = new ListBookData();
                }
                data =  data_3;
                break;
            case Constants.TYPE_4:
                if(data_4 == null) {
                    data_4 = new ListBookData();
                }
                data = data_4;
                break;
            case Constants.TYPE_5:
                if(data_5 == null) {
                    data_5 = new ListBookData();
                }
                data = data_5;
                break;
        }
        return data;
    }

    public List<BookItem> getList() {
        return list;
    }

    //向list中添加记录，若list中已有要添加的记录，则用新的替换掉
    public void addItems(BookItem item) {
        if(item != null) {
            Map<String,Object> map = getBookItem(item.getId());
            if(map != null) {
                int i = (int)map.get("position");
                list.remove(i);
                list.add(i, item);
                map = null;
            }else{
                list.add(item);
            }
        }
    }

    public Map<String,Object> getBookItem(int id) {
        int i = -1;
        for(BookItem item : list) {
            i ++;
            if(item.getId() == id){
                Map<String,Object> result = new HashMap<>();
                result.put("item",item);
                result.put("position",i);
                return result;
            }
        }
        return null;
    }
}

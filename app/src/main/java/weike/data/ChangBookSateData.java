package weike.data;

/**
 * Created by Rth on 2015/4/10.
 */
public class ChangBookSateData {

    private static ChangBookSateData data = null;

    private String id;
    private String close;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public static ChangBookSateData getInstance() {
        if(data == null) {
            data = new ChangBookSateData();
        }
        return data;
    }

    public static void clear(){
        if(data != null) {
            data = null;
        }
    }
}

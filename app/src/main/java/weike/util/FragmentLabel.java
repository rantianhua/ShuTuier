package weike.util;

/**
 * Created by Rth on 2015/4/15.
 */
public enum FragmentLabel {
    Home("首页"),
    Commit("发布"),
    Message("消息"),
    Swipe("扫一扫"),
    Setting("设置");

    private String value = null;

    private FragmentLabel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

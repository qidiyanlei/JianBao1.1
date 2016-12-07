package bgs.com.jianbao11.bean;

/**
 * Created by 醇色 on 2016/11/30.
 */

public class Info_shouyel {
    private String Ltitle;
    private String Lprice;
    private String user_icon_url;
    private int tag;
    private String Limg_url;

    public Info_shouyel(String ltitle, String lprice, String user_icon_url, int tag, String limg_url) {
        Ltitle = ltitle;
        Lprice = lprice;
        this.user_icon_url = user_icon_url;
        this.tag = tag;
        Limg_url = limg_url;
    }

    public String getLtitle() {
        return Ltitle;
    }

    public void setLtitle(String ltitle) {
        Ltitle = ltitle;
    }

    public String getLprice() {
        return Lprice;
    }

    public void setLprice(String lprice) {
        Lprice = lprice;
    }

    public String getUser_icon_url() {
        return user_icon_url;
    }

    public void setUser_icon_url(String user_icon_url) {
        this.user_icon_url = user_icon_url;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getLimg_url() {
        return Limg_url;
    }

    public void setLimg_url(String limg_url) {
        Limg_url = limg_url;
    }
}

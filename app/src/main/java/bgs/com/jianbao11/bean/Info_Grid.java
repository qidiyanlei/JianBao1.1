package bgs.com.jianbao11.bean;

/**
 * Created by 醇色 on 2016/11/28.
 */

public class Info_Grid {
    private String title;
    private String img_url;
    private String price;
    private int tag;

    public Info_Grid(String title, String img_url, String price, int tag) {
        this.title = title;
        this.img_url = img_url;
        this.price = price;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}

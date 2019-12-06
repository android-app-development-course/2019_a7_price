package cn.itcast.price_search5;

/**
 * Created by xiaomo on 2019/12/4.
 */
public class commodity {
    private int imgId;
    private byte[] bitmapByte;
    private String title;
    private String price;

    public commodity(byte[] bitmapByte,String title,String price,int imgId){
        this.bitmapByte=bitmapByte;
        this.title = title;
        this.price = price;
        this.imgId = imgId;
    }

    public int getImgId(){
        return imgId;
    }

    public String getTitle(){
        return title;
    }

    public String getPrice(){
        return price;
    }

    public byte[] getBitmapByte(){return bitmapByte;}
}

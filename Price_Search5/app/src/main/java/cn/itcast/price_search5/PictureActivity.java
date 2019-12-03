package cn.itcast.price_search5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class PictureActivity extends AppCompatActivity {
    ImageView img_receive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        img_receive = (ImageView)findViewById(R.id.img_receive);
        Intent intent = getIntent();
        if(intent !=null)
        {
            byte [] bis=intent.getByteArrayExtra("bitmap");
            Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
            img_receive.setImageBitmap(bitmap);
        }
    }
}

package cn.itcast.price_search5;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class PictureActivity extends AppCompatActivity {
    ImageView img_receive;
    TextView tv_title,tv_price;
    byte [] bis;
    String title;
    String price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        img_receive = (ImageView)findViewById(R.id.img_receive);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_price=(TextView)findViewById(R.id.tv_price);
        Intent intent = getIntent();
        if(intent !=null)
        {
            bis=intent.getByteArrayExtra("bitmap");
            title=intent.getStringExtra("title");
            price=intent.getStringExtra("price");
            Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
            img_receive.setImageBitmap(bitmap);
            tv_title.setText(title);
            tv_price.setText(price);
        }
    }
}

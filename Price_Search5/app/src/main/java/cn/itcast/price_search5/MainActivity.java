package cn.itcast.price_search5;


//服务器端为price2,实现先传照片大小再传照片，但bitmap还是为空
//此次尝试加上转码处理再生成bitmap,可以成功生成图片，但很模糊
        import android.animation.Animator;
        import android.animation.AnimatorListenerAdapter;
        import android.animation.AnimatorSet;
        import android.animation.ObjectAnimator;
        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.util.DisplayMetrics;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.Socket;
        import com.facebook.shimmer.ShimmerFrameLayout;

        import cn.itcast.price_search5.view.SlideMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private long exitTime;
    private ImageView btn_back;
    private SlideMenu slideMenu;
    EditText et_send;
    TextView  textView3,textView1;
    Button bt_search1,bt_search2;
    ImageView img_receive;
    String IP="129.204.195.246";
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();
        //点击返回键打开或关闭Menu
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideMenu.switchMenu();
            }
        });
        //标题动炫效果
        ShimmerFrameLayout mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        mShimmerViewContainer.useDefaults();//必须设置

        //style
        mShimmerViewContainer.setDuration(10000);
        mShimmerViewContainer.startShimmerAnimation();
        //监听查询按钮
        et_send = (EditText)findViewById(R.id.et_send);
        bt_search1 =(Button) findViewById(R.id.bt_search1);
        bt_search1.setOnClickListener(this);
        bt_search2 =(Button) findViewById(R.id.bt_search2);
        bt_search2.setOnClickListener(this);

        img_receive = (ImageView)findViewById(R.id.img_receive);
        //handler传递信息到主线程
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.what == 0x001) {
                    //tv_receive.setText(msg.obj.toString());
                    Toast.makeText(MainActivity.this, "凉了,该商品没有被收录", Toast.LENGTH_SHORT).show();
                }
            }
        };


    }
    /**
     * 初始化View控件
     */
    private void initViews() {
        btn_back = (ImageView)findViewById(R.id.btn_back);
        slideMenu = (SlideMenu)findViewById(R.id.slideMenu);
        textView3=(TextView)findViewById(R.id.textView3);
        textView1=(TextView)findViewById(R.id.textView1);
    }

    /**
     * 重写返回键，实现双击退出效果
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 发送链接、接受图片
     */
    public void sendandreceive(String sendMessage) {
        final String sendMessage1=sendMessage;
        if(sendMessage1.equals("")) {
            Toast.makeText(MainActivity.this, "发送信息不能为空", Toast.LENGTH_SHORT).show();
        } else {

            new Thread() {
                public void run() {
                    try {
                        Socket s = new Socket(IP, 8000);
                        OutputStream os = s.getOutputStream();
                        os.write((sendMessage1 + "\r\n").getBytes());
                        os.flush();
                        InputStream inputStream = s.getInputStream();
//begin 图片接收s
//第1步：接收服务端发过来的图片大小
                        byte[] picLenBuff = new byte[200];
                        int picLen1 = inputStream.read(picLenBuff);
//将String 转换成 int
                        String picLenString = new String(picLenBuff, 0, picLen1);
                        if(picLenString.equals("no")){
                            Message msg = new Message();
                            msg.what = 0x001;
                            handler.sendMessage(msg);
                            return;
                        }
                        else {

                            int getPicLen = Integer.valueOf(picLenString);

//第2步：回馈。向服务端反馈客户端已经接收到图片的大小了，可以开始发图片了
                            OutputStream outputStream = s.getOutputStream();
                            String string = "ok";
                            outputStream.write(string.getBytes());
                            outputStream.flush();
//第3步：开始接收图片
                            int offset = 0;//定义偏移量
                            byte[] bitmapBuff = new byte[getPicLen];//初始化图片缓存
                            int len;
//********************最主要部分**********************
                            while (offset < getPicLen) {
                                if (getPicLen - offset <= 1024) {
                                    len = inputStream.read(bitmapBuff, offset, getPicLen - offset);
                                } else {
                                    len = inputStream.read(bitmapBuff, offset, 1024);
                                }
                                offset += len;
                                if (len == -1) {
                                    break;
                                }
                            }
//用intent跳转activtity显示图片
                            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBuff, 0, offset);
                            s.close();
                            ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, outPut);
                            byte[] bitmapByte = outPut.toByteArray();
                            Intent intent = new Intent(MainActivity.this, PictureActivity.class);
                            intent.putExtra("bitmap", bitmapByte);
                            startActivity(intent);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bt_search1:
                String sendMessage1 = et_send.getText().toString();
                sendandreceive(sendMessage1);
                break;
            case R.id.bt_search2:
                String sendMessage2=bt_search2.getText().toString();
                sendandreceive(sendMessage2);
                break;

            default:
        }
    }
}


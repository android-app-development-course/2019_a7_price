package cn.itcast.price_search5;


//服务器端为price2,实现先传照片大小再传照片，但bitmap还是为空
//此次尝试加上转码处理再生成bitmap,可以成功生成图片，但很模糊
        import android.animation.Animator;
        import android.animation.AnimatorListenerAdapter;
        import android.animation.AnimatorSet;
        import android.animation.ObjectAnimator;
        import android.annotation.SuppressLint;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Base64;
        import android.util.DisplayMetrics;
        import android.view.ContextMenu;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.net.Socket;
        import java.sql.ResultSet;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Date;
        import java.util.List;

        import com.facebook.shimmer.ShimmerFrameLayout;
        import cn.itcast.price_search5.view.SlideMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private long exitTime;
    private ImageView btn_back;
    private SlideMenu slideMenu;
    EditText et_send;
    TextView  textView3,textView1;
    Button bt_search1;
    ImageView img_receive;
    String IP="129.204.195.246";
    Handler handler;

    //数据库
    private MyDatabaseHelper dbHelper;
    private List<commodity> commodityList = new ArrayList<>();
    commodityAdapter adapter;
    ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();//初始化主界面控件
        dbHelper = new MyDatabaseHelper(this,"Commodity10.db",null,1);
        //初始化数据
        initcommodity();
        //测试commodityList的长度，没问题
/*        int listlength=commodityList.size();
        for(int i=0;i<2;++i){
            Toast.makeText(MainActivity.this,String.valueOf(listlength) , Toast.LENGTH_SHORT).show();
        }*/
        adapter = new commodityAdapter(MainActivity.this,
                R.layout.commodity_item,commodityList);
        listView = (ListView) findViewById(R.id.commodityList);
        //为 ListView 的所有 item 注册 ContextMenu
        this.registerForContextMenu(listView);

        listView.setAdapter(adapter);
        //ScrollView里面放ListView就只能显示一条数据，重写listview，效果不好
        //删除scrollview后别忘了注释重写的listview
        //setListViewHeightBasedOnChildren(listView);
        //listview元素的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,
                                    int position,long id){
                commodity cmdy = commodityList.get(position);
                String title = cmdy.getTitle();
                String price = cmdy.getPrice();
                byte[] BitmapByte=cmdy.getBitmapByte();
                Intent intent = new Intent(MainActivity.this,PictureActivity.class);
                intent.putExtra("title",title);
                intent.putExtra("price",price);
                intent.putExtra("bitmap",BitmapByte);
                startActivity(intent);
            }
        });


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

        img_receive = (ImageView)findViewById(R.id.img_receive);
        //handler传递信息到主线程
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if(msg.what == 0x001) {
                    Toast.makeText(MainActivity.this, "凉了,该商品没有被收录", Toast.LENGTH_SHORT).show();
                }
                if(msg.what==0x002){
                    initcommodity();
                    adapter = new commodityAdapter(MainActivity.this,
                            R.layout.commodity_item,commodityList);
                    listView.setAdapter(adapter);
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
    //初始化listview历史记录界面
    private void initcommodity(){
        commodityList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Commodity10",null,null,null,null,null,null);
        int i=0;
        if(cursor.moveToFirst()){
            do{
                //遍历
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String price = cursor.getString(cursor.getColumnIndex("price"));
                String BitmapString=cursor.getString(cursor.getColumnIndex("bitmapString"));
                byte[] BitmapByte=Base64.decode(BitmapString.getBytes(), Base64.DEFAULT);
                commodity cmdy = new commodity(BitmapByte,title,price,i);
                commodityList.add(cmdy);
                i=i+1;
            }while(cursor.moveToNext());
        }
        Collections.reverse(commodityList);
        cursor.close();
    }
/*    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }*/
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
    //实现长按删除功能
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInof)

    {
        super.onCreateContextMenu(menu, view, menuInof);
        menu.add(0,1, Menu.NONE,"删除");
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            case 1:
                //删除列表项...
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                int pos = (int)(info.id);//这里的info.id对应的就是数据库中_id的值
                //int pos=(int)listView.getAdapter().getItemId(menuInfo.position);
                String delete_str=commodityList.get(pos).getTitle();
                if(commodityList.remove(pos)!=null){//这行代码必须有
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    //不知名原因，id总是对不好，所以改成title删除
                    //一开始想着获取listview的text，一直没找到方法，看到这里突然想到通过数据就能获得title，省事多了
/*                    String st=Integer.toString(commodityList.size()-pos);
                    String s="delete from Commodity10 where _id = '"+st+"'"+";";
                    db.execSQL(s);*/
                    String whereClause = "title=?";
                    String[] whereArgs = {delete_str};
                    db.delete("Commodity10",whereClause,whereArgs);
                    db.close();
                    Message msg_ref = new Message();
                    msg_ref.what = 0x002;
                    handler.sendMessage(msg_ref);
                    Toast.makeText(this,"删除成功", Toast.LENGTH_SHORT).show();
                }else {
                    System.out.println("failed");
                }
                adapter.notifyDataSetChanged();//刷新view
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
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
                        String fst = new String(picLenBuff, 0, picLen1);
                        String[] strArr = fst.split("\\^");//特殊字符要加上转义字符“\\”
                        System.out.println(strArr.length);
                        String title=strArr[0];
                        String price=strArr[1];
                        String picLenString=strArr[2];
                        if(fst.equals("no")){
                            Message msg = new Message();
                            msg.what = 0x001;
                            handler.sendMessage(msg);
                            return;
                        }
                        else {
//将String 转换成 int
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
                            String bitmapString=Base64.encodeToString(bitmapByte, Base64.DEFAULT);
                            Intent intent = new Intent(MainActivity.this, PictureActivity.class);
                            intent.putExtra("bitmap", bitmapByte);
                            intent.putExtra("title", title);
                            intent.putExtra("price", price);

                            check(title,price,bitmapString);
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

            default:
        }
    }
    public void check(String title,String price,String bitmapString){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from   Commodity10  where   title=? ",
                new String[] { title });
        if(cursor.moveToNext()==false){
            ContentValues values = new ContentValues();
            values.put("title",title);
            values.put("price",price);
            values.put("bitmapString",bitmapString);
            long index=db.insert("Commodity10",null,values);
            values.clear();
            Message msg_ref = new Message();
            msg_ref.what = 0x002;
            handler.sendMessage(msg_ref);
        }
        db.close();
    }
}


package cn.itcast.price_search5;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;


/**
 * Created by gcj on 2017/11/28
 */
public class StartActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 注意：此处将setContentView()方法注释掉
        // setContentView(R.layout.activity_start);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoLogin();
            }
        }, 2000);
    }

    /**
     * 前往注册、登录主页
     */
    private void gotoLogin() {
        Intent intent = new Intent(StartActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        //取消界面跳转时的动画，使启动页的logo图片与注册、登录主页的logo图片完美衔接
        overridePendingTransition(0, 0);
    }

    /**
     * 屏蔽物理返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //实现只在冷启动时显示启动页，即点击返回键与点击HOME键退出效果一致
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        if (handler != null) {
            //If token is null, all callbacks and messages will be removed.
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}

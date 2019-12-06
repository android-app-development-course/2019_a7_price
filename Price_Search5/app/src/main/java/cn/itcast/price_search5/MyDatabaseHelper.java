package cn.itcast.price_search5;

/**
 * Created by xiaomo on 2019/12/4.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by xiaomo on 2019/11/11.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

/*    public static final String Create_Commodity_Table = "create table Commodity5 ("
            + "title text,"
            + "price text,"
            + "bitmapString text)";*/
public static final String Create_Commodity_Table ="create table Commodity10(_id integer primary key autoincrement,title varchar(100), price varchar(100),bitmapString varchar(1000))";
    private Context mContext;

    public MyDatabaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,
                            int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_Commodity_Table);
        Toast.makeText(mContext,"创建成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
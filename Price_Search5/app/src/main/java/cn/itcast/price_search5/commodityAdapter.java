package cn.itcast.price_search5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiaomo on 2019/12/4.
 */
public class commodityAdapter extends ArrayAdapter<commodity> {
    private int resourceId;

    public commodityAdapter(Context context, int textViewResourceId, List<commodity> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        commodity cmdy = getItem(position);//获取当前commodity实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView cmdy_item = (TextView) view.findViewById(R.id.cmdy_item);

        cmdy_item.setText(cmdy.getTitle());

        return view;
    }
}

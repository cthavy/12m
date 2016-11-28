package io.github.a12m.one2many;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mohammed on 7/30/16.
 */
public class EventsListAdapter extends BaseAdapter {
    String names[];
    ArrayList<Bitmap> pics = new ArrayList<>();
    Context context;

    private static LayoutInflater inflater = null;

    public EventsListAdapter(Context actContext, String[] eventNames, ArrayList<Bitmap> eventPic) {
        names = eventNames;
        context = actContext;
        pics = eventPic;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.lobby_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.Itemname);
        holder.iv = (ImageView) rowView.findViewById(R.id.icon);
        holder.tv.setText(names[position]);
        holder.iv.setImageBitmap(pics.get(position));

        return rowView;
    }
}

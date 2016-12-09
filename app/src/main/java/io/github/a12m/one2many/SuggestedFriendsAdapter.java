package io.github.a12m.one2many;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aqeel on 12/8/16.
 */

public class SuggestedFriendsAdapter extends ArrayAdapter{
    private Context context;
    private int layoutResourceId;
    private ArrayList<ParseObject> data = new ArrayList();

    public SuggestedFriendsAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.e_pic);
            holder.username = (TextView) row.findViewById(R.id.username);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ParseObject parseObject = data.get(position);

        Picasso.with(getContext())
                .load(((ParseFile) parseObject.get("avatarPic")).getUrl())
                .error(R.drawable.error)
                .resize(100, 100)
                .into(holder.image);

        holder.username.setText(parseObject.getString("username"));

        return row;
    }

    static class ViewHolder {
        ImageView image;
        TextView username;
    }
}

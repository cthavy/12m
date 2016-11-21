package io.github.a12m.one2many;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ParseObject> data = new ArrayList();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
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
            holder.image = (ImageView) row.findViewById(R.id.picture);
            holder.isVideo = (ImageView) row.findViewById(R.id.imageView_video_icon);
            holder.progressBar = (ProgressBar) row.findViewById(R.id.progress_bar_grid_view_picture);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ParseObject parseObject = data.get(position);

        final ViewHolder finalHolder = holder;

        if(parseObject.getBoolean("isVideo")){
            holder.isVideo.setVisibility(View.VISIBLE);


            if(parseObject.get("videoPreview") != null){
                Picasso.with(getContext())
                        .load(((ParseFile) parseObject.get("videoPreview")).getUrl())
                        .error(R.drawable.video_preview)
                        .resize(140, 208)
                        .into(holder.image, new Callback() {
                            @Override
                            public void onSuccess() {
                                finalHolder.progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            } else {
                Picasso.with(getContext())
                        .load(R.drawable.video_preview)
                        .resize(140, 208)
                        .into(holder.image, new Callback() {
                            @Override
                            public void onSuccess() {
                                finalHolder.progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

        } else {
            Picasso.with(getContext())
                    .load(((ParseFile) parseObject.get("pic")).getUrl())
                    .resize(140, 208)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            finalHolder.progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        return row;
    }

    static class ViewHolder {
        ImageView image;
        ImageView isVideo;
        ProgressBar progressBar;
    }
}
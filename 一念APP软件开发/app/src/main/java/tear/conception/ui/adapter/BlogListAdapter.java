package tear.conception.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tear.conception.R;
import tear.conception.model.Blog;

public class BlogListAdapter extends BaseAdapter {

    private Context context;
    private List<Blog> blogList;
    private LayoutInflater inflater;
    private OnBlogItemClickListener listener;

    public interface OnBlogItemClickListener {
        void onBlogClick(Blog blog, int position);
    }

    public BlogListAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnBlogItemClickListener(OnBlogItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Blog> newBlogList) {
        this.blogList.clear();
        this.blogList.addAll(newBlogList);
        notifyDataSetChanged();
    }

    public void addData(List<Blog> moreBlogList) {
        this.blogList.addAll(moreBlogList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return blogList != null ? blogList.size() : 0;
    }

    @Override
    public Blog getItem(int position) {
        return blogList != null ? blogList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_blog, parent, false);
            holder = new ViewHolder();
            holder.ivAvatar = convertView.findViewById(R.id.iv_avatar);
            holder.tvNickname = convertView.findViewById(R.id.tv_nickname);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvType = convertView.findViewById(R.id.tv_type);
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvDescription = convertView.findViewById(R.id.tv_description);
            holder.ivCover = convertView.findViewById(R.id.iv_cover);
            holder.tvViews = convertView.findViewById(R.id.tv_views);
            holder.tvCommentCount = convertView.findViewById(R.id.tv_comment_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Blog blog = blogList.get(position);
        
        holder.tvNickname.setText(blog.getNickname());
        holder.tvTitle.setText(blog.getTitle());
        holder.tvDescription.setText(blog.getDescription());
        holder.tvType.setText(blog.getTypeName());
        holder.tvViews.setText(formatCount(blog.getViews()));
        holder.tvCommentCount.setText(formatCount(blog.getCommentCount()));
        
        String timeText = formatTime(blog.getUpdateTime());
        if (timeText.isEmpty()) {
            timeText = formatTime(blog.getCreateTime());
        }
        holder.tvTime.setText(timeText);
        
        if (blog.getFirstPicture() != null && !blog.getFirstPicture().isEmpty()) {
            holder.ivCover.setVisibility(View.VISIBLE);
            loadImage(holder.ivCover, blog.getFirstPicture());
        } else {
            holder.ivCover.setVisibility(View.GONE);
        }
        
        holder.ivAvatar.setImageResource(R.drawable.default_avatar);
        if (blog.getAvatar() != null && !blog.getAvatar().isEmpty()) {
            loadAvatar(holder.ivAvatar, blog.getAvatar());
        }
        
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBlogClick(blog, position);
                }
            }
        });

        return convertView;
    }

    private String formatCount(int count) {
        if (count >= 10000) {
            return String.format(Locale.getDefault(), "%.1fw", count / 10000.0);
        } else if (count >= 1000) {
            return String.format(Locale.getDefault(), "%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }

    private String formatTime(long timestamp) {
        if (timestamp <= 0) {
            return "";
        }
        
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        if (diff < 60 * 1000) {
            return "刚刚";
        } else if (diff < 60 * 60 * 1000) {
            return (diff / (60 * 1000)) + "分钟前";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return (diff / (60 * 60 * 1000)) + "小时前";
        } else if (diff < 7 * 24 * 60 * 60 * 1000) {
            return (diff / (24 * 60 * 60 * 1000)) + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }

    private void loadImage(final ImageView imageView, final String url) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL imageUrl = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream input = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null && imageView != null) {
                    imageView.setImageBitmap(result);
                }
            }
        }.execute(url);
    }

    private void loadAvatar(final ImageView imageView, final String url) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL imageUrl = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream input = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null && imageView != null) {
                    imageView.setImageBitmap(result);
                }
            }
        }.execute(url);
    }

    static class ViewHolder {
        ImageView ivAvatar;
        TextView tvNickname;
        TextView tvTime;
        TextView tvType;
        TextView tvTitle;
        TextView tvDescription;
        ImageView ivCover;
        TextView tvViews;
        TextView tvCommentCount;
    }
}

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
import tear.conception.model.Discussion;

public class DiscussionListAdapter extends BaseAdapter {

    private Context context;
    private List<Discussion> discussionList;
    private LayoutInflater inflater;
    private OnDiscussionItemClickListener listener;

    public interface OnDiscussionItemClickListener {
        void onDiscussionClick(Discussion discussion, int position);
    }

    public DiscussionListAdapter(Context context, List<Discussion> discussionList) {
        this.context = context;
        this.discussionList = discussionList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnDiscussionItemClickListener(OnDiscussionItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Discussion> newDiscussionList) {
        this.discussionList.clear();
        this.discussionList.addAll(newDiscussionList);
        notifyDataSetChanged();
    }

    public void addData(List<Discussion> moreDiscussionList) {
        this.discussionList.addAll(moreDiscussionList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionList != null ? discussionList.size() : 0;
    }

    @Override
    public Discussion getItem(int position) {
        return discussionList != null ? discussionList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_discussion, parent, false);
            holder = new ViewHolder();
            holder.ivAvatar = convertView.findViewById(R.id.iv_avatar);
            holder.tvNickname = convertView.findViewById(R.id.tv_nickname);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvContent = convertView.findViewById(R.id.tv_content);
            holder.tvViews = convertView.findViewById(R.id.tv_views);
            holder.tvCommentCount = convertView.findViewById(R.id.tv_comment_count);
            holder.tvLikeCount = convertView.findViewById(R.id.tv_like_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Discussion discussion = discussionList.get(position);
        
        holder.tvNickname.setText(discussion.getNickname());
        holder.tvTitle.setText(discussion.getTitle());
        holder.tvContent.setText(discussion.getContent());
        holder.tvViews.setText(formatCount(discussion.getViewCount()));
        holder.tvCommentCount.setText(formatCount(discussion.getCommentCount()));
        holder.tvLikeCount.setText(formatCount(discussion.getLikeCount()));
        
        String timeText = formatTime(discussion.getCreateTime());
        holder.tvTime.setText(timeText);
        
        holder.ivAvatar.setImageResource(R.drawable.default_avatar);
        if (discussion.getAvatar() != null && !discussion.getAvatar().isEmpty()) {
            loadAvatar(holder.ivAvatar, discussion.getAvatar());
        } else if (discussion.getQqNumber() != null && !discussion.getQqNumber().isEmpty()) {
            String avatarUrl = "https://q1.qlogo.cn/g?b=qq&nk=" + discussion.getQqNumber() + "&s=100";
            loadAvatar(holder.ivAvatar, avatarUrl);
        }
        
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDiscussionClick(discussion, position);
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

    private String formatTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timeString);
            long timestamp = date.getTime();
            
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
                SimpleDateFormat displayFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return timeString;
        }
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
        TextView tvTitle;
        TextView tvContent;
        TextView tvViews;
        TextView tvCommentCount;
        TextView tvLikeCount;
    }
}

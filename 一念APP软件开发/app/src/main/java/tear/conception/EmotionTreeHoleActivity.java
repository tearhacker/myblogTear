package tear.conception;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tear.conception.module.BlogApiService;

public class EmotionTreeHoleActivity extends Activity {

    private LinearLayout llBack;
    private LinearLayout llChatContainer;
    private LinearLayout llInputContainer;
    private LinearLayout llShare;
    private ScrollView scrollView;
    private EditText etMessage;
    private TextView tvSend;
    private ProgressBar progressBar;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_emotion_tree_hole);

        initViews();
        setupListeners();
    }

    private void initViews() {
        llBack = findViewById(R.id.ll_back);
        llChatContainer = findViewById(R.id.ll_chat_container);
        llInputContainer = findViewById(R.id.ll_input_container);
        llShare = findViewById(R.id.ll_share);
        scrollView = findViewById(R.id.scroll_view);
        etMessage = findViewById(R.id.et_message);
        tvSend = findViewById(R.id.tv_send);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog();
            }
        });
    }

    private void sendMessage() {
        final String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        etMessage.setText("");
        addMessageView(message, true, "我");
        chatMessages.add(new ChatMessage(message, true, "我"));
        
        showLoading(true);

        String prompt = buildEmotionPrompt(message);
        
        BlogApiService.aiChat(prompt, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        try {
                            int code = response.optInt("code", -1);
                            if (code == 200) {
                                JSONObject data = response.optJSONObject("data");
                                if (data != null) {
                                    String aiResponse = data.optString("response", "我在听...");
                                    addMessageView(aiResponse, false, "树洞");
                                    chatMessages.add(new ChatMessage(aiResponse, false, "树洞"));
                                }
                            } else {
                                String errorMsg = response.optString("message", "我暂时无法回应，请稍后再试");
                                addMessageView(errorMsg, false, "树洞");
                                chatMessages.add(new ChatMessage(errorMsg, false, "树洞"));
                            }
                        } catch (Exception e) {
                            addMessageView("我暂时无法回应，请稍后再试", false, "树洞");
                            chatMessages.add(new ChatMessage("我暂时无法回应，请稍后再试", false, "树洞"));
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        addMessageView("网络似乎不太顺畅，但我一直在", false, "树洞");
                        chatMessages.add(new ChatMessage("网络似乎不太顺畅，但我一直在", false, "树洞"));
                    }
                });
            }
        });
    }

    private String buildEmotionPrompt(String userMessage) {
        return "你是一个温暖、善解人意的情感树洞。用户向你倾诉心事，请用温柔、理解、治愈的语气回应。" +
               "回应要简短（不超过100字），真诚，有共情力。不要说教，只是倾听和陪伴。" +
               "用户说：" + userMessage;
    }

    private void addMessageView(String message, boolean isUser, String label) {
        View messageView = LayoutInflater.from(this).inflate(
                R.layout.item_emotion_message, llChatContainer, false);
        
        TextView tvAvatar = messageView.findViewById(R.id.tv_avatar);
        TextView tvLabel = messageView.findViewById(R.id.tv_label);
        TextView tvMessage = messageView.findViewById(R.id.tv_message);
        
        tvLabel.setText(label);
        tvMessage.setText(message);
        
        if (isUser) {
            tvAvatar.setText("😊");
            tvMessage.setBackgroundResource(R.drawable.bg_message_user);
            tvMessage.setTextColor(getResources().getColor(R.color.white));
        } else {
            tvAvatar.setText("☁");
            tvMessage.setBackgroundResource(R.drawable.bg_message_ai);
            tvMessage.setTextColor(getResources().getColor(R.color.love_primary));
        }
        
        llChatContainer.addView(messageView);
        scrollToBottom();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvSend.setEnabled(!show);
    }

    private void scrollToBottom() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }

    private void showShareDialog() {
        if (chatMessages.isEmpty()) {
            Toast.makeText(this, "还没有聊天记录哦", Toast.LENGTH_SHORT).show();
            return;
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("分享聊天记录")
                .setItems(new String[]{"保存为图片", "分享到QQ/微信"}, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if (which == 0) {
                            saveChatAsImage();
                        } else {
                            shareChatAsImage();
                        }
                    }
                })
                .show();
    }

    private void saveChatAsImage() {
        Bitmap bitmap = generateChatImage();
        if (bitmap != null) {
            saveImageToGallery(bitmap);
        }
    }

    private void shareChatAsImage() {
        Bitmap bitmap = generateChatImage();
        if (bitmap != null) {
            shareImage(bitmap);
        }
    }

    private Bitmap generateChatImage() {
        View shareView = LayoutInflater.from(this).inflate(R.layout.layout_share_chat, null);
        LinearLayout llShareContent = shareView.findViewById(R.id.ll_share_content);
        TextView tvDate = shareView.findViewById(R.id.tv_date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));

        for (ChatMessage msg : chatMessages) {
            View messageView = LayoutInflater.from(this).inflate(R.layout.item_share_message, llShareContent, false);
            TextView tvAvatar = messageView.findViewById(R.id.tv_avatar);
            TextView tvLabel = messageView.findViewById(R.id.tv_label);
            TextView tvMessage = messageView.findViewById(R.id.tv_message);

            tvLabel.setText(msg.label);
            tvMessage.setText(msg.message);

            if (msg.isUser) {
                tvAvatar.setText("😊");
                tvMessage.setBackgroundResource(R.drawable.bg_message_user);
                tvMessage.setTextColor(Color.WHITE);
            } else {
                tvAvatar.setText("☁");
                tvMessage.setBackgroundResource(R.drawable.bg_message_ai);
                tvMessage.setTextColor(getResources().getColor(R.color.love_primary));
            }

            llShareContent.addView(messageView);
        }

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        shareView.measure(widthMeasureSpec, heightMeasureSpec);
        shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(shareView.getMeasuredWidth(), shareView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        shareView.draw(canvas);

        return bitmap;
    }

    private void saveImageToGallery(Bitmap bitmap) {
        try {
            String fileName = "emotion_tree_hole_" + System.currentTimeMillis() + ".png";
            OutputStream fos;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/一念APP");

                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/一念APP";
                File file = new File(imagesDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File imageFile = new File(imagesDir, fileName);
                fos = new FileOutputStream(imageFile);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Toast.makeText(this, "图片已保存到相册", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareImage(Bitmap bitmap) {
        try {
            String fileName = "emotion_tree_hole_share_" + System.currentTimeMillis() + ".png";
            File cacheDir = getExternalCacheDir();
            File imageFile = new File(cacheDir, fileName);
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            Uri imageUri = Uri.fromFile(imageFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "分享到"));
        } catch (Exception e) {
            Toast.makeText(this, "分享失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class ChatMessage {
        String message;
        boolean isUser;
        String label;

        ChatMessage(String message, boolean isUser, String label) {
            this.message = message;
            this.isUser = isUser;
            this.label = label;
        }
    }
}

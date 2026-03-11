package tear.conception.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import tear.conception.R;
import tear.conception.ui.view.PetalParticleView;

public class SakuraLoveLetterDialog extends Dialog {

    private PetalParticleView petalView;
    private TextView btnWriteLetter;
    private TextView btnLater;
    private OnDialogClickListener listener;

    public interface OnDialogClickListener {
        void onWriteLetter();
        void onLater();
    }

    public SakuraLoveLetterDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sakura_love_letter);

        petalView = findViewById(R.id.petal_view);
        btnWriteLetter = findViewById(R.id.btn_write_letter);
        btnLater = findViewById(R.id.btn_later);

        btnWriteLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onWriteLetter();
                }
                dismiss();
            }
        });

        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLater();
                }
                dismiss();
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        if (petalView != null) {
            petalView.startContinuousMode();
        }
        animateButtons();
    }

    private void animateButtons() {
        btnWriteLetter.setAlpha(0f);
        btnWriteLetter.setScaleX(0.8f);
        btnWriteLetter.setScaleY(0.8f);
        btnWriteLetter.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(new OvershootInterpolator())
            .start();

        btnLater.setAlpha(0f);
        btnLater.setScaleX(0.8f);
        btnLater.setScaleY(0.8f);
        btnLater.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setStartDelay(150)
            .setInterpolator(new OvershootInterpolator())
            .start();
    }

    @Override
    public void dismiss() {
        if (petalView != null) {
            petalView.stopAnimation();
        }
        super.dismiss();
    }
}

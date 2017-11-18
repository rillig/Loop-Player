package de.roland_illig.loopplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class WaveformView extends View {
    public WaveformView(Context context) {
        super(context);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint white = new Paint();
        white.setColor(Color.WHITE);
        white.setAntiAlias(true);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setAntiAlias(true);

        canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), white);
        canvas.drawLine(0.0f, 0.0f, getWidth(), getHeight(), blue);
        canvas.drawLine(0.0f, getHeight(), getWidth(), 0.0f, blue);
    }
}

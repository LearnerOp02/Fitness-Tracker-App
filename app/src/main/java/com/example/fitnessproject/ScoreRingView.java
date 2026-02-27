package com.example.fitnessproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScoreRingView extends View {

    private final Paint bgRingPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fgRingPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint subTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int score = 0;

    public ScoreRingView(Context ctx) { super(ctx); init(); }
    public ScoreRingView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public ScoreRingView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        bgRingPaint.setStyle(Paint.Style.STROKE);
        bgRingPaint.setStrokeWidth(18f);
        bgRingPaint.setColor(0xFF2A2A5A);
        bgRingPaint.setStrokeCap(Paint.Cap.ROUND);

        fgRingPaint.setStyle(Paint.Style.STROKE);
        fgRingPaint.setStrokeWidth(18f);
        fgRingPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        subTextPaint.setTextAlign(Paint.Align.CENTER);
        subTextPaint.setColor(0xFFAAAAAA);
    }

    public void setScore(int score) {
        this.score = Math.max(0, Math.min(100, score));
        // Color gradient: red → orange → green
        if      (score < 40) fgRingPaint.setColor(0xFFF44336);
        else if (score < 70) fgRingPaint.setColor(0xFFFF9800);
        else                 fgRingPaint.setColor(0xFF4CAF50);
        textPaint.setColor(fgRingPaint.getColor());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        float cx = w / 2f, cy = h / 2f;
        float r  = Math.min(cx, cy) - 20f;

        RectF oval = new RectF(cx - r, cy - r, cx + r, cy + r);

        // Background ring
        canvas.drawArc(oval, -90, 360, false, bgRingPaint);

        // Score ring (sweep angle = score * 3.6)
        canvas.drawArc(oval, -90, score * 3.6f, false, fgRingPaint);

        // Score number
        textPaint.setTextSize(r * 0.65f);
        canvas.drawText(String.valueOf(score), cx, cy + r * 0.22f, textPaint);

        // "/100" sub text
        subTextPaint.setTextSize(r * 0.22f);
        canvas.drawText("/ 100", cx, cy + r * 0.52f, subTextPaint);
    }
}
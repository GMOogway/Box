package com.github.tvbox.osc.subtitle.widget;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.tvbox.osc.cache.CacheManager;
import com.github.tvbox.osc.subtitle.DefaultSubtitleEngine;
import com.github.tvbox.osc.subtitle.SubtitleEngine;
import com.github.tvbox.osc.subtitle.model.Subtitle;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.util.StringUtils;


import org.jetbrains.annotations.Nullable;

import java.util.List;

import xyz.doikki.videoplayer.player.AbstractPlayer;

/**
 * @author AveryZhong.
 */

@SuppressLint("AppCompatCustomView")
public class SimpleSubtitleView extends TextView
        implements SubtitleEngine, SubtitleEngine.OnSubtitleChangeListener,
        SubtitleEngine.OnSubtitlePreparedListener {

    private static final String EMPTY_TEXT = "";

    private SubtitleEngine mSubtitleEngine;

    public boolean isInternal = false;

    public boolean hasInternal = false;

    private TextView backGroundText = null;//用于描边的TextView

    private int backGroundTextColor = Color.BLACK;//用于描边的TextView

    // 当前显示的图形字幕位图
    private android.graphics.Bitmap currentBitmap = null;

    public SimpleSubtitleView(final Context context) {
        super(context);
        backGroundText = new TextView(context);
        init();
    }

    public SimpleSubtitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        backGroundText = new TextView(context, attrs);
        init();
    }

    public SimpleSubtitleView(final Context context, final AttributeSet attrs,
                              final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backGroundText = new TextView(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSubtitleEngine = new DefaultSubtitleEngine();
        mSubtitleEngine.setOnSubtitlePreparedListener(this);
        mSubtitleEngine.setOnSubtitleChangeListener(this);
    }

    @Override
    public void onSubtitlePrepared(@Nullable final List<Subtitle> subtitles) {
        start();
    }

    @Override
    public void onSubtitleChanged(@Nullable final Subtitle subtitle) {
        String logMsg = "========== SimpleSubtitleView onSubtitleChanged called ==========";
        writeSubtitleDebugLog(logMsg);

        logMsg = "isInternal: " + isInternal;
        writeSubtitleDebugLog(logMsg);

        if (subtitle == null) {
            logMsg = "subtitle object is null";
            writeSubtitleDebugLog(logMsg);
            clearSubtitle();
            return;
        }

        // 检查是否有位图字幕（图形字幕）
        logMsg = "subtitle.bitmap: " + (subtitle.bitmap != null ? "not null" : "null");
        writeSubtitleDebugLog(logMsg);

        if (subtitle.bitmap != null) {
            // 图形字幕
            logMsg = "Displaying bitmap subtitle";
            writeSubtitleDebugLog(logMsg);
            currentBitmap = subtitle.bitmap;
            setText(EMPTY_TEXT);  // 清除文本
            invalidate();  // 触发重绘
            return;
        }

        // 文本字幕
        currentBitmap = null;  // 清除位图

        if (subtitle.content == null) {
            logMsg = "subtitle.content is null";
            writeSubtitleDebugLog(logMsg);
            setText(EMPTY_TEXT);
            return;
        }

        String originalText = subtitle.content;
        logMsg = "Original subtitle text: [" + originalText + "]";
        writeSubtitleDebugLog(logMsg);

        logMsg = "Original subtitle text length: " + originalText.length();
        writeSubtitleDebugLog(logMsg);

        if (StringUtils.isEmpty(subtitle)) {
            logMsg = "Subtitle is empty, setting empty text";
            writeSubtitleDebugLog(logMsg);
            setText(EMPTY_TEXT);
            return;
        }

        String text = subtitle.content;
        if (text.startsWith("Dialogue:") || text.startsWith("m ")) {
            logMsg = "Subtitle starts with Dialogue: or m , setting empty text";
            writeSubtitleDebugLog(logMsg);
            setText(EMPTY_TEXT);
            return;
        }

        text = text.replaceAll("(?:\\r\\n)", "<br />");
        text = text.replaceAll("(?:\\r)", "<br />");
        text = text.replaceAll("(?:\\n)", "<br />");
        text = text.replaceAll("\\\\N", "<br />");
        text = text.replaceAll("\\{[\\s\\S]*?\\}", "");
        text = text.replaceAll("^.*?,.*?,.*?,.*?,.*?,.*?,.*?,.*?,.*?,", "");

        logMsg = "Processed subtitle text: [" + text + "]";
        writeSubtitleDebugLog(logMsg);

        logMsg = "Processed subtitle text length: " + text.length();
        writeSubtitleDebugLog(logMsg);

        setText(Html.fromHtml(text));

        logMsg = "Text set to TextView, current text: [" + getText() + "]";
        writeSubtitleDebugLog(logMsg);
    }

    /**
     * 清除字幕内容（文本和位图）
     */
    private void clearSubtitle() {
        setText(EMPTY_TEXT);
        currentBitmap = null;
        invalidate();
    }

    @Override
    public void setSubtitlePath(final String path) {
        isInternal = false; // 切换到外部字幕
        mSubtitleEngine.setSubtitlePath(path);
    }

    @Override
    public void setSubtitleDelay(Integer mseconds) {
        mSubtitleEngine.setSubtitleDelay(mseconds);
    }

    public void setPlaySubtitleCacheKey(String cacheKey) {
        mSubtitleEngine.setPlaySubtitleCacheKey(cacheKey);
    }

    public String getPlaySubtitleCacheKey() {
        return mSubtitleEngine.getPlaySubtitleCacheKey();
    }

    public void clearSubtitleCache() {
        String subtitleCacheKey = getPlaySubtitleCacheKey();
        if (subtitleCacheKey != null && subtitleCacheKey.length() > 0) {
            CacheManager.delete(MD5.string2MD5(subtitleCacheKey), "");
        }
    }

    @Override
    public void reset() {
        mSubtitleEngine.reset();
    }

    @Override
    public void start() {
        // 内置字幕不需要启动外部字幕引擎
        if (!isInternal) {
            mSubtitleEngine.start();
        }
    }

    @Override
    public void pause() {
        mSubtitleEngine.pause();
    }

    @Override
    public void resume() {
        // 内置字幕不需要启动外部字幕引擎
        if (!isInternal) {
            mSubtitleEngine.resume();
        }
    }

    @Override
    public void stop() {
        mSubtitleEngine.stop();
    }

    @Override
    public void destroy() {
        mSubtitleEngine.destroy();
    }

    // 停止外部字幕引擎（用于切换到内置字幕）
    public void stopExternalSubtitle() {
        mSubtitleEngine.stop();
    }

    @Override
    public void bindToMediaPlayer(AbstractPlayer mediaPlayer) {
        mSubtitleEngine.bindToMediaPlayer(mediaPlayer);
    }

    @Override
    public void setOnSubtitlePreparedListener(final OnSubtitlePreparedListener listener) {
        mSubtitleEngine.setOnSubtitlePreparedListener(listener);
    }

    @Override
    public void setOnSubtitleChangeListener(final OnSubtitleChangeListener listener) {
        mSubtitleEngine.setOnSubtitleChangeListener(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        destroy();
        super.onDetachedFromWindow();
    }

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.backGroundTextColor = color;
        super.setShadowLayer(radius, dx, dy, color);
    }

    public void setBackGroundTextColor(int backGroundTextColor) {
        if (backGroundTextColor != this.backGroundTextColor) {
            this.backGroundTextColor = backGroundTextColor;
            invalidate();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        //同步布局参数
        backGroundText.setLayoutParams(params);
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = backGroundText.getText();
        //两个TextView上的文字必须一致
        if (TextUtils.isEmpty(tt) || !tt.equals(this.getText())) {
            backGroundText.setText(getText());
            this.postInvalidate();
        }
        backGroundText.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        backGroundText.setTextSize(size);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (backGroundText != null) {
            backGroundText.setText(text);
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        backGroundText.layout(left, top, right, bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 如果有图形字幕位图，绘制位图
        if (currentBitmap != null && !currentBitmap.isRecycled()) {
            // 计算位图位置（居中显示）
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            int bitmapWidth = currentBitmap.getWidth();
            int bitmapHeight = currentBitmap.getHeight();

            // 缩放位图以适应视图宽度，保持宽高比
            float scale = Math.min(
                (float) viewWidth / bitmapWidth,
                (float) viewHeight / bitmapHeight
            );

            int scaledWidth = (int) (bitmapWidth * scale);
            int scaledHeight = (int) (bitmapHeight * scale);

            int left = (viewWidth - scaledWidth) / 2;
            int top = (viewHeight - scaledHeight) / 2;

            // 绘制位图
            android.graphics.Rect src = new android.graphics.Rect(0, 0, bitmapWidth, bitmapHeight);
            android.graphics.Rect dst = new android.graphics.Rect(left, top, left + scaledWidth, top + scaledHeight);
            canvas.drawBitmap(currentBitmap, src, dst, null);
            return;  // 位图字幕不显示文本
        }

        // 文本字幕：其他地方，backGroundText和super的先后顺序影响不会很大，但是此处必须要先绘制backGroundText，
        drawBackGroundText();
        backGroundText.draw(canvas);
        super.onDraw(canvas);
    }

    private void drawBackGroundText() {
        TextPaint tp = backGroundText.getPaint();
        //设置描边宽度
        tp.setStrokeWidth(4);
        //背景描边并填充全部
        tp.setStyle(Paint.Style.STROKE);
        //设置描边颜色
        backGroundText.setTextColor(backGroundTextColor);
        //将背景的文字对齐方式做同步
        backGroundText.setGravity(getGravity());
    }

    private void writeSubtitleDebugLog(String message) {
        try {
            java.io.File logFile = new java.io.File("/sdcard/dsm/subtitle_debug.log");
            java.io.FileWriter writer = new java.io.FileWriter(logFile, true);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestamp = sdf.format(new java.util.Date());
            writer.write(timestamp + " - " + message + "\n");
            writer.close();
        } catch (Exception e) {
            // 忽略写入错误
        }
    }

}

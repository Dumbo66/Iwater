/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.dumbo.iwater.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.app.dumbo.iwater.util.CommonUtil;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.R;
import com.journeyapps.barcodescanner.CameraPreview;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class MyViewFinderView extends ViewfinderView {
    protected static final String TAG = ViewfinderView.class.getSimpleName();

    protected static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    protected static final long ANIMATION_DELAY = 80L;
    protected static final int CURRENT_POINT_OPACITY = 0xA0;
    protected static final int MAX_RESULT_POINTS = 20;
    protected static final int POINT_SIZE = 6;
    protected static final int LINE_LENGTH = 50;
    protected static final int LINE_WIDTH = 7;
    protected static final int RAD = 2;
    public int laserLinePosition=0;
    public float[] position=new float[]{0f,0.5f,1f};
    public int[] colors=new int[]{0x1000ff00,0xff00ff00,0x1000ff00};

    protected final Paint paint;
    protected Bitmap resultBitmap;
    protected final int maskColor;
    protected final int resultColor;
    protected final int laserColor;
    protected final int resultPointColor;
    protected int scannerAlpha;
    protected List<ResultPoint> possibleResultPoints;
    protected List<ResultPoint> lastPossibleResultPoints;
    protected CameraPreview cameraPreview;
    public LinearGradient linearGradient ;

    // Cache the framingRect and previewFramingRect, so that we can still draw it after the preview
    // stopped.
    protected Rect framingRect;
    protected Rect previewFramingRect;

    // This constructor is used when the class is built from an XML resource.
    public MyViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Resources resources = getResources();

        // Get setted attributes on view
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.zxing_finder);

        this.maskColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_mask,
                resources.getColor(R.color.zxing_viewfinder_mask));
        this.resultColor = attributes.getColor(R.styleable.zxing_finder_zxing_result_view,
                resources.getColor(R.color.zxing_result_view));
        this.laserColor = attributes.getColor(R.styleable.zxing_finder_zxing_viewfinder_laser,
                resources.getColor(R.color.zxing_viewfinder_laser));
        this.resultPointColor = attributes.getColor(R.styleable.zxing_finder_zxing_possible_result_points,
                resources.getColor(R.color.zxing_possible_result_points));

        attributes.recycle();

        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(MAX_RESULT_POINTS);
        lastPossibleResultPoints = new ArrayList<>(MAX_RESULT_POINTS);
    }

    public void setCameraPreview(CameraPreview view) {
        this.cameraPreview = view;
        view.addStateListener(new CameraPreview.StateListener() {
            @Override
            public void previewSized() {
                refreshSizes();
                invalidate();
            }

            @Override
            public void previewStarted() {

            }

            @Override
            public void previewStopped() {

            }

            @Override
            public void cameraError(Exception error) {

            }

            @Override
            public void cameraClosed() {

            }
        });
    }

    protected void refreshSizes() {
        if(cameraPreview == null) {
            return;
        }
        Rect framingRect = cameraPreview.getFramingRect();
        Rect previewFramingRect = cameraPreview.getPreviewFramingRect();
        if(framingRect != null && previewFramingRect != null) {
            this.framingRect = framingRect;
            this.previewFramingRect = previewFramingRect;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }

        final Rect frame = framingRect;
        final Rect previewFrame = previewFramingRect;

        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        paint.setColor(0xff00ff00);//定义画笔的颜色
        //绘制四个角
        //左上角
        canvas.drawRoundRect(frame.left, frame.top, frame.left+ LINE_LENGTH,
                frame.top+LINE_WIDTH, RAD,RAD,paint);
        canvas.drawRoundRect(frame.left, frame.top, frame.left + LINE_WIDTH,
                frame.top + LINE_LENGTH,RAD,RAD, paint);
        //右上角
        canvas.drawRoundRect(frame.right-LINE_LENGTH, frame.top, frame.right,
                frame.top+LINE_WIDTH,RAD,RAD, paint);
        canvas.drawRoundRect(frame.right-LINE_WIDTH, frame.top, frame.right,
                frame.top+LINE_LENGTH,RAD,RAD, paint);
        //左下角
        canvas.drawRoundRect(frame.left, frame.bottom-LINE_WIDTH,
                frame.left+LINE_LENGTH, frame.bottom, RAD,RAD,paint);
        canvas.drawRoundRect(frame.left, frame.bottom-LINE_LENGTH,
                frame.left+LINE_WIDTH, frame.bottom, RAD,RAD,paint);
        //右下角
        canvas.drawRoundRect(frame.right-LINE_LENGTH, frame.bottom-LINE_WIDTH,
                frame.right, frame.bottom,RAD,RAD, paint);
        canvas.drawRoundRect(frame.right-LINE_WIDTH, frame.bottom-LINE_LENGTH,
                frame.right, frame.bottom,RAD,RAD, paint);

        //绘制边框
        paint.setColor(0xff00aa00);//定义画笔的颜色
        canvas.drawLine(frame.left,frame.top,frame.right,frame.top,paint);
        canvas.drawLine(frame.left,frame.bottom,frame.right,frame.bottom,paint);
        canvas.drawLine(frame.left,frame.top,frame.left,frame.bottom,paint);
        canvas.drawLine(frame.right,frame.top,frame.right,frame.bottom,paint);

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            // Draw a red "laser scanner" line through the middle to show decoding is active
//            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            final int middle = frame.height() / 2 + frame.top;
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            //绘制扫描线
            laserLinePosition=laserLinePosition+12;
            if(laserLinePosition>frame.height())
            {
                laserLinePosition=0;
            }
            linearGradient= new LinearGradient(frame.left + 1, frame.top+laserLinePosition , frame.right -1
                    , frame.top +8+laserLinePosition, colors, position, Shader.TileMode.CLAMP);
            paint.setShader(linearGradient);
            canvas.drawRect(frame.left + 1, frame.top+laserLinePosition ,
                    frame.right -1 , frame.top +8+laserLinePosition, paint);
            paint.setShader(null);

            //绘制提示语
            paint.setColor(0xffffffff);

            paint.setTextSize(CommonUtil.sp2px(getContext(),14.0f));
            final String text="请将二维码放入框内扫描！";
            final float textLength=paint.measureText(text);
            final float x=(width-textLength)/2;
            final float y=frame.bottom+frame.width()/10;
            canvas.drawText(text,x,y,paint);

            final float scaleX = frame.width() / (float) previewFrame.width();
            final float scaleY = frame.height() / (float) previewFrame.height();

            final int frameLeft = frame.left;
            final int frameTop = frame.top;

            // draw the last possible result points
            if (!lastPossibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(0xff00ff00);
                float radius = POINT_SIZE / 2.0f;
                for (final ResultPoint point : lastPossibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            radius, paint
                    );
                }
                lastPossibleResultPoints.clear();
            }

            // draw current possible result points
            if (!possibleResultPoints.isEmpty()) {
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (final ResultPoint point : possibleResultPoints) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX),
                            frameTop + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint
                    );
                }

                // swap and clear buffers
                final List<ResultPoint> temp = possibleResultPoints;
                possibleResultPoints = lastPossibleResultPoints;
                lastPossibleResultPoints = temp;
                possibleResultPoints.clear();
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param result An image of the result.
     */
    public void drawResultBitmap(Bitmap result) {
        resultBitmap = result;
        invalidate();
    }

    /**
     * Only call from the UI thread.
     *
     * @param point a point to draw, relative to the preview frame
     */
    public void addPossibleResultPoint(ResultPoint point) {
        if (possibleResultPoints.size() < MAX_RESULT_POINTS)
            possibleResultPoints.add(point);
    }
}

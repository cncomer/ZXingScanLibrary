package com.bestjoy.library.scan.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * QRcode使用里德-所罗门码来进行错误修正。对于我们来说，里德-所罗门编码有两个非常重要的特性。
 * 第一，它是一种显式系统码，也就是说，你可以在最终的编码中直接看到原有的信息。就好比我们对”hello world”进行编码，最终看到的是”hello world”以及其后面跟随的几个容错码。
 * 第二点，里德-所罗门编码是可以被”异或”的，将两个不同里德-所罗门编码得到的结果异或运算后会得到一个新的里德-所罗门码，并且这个新码的原码即是原来两个原码的异或。
 * 如果你想知道为什么这两个特性会成立，请看Finite Field Arithmetic and Reed-Solomon Coding.
 */
public class QRGenerater extends Thread {
	private static final String TAG = "QRGenerater";
	private static String[] contentFormats = new String[]{
		"%s",
		"%s",
		"MECARD:N:%s;TEL:%s;ORG:%s;TITLE:%s;URL:%s;;",
		"geo:%s,%s",
		"WIFI:T:%s;S:%s;P:%s;;"
	};
	
	public static final int URL_CONTENT_FORMAT = 0;
	public static final int EMAIL_CONTENT_FORMAT = 1;
	public static final int MECARD_CONTENT_FORMAT = 2;
	public static final int GEO_CONTENT_FORMAT = 3;
	public static final int WIFI_CONTENT_FORMAT = 4;
	private static final int MIN_VALUE_FOR_CONTENT_FORMAT = 0;
	private static final int MAX_VALUE_FOR_CONTENT_FORMAT = 4;
	
	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;
	
	private static final int on = View.VISIBLE;
	private static final int off = View.GONE;
	
	private boolean isCancel = false;
	private int mQrContentFormat = 0;
	private String mQrContent;
	/**
	 *A class which wraps a 2D array of bytes. The default usage is signed. 
	 *If you want to use it as a unsigned container, it's up to you to do byteValue & 0xff 
	 *at each location. JAVAPORT: The original code was a 2D array of ints, but since it only
	 * ever gets assigned -1, 0, and 1, I'm going to use less memory and go with bytes.
	 */
	private BitMatrix byteMatrix;
	private Bitmap mBitmap;
	
	private int mHeight=300;
	private int mWidth=300;
    private static final int PADDING_SIZE_MIN = 20; // 最小留白长度, 单位: px
    private int mWhitePadding=PADDING_SIZE_MIN;
	public static interface QRGeneratorFinishListener {
		void onQRGeneratorFinish(Bitmap bitmap);
	}
	
	private QRGeneratorFinishListener mQRGeneratorFinishListener;
	
	public void setQRGeneratorFinishListener(QRGeneratorFinishListener listener) {
		mQRGeneratorFinishListener = listener;
	}
	
	public QRGenerater(String content) {
		if (DebugUtils.DEBUG_QRGEN) Log.v(TAG, "create QRThread for " + content);
		mQrContent = content;
	}
	
	public void setDimens(int height, int width) {
		DebugUtils.logD(TAG, "setDimens height " + height + " width "  + width);
		mHeight = height;
		mWidth = width;
	}

    public void setDimens(int height, int width, int whitePadding) {
        DebugUtils.logD(TAG, "setDimens height " + height + " width "  + width + ", whitePadding " + whitePadding);
        mHeight = height;
        mWidth = width;
        mWhitePadding = whitePadding;
    }
	
	public void setCancelStatus(boolean isCancel) {
		this.isCancel = isCancel;
	}
	
	public static String getContentFormat(int contentFormatType) {
		if (contentFormatType < MIN_VALUE_FOR_CONTENT_FORMAT || URL_CONTENT_FORMAT > MAX_VALUE_FOR_CONTENT_FORMAT) {
			throw new IllegalArgumentException("contentFormatType must be in the range from " + MIN_VALUE_FOR_CONTENT_FORMAT + " to " + MAX_VALUE_FOR_CONTENT_FORMAT );
		}
		return contentFormats[contentFormatType];
	}
	
	
	@Override
	public void run() {
		if (DebugUtils.DEBUG_QRGEN) {
			Log.v(TAG, "start thread");
			Log.v(TAG, "encoding " + mQrContent);
		}
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		try {
			Hashtable<EncodeHintType,Object> hints = new Hashtable<EncodeHintType,Object>(2);
		      hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 设置QR二维码的纠错级别——这里选择最高H级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            byteMatrix = qrCodeWriter.encode(
					        mQrContent,
							BarcodeFormat.QR_CODE, 
							mWidth,
							mHeight, 
							hints);
			int width = byteMatrix.getWidth();
			int height = byteMatrix.getHeight();
			int[] pixels = new int[width * height];
            boolean isFirstBlackPoint = false;
            int startX = 0;
            int startY = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (byteMatrix.get(x, y)) {
                        if (isFirstBlackPoint == false)
                        {
                            isFirstBlackPoint = true;
                            startX = x;
                            startY = y;
                            Log.d("createQRCode", "x y = " + x + " " + y);
                        }
                        pixels[y * width + x] = BLACK;
                    }
                }
            }

//		    // All are 0, or black, by default
//		    for (int y = 0; y < height; y++) {
//		      int offset = y * width;
//		      for (int x = 0; x < width; x++) {
//		    	if(isCancel)throw new CanceledException();
//		        pixels[offset + x] = byteMatrix.get(x, y) ? BLACK : WHITE;
//		      }
//		    }
//
//		    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		    mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            // 剪切中间的二维码区域，减少padding区域
            if (startX > mWhitePadding) {
                int x1 = startX - mWhitePadding;
                int y1 = startY - mWhitePadding;
                if (x1 > 0 && y1 > 0) {
                    int w1 = width - x1 * 2;
                    int h1 = height - y1 * 2;
                    mBitmap = Bitmap.createBitmap(mBitmap, x1, y1, w1, h1);
                }
            }

            if (DebugUtils.DEBUG_QRGEN) Log.v(TAG, "end thread");
		} catch (WriterException e) {
			e.printStackTrace(); 
			mBitmap = null;
		}
		if (mQRGeneratorFinishListener != null) mQRGeneratorFinishListener.onQRGeneratorFinish(mBitmap);
	}
	
	public Bitmap getBitmap() {
		return mBitmap;
	}
	
}

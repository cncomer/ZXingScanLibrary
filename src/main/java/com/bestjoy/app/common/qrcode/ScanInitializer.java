package com.bestjoy.app.common.qrcode;

import android.content.Context;

import com.bestjoy.library.scan.R;
import com.bestjoy.library.scan.utils.DebugUtils;

/**
 * Created by bestjoy on 16/3/8.
 */
public class ScanInitializer {

    private static final String TAG = "ScanInitializer";
    private Context mContext;
    private boolean mHasInit = false;
    private static final ScanInitializer INSTANCE = new ScanInitializer();


    public static ScanInitializer getInstance() {
        return INSTANCE;
    }

    /**
     * 集成该库必须调用此方法初始化.
     * @param context
     */
    public void init(Context context) {
        DebugUtils.logD(TAG, "init");
        mContext = context;
        mHasInit = true;
        BeepAndVibrate.getInstance().setContext(context, R.raw.beep);
        ResultParserManager.getInstance().setContext(context);

    }

    public boolean isHasInit() {
        return mHasInit;
    }
}

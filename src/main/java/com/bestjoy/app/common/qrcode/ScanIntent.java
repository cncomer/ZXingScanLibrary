package com.bestjoy.app.common.qrcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bestjoy.library.scan.R;


/**
 * Created by bestjoy on 16/3/8.
 */
public class ScanIntent {
    public static final class Encode {
        public static final String ACTION = "com.shwy.bestjoy.android.ENCODE";
        public static final String DATA = "ENCODE_DATA";
        public static final String TYPE = "ENCODE_TYPE";
        public static final String FORMAT = "com.google.zxing.client.android.ENCODE_FORMAT";

        private Encode() {
        }
    }

    public static final class History {
        public static final String ITEM_NUMBER = "ITEM_NUMBER";

        private History() {
        }
    }

    public static final class Scan {
        public static final String ACTION = "com.google.zxing.client.android.SCAN";
        public static final String MODE = "SCAN_MODE";
        public static final String SCAN_FORMATS = "SCAN_FORMATS";
        public static final String CHARACTER_SET = "CHARACTER_SET";
        public static final String PRODUCT_MODE = "PRODUCT_MODE";
        public static final String ONE_D_MODE = "ONE_D_MODE";
        public static final String QR_CODE_MODE = "QR_CODE_MODE";
        public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";
        public static final String RESULT = "SCAN_RESULT";
        public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";
        public static final String SAVE_HISTORY = "SAVE_HISTORY";

        private Scan() {
        }
    }

    public static final class SearchBookContents {
        public static final String ACTION = "com.google.zxing.client.android.SEARCH_BOOK_CONTENTS";
        public static final String ISBN = "ISBN";
        public static final String QUERY = "QUERY";

        private SearchBookContents() {
        }
    }

    public static final class Share {
        public static final String ACTION = "com.google.zxing.client.android.SHARE";

        private Share() {
        }
    }

    public static final class WifiConnect {
        public static final String ACTION = "com.shwy.bestjoy.intent.action.WIFI_CONNECT";
        public static final String SSID = "SSID";
        public static final String TYPE = "TYPE";
        public static final String PASSWORD = "PASSWORD";

        private WifiConnect() {
        }
    }




    public static final void openURL(Context context, String url) {
        launchIntent(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static final void launchIntent(Context context, Intent intent) {
        if (intent != null) {
            try {
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.msg_intent_failed);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }
        }
    }
}

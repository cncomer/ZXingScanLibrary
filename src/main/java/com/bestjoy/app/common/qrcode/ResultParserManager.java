package com.bestjoy.app.common.qrcode;

import android.app.Activity;
import android.content.Context;

import com.bestjoy.app.common.qrcode.result.ParsedResult;
import com.bestjoy.app.common.qrcode.result.ResultHandler;
import com.bestjoy.app.common.qrcode.result.ResultParser;
import com.bestjoy.app.common.qrcode.result.TextParsedResult;
import com.bestjoy.app.common.qrcode.result.TextResultHandler;
import com.bestjoy.library.scan.R;
import com.bestjoy.library.scan.utils.DebugUtils;
import com.google.zxing.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bestjoy on 16/3/8.
 */
class ResultParserManager {

    private static final String TAG = "ResultParserManager";
    protected static Context mContext;
    protected static final List<ResultParser> PARSERS = new ArrayList<ResultParser>();

    protected static final List<String> ResultHandlerClassName = new ArrayList<String>();

    private static ResultParserManager INSTANCE = new ResultParserManager();

    public synchronized static ResultParserManager getInstance() {
        return INSTANCE;
    }

    public synchronized void setInstance(ResultParserManager instance) {
        INSTANCE = instance;
    }

    public void setContext(Context context) {
        mContext = context;
        initParser(mContext);
    }

    protected void initParser(Context context) {
        //初始化解析器
        PARSERS.clear();
        ResultHandlerClassName.clear();
        String[] parsers = context.getResources().getStringArray(R.array.parser);
        Class<?> parserClz = null;
        Object parserObject = null;
        for(String parser: parsers) {
            try {
                parserClz = Class.forName(parser);
                parserObject = parserClz.newInstance();
                if (parserObject instanceof ResultParser) {
                    DebugUtils.logD(TAG, "find ResultParser " + parserClz.getName());
                    PARSERS.add((ResultParser) parserObject);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //初始化处理器类名
        String[] resultHandlers = context.getResources().getStringArray(R.array.parser_handler);
        for(String resultHandler: resultHandlers) {
            ResultHandlerClassName.add(resultHandler);
            DebugUtils.logD(TAG, "find ResultHandlerClassName " + resultHandler);
        }

    }

    public ParsedResult parseResult(Result theResult) {
        if (!ScanInitializer.getInstance().isHasInit()) {
            throw new RuntimeException("You must call ScanInitializer.getInstance().init() in Application");

        }
        for (ResultParser parser : PARSERS) {
            ParsedResult result = parser.parse(theResult);
            if (result != null) {
                return result;
            }
        }
        return new TextParsedResult(theResult.getText().replace("\r\n", "\n"), null);
    }

    public ResultHandler makeResultHandler(Activity activity, Result rawResult) {
        DebugUtils.logD(TAG, "makeResultHandler....");
        ParsedResult result = parseResult(rawResult);
        String type = result.getType().getType();
        ResultHandler resultHandler = null;
        Class<?> parserClz = null;
        Object parserObject = null;
        for(String resultHandlerClassName: ResultHandlerClassName) {
            DebugUtils.logD(TAG, "makeResultHandler try " + resultHandlerClassName + " for result.getType() " + type);
            try {
                parserClz = Class.forName(resultHandlerClassName);
                //检查类AnnotationTest是否含有@ParsedResultTypeAnnotation注解
                if(parserClz.isAnnotationPresent(ParsedResultTypeAnnotation.class)){
                    //若存在就获取注解
                    ParsedResultTypeAnnotation annotation= parserClz.getAnnotation(ParsedResultTypeAnnotation.class);
                    if (annotation.parsedResultTypeName().equals(type)) {
                        Constructor constructor = parserClz.getDeclaredConstructor(new Class[]{Activity.class, ParsedResult.class});
                        if (constructor != null) {
                            parserObject = constructor.newInstance(activity, result);
                            DebugUtils.logD(TAG, "find ResultHandler " + parserClz.getName());
                            resultHandler = (ResultHandler) parserObject;
                        }
                    }
                } else {
                    DebugUtils.logD(TAG, "ParsedResultTypeAnnotation isAnnotationPresent? false");
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (resultHandler == null) {
            //没有找到处理程序，我们当做纯文本处理
            resultHandler = new TextResultHandler(activity, result);
        }
        return resultHandler;
    }

}

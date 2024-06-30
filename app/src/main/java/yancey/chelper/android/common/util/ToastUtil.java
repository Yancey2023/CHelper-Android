package yancey.chelper.android.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class ToastUtil {

    private static Object iNotificationManagerObj;

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {

    }

    private ToastUtil() {

    }

    public static void show(Context context, @StringRes int text) {
        show(context, context.getString(text));
    }

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    @SuppressWarnings("unused")
    public static void show(Context context, @StringRes int text, @Duration int duration) {
        show(context, context.getString(text), duration);
    }

    public static void show(Context context, String text, @Duration int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        //后setText 兼容小米默认会显示app名称的问题
        Toast toast = Toast.makeText(context, null, duration);
        toast.setText(text);
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            toast.show();
        } else {
            showSystemToast(toast);
        }
    }

    /**
     * 显示系统Toast
     */
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("SoonBlockedPrivateApi,PrivateApi")
    private static void showSystemToast(Toast toast) {
        try {
            Method getServiceMethod = Toast.class.getDeclaredMethod("getService");
            getServiceMethod.setAccessible(true);
            //hook INotificationManager
            if (iNotificationManagerObj == null) {
                iNotificationManagerObj = getServiceMethod.invoke(null);

                Class<?> iNotificationManagerCls = Class.forName("android.app.INotificationManager");
                Object iNotificationManagerProxy = Proxy.newProxyInstance(toast.getClass().getClassLoader(), new Class[]{iNotificationManagerCls},
                        (proxy, method, args) -> {
                            // 强制使用系统Toast
                            // 华为p20 pro上为enqueueToastEx
                            if (Objects.equals(method.getName(), "enqueueToast") || Objects.equals(method.getName(), "enqueueToastEx")) {
                                args[0] = "android";
                            }
                            return method.invoke(iNotificationManagerObj, args);
                        });
                Field sServiceFiled = Toast.class.getDeclaredField("sService");
                sServiceFiled.setAccessible(true);
                sServiceFiled.set(null, iNotificationManagerProxy);
            }
            toast.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
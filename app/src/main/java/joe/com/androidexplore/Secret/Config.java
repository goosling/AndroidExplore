package joe.com.androidexplore.Secret;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JOE on 2016/7/19.
 */
public class Config {

    public static final String KEY_TOKEN = "token";
    public static final String APP_ID = "joe.com.android.explore.Secret";
    public static final String CHARSET = "utf-8";

    public static String getCachedToken(Context context) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getString(KEY_TOKEN, null);
    }

    /**
     * 将token缓存
     * @param context
     * @param token
     */
    public static void cachedToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }
}

package joe.com.androidexplore.Secret.net;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import joe.com.androidexplore.Secret.Config;

/**
 * Created by JOE on 2016/7/19.
 */
public class NetConnection {

    public NetConnection(final String url, final HttpMethod method,
                         SuccessCallback successCallback, FailCallback failCallback,
                         final String ...kvs) {
        //防止阻塞主线程
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {

                StringBuffer paramsString = new StringBuffer();
                for(int i=0; i<kvs.length; i += 2) {
                    paramsString.append(kvs[i]).append("=").append(kvs[i+1]).append("&");
                }

                URLConnection urlConnection;

                try{
                    URL url1 = new URL(url);
                    urlConnection = url1.openConnection();
                    switch (method) {
                        case POST:
                            urlConnection.setDoOutput(true);
                            BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(
                                    urlConnection.getOutputStream(), Config.CHARSET
                            ));
                            bw.write(paramsString.toString());

                            break;
                        case GET:

                            break;
                        default:
                            URL url2 = new URL(url + "?" + paramsString.toString());
                            urlConnection = url2.openConnection();
                            break;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }
        };
    }

    public static interface SuccessCallback {
        void onSuccess(String result);
    }

    public static interface FailCallback {
        void onFail();
    }
}

package joe.com.androidexplore.threadTest;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by JOE on 2016/7/18.
 */
public class DownloadFileTask extends AsyncTask<URL, Integer, Long> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
    }

    @Override
    protected Long doInBackground(URL... params) {
        int count = params.length;
        long totalSize = 0;
        for(int i=0; i< count ; i++) {

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}

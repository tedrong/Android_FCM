package rong.ndhu.com.wise;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView webView;
    private String WebServer = "http:///192.168.1.118:3000/";
    private ProgressBar setProgressPercent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl("file:///android_asset/index.html");
        webView.loadUrl(WebServer);

        webView.addJavascriptInterface(this, "wx");



    }





    @android.webkit.JavascriptInterface
    public void actionFromJsWithParam(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, "JS called Native Function...：" + str, Toast.LENGTH_SHORT).show();

                System.out.println("Get in Web call");

                String token = FirebaseInstanceId.getInstance().getToken();


                // Log and toast
                final String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                new MyAsyncTask().execute(str, msg);





            }
        });

    }


    public void postData(String user, String msg){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("user", user).add("token", msg).build();
        Request request = new Request.Builder().url(WebServer+"apptoken").post(formBody).build();

        try{
            Response response = okHttpClient.newCall(request).execute();
        }
        catch(IOException e){
            System.out.println(e.toString());
        }
    }


    private class MyAsyncTask extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... params) {
            postData(params[0], params[1]);
            return null;
        }

        protected void onPostExecute(Double result){
            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(Integer... progress){
            setProgressPercent.setProgress(progress[0]);
        }
    }





    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;

        }
    }//myWebClient




    boolean doubleBackToExitPressedOnce = false;

    @Override
    // This method is used to detect back button
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
        else if(doubleBackToExitPressedOnce) {
            // Let the system handle the back button
            super.onBackPressed();
            return;
        }
        else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }//onBackPressed
}

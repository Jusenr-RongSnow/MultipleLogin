package com.myself.multiplelogin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tencent.connect.common.Constants;
import com.tencent.open.utils.HttpUtils;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {

    private Tencent mTencent;
    private BaseUiListener mUiListener;
    private BaseApiListener mApiListener;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
//        // 其中APP_ID是分配给第三方应用的appid，类型为String。
//        mTencent = Tencent.createInstance(MyApp.APP_ID, this.getApplicationContext());
//        // 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
//        mUiListener = new BaseUiListener();
//        mApiListener = new BaseApiListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

//        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 应用调用Andriod_SDK接口时，
     * 如果要成功接收到回调，需要在调用接口的Activity的onActivityResult。
     * 其中onActivityResultData接口中的listener,
     * 为当前调用的Activity所实现的相应回调UIListener。
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mUiListener);
        //在某些低端机上调用登录后，由于内存紧张导致APP被系统回收，登录成功后无法成功回传数据。
        //解决办法如下:
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                mTencent.handleLoginData(data, mUiListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * activity	调用者activity。应用使用SDK时，会从应用自己的Activity跳转到SDK的Activity，应用调用SDK的Activity即为这里的调用者activity。
     * scope	应用需要获得哪些API的权限，由“，”分隔。
     * 例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
     * listener	回调API，IUiListener实例。
     */
    public void login() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", mUiListener);
        }
    }

    private void doLogin() {
        IUiListener listener = new BaseUiListener() {
            @Override
            protected void doComplete(Object values) {
                super.doComplete(values);
                updateLoginButton();
            }
        };
        mTencent.login(this, "all", listener);
    }

    private void updateLoginButton() {
    }

    /**
     *
     */
    public void logout() {
        mTencent.logout(this);
    }

    /**
     * 获取用户信息-异步方式调用
     * graphPath	要调用的接口名称，通过SDK中的Constant类获取宏定义。
     * params	以K-V组合的字符串参数。Params是一个Bundle类型的参数，里面以键值对（Key-value）的形式存储数据，应用传入的邀请分享等参数就是通过这种方式传递给SDK，然后由SDK发送到后台。
     * httpMethod	使用的http方式，如Constants.HTTP_GET，Constants.HTTP_POST。
     * listener	回调接口，IUiListener实例。
     * state	状态对象，将在回调时原样传回给 listener，供应用识别异步调用。SDK内部不访问该对象。
     */
    public void getUserInfo() {
        mTencent.requestAsync(Constants.LOGIN_INFO, null, Constants.HTTP_GET, mApiListener, null);
    }

    /**
     * 获取用户信息-同步方式调用
     */
    public void getUserInfoInThread() {
        new Thread() {
            @Override
            public void run() {
                JSONObject json = null;

                try {
                    json = mTencent.request(Constants.LOGIN_INFO, null, Constants.HTTP_GET);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (HttpUtils.NetworkUnavailableException e) {
                    e.printStackTrace();
                } catch (HttpUtils.HttpStatusException e) {
                    e.printStackTrace();
                }

                System.out.println(json);
            }
        }.start();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }

    /**
     * 调用SDK已经封装好的接口时，
     * 例如：登录、快速支付登录、应用分享、应用邀请等接口，需传入该回调的实例。
     */
    public class BaseUiListener implements IUiListener {

        protected void doComplete(Object values) {

        }

        @Override
        public void onComplete(Object o) {
            doComplete(o);
        }

        @Override
        public void onError(UiError e) {
            showResult("onError:", "code:" + e.errorCode + ", msg:"
                    + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            showResult("onCancel", "");
        }
    }

    /**
     * 使用requestAsync、request等通用方法调用sdk未封装的接口时，
     * 例如上传图片、查看相册等，需传入该回调的实例。
     */
    public class BaseApiListener implements IRequestListener {

        protected void doComplete(JSONObject response, Object state) {
        }

        @Override
        public void onComplete(JSONObject jsonObject) {
            showResult("IRequestListener.onComplete:", jsonObject.toString());
            doComplete(jsonObject, "");
        }

        @Override
        public void onIOException(IOException e) {
            showResult("IRequestListener.onIOException:", e.getMessage());
        }

        @Override
        public void onMalformedURLException(MalformedURLException e) {
            showResult("IRequestListener.onMalformedURLException", e.toString());
        }

        @Override
        public void onJSONException(JSONException e) {
            showResult("IRequestListener.onJSONException:", e.getMessage());
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e) {
            //TODO Auto-generated method stub
        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e) {
            //TODO Auto-generated method stub
        }

        @Override
        public void onNetworkUnavailableException(HttpUtils.NetworkUnavailableException e) {
            //  当前网络不可用时触发此异常
        }

        @Override
        public void onHttpStatusException(HttpUtils.HttpStatusException e) {
            //  http请求返回码非200时触发此异常
        }

        @Override
        public void onUnknowException(Exception e) {
            //  出现未知错误时会触发此异常
        }
    }

    public void showResult(String methodName, String rusult) {
        Log.e(methodName, rusult);
    }
}

package tw.org.iii.www.iotest3;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {//宣告變數
    private ConnectivityManager mgr;
    private String data ;
    private StringBuffer sb;
    private UIHandler handler;
    private TextView mesg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new UIHandler();
        mesg = (TextView)findViewById(R.id.mesg);

        mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//進行網路連線
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isConnected()){//要寫傳回NULL值 不然會無法執行
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                while (ifs.hasMoreElements()) {
                    NetworkInterface ip = ifs.nextElement();
                    Enumeration<InetAddress> ips = ip.getInetAddresses();
                    while (ips.hasMoreElements()) {
                        InetAddress ia = ips.nextElement();
                        Log.d("brad", ia.getHostAddress());
                    }
                }


            } catch (SocketException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("brad", "NOT Connect");
        }


    }

    public void test1(View v){
//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://www.google.com");
//                    HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
//                    conn.connect();
//                    InputStream in = conn.getInputStream();
//                    int c; StringBuffer sb = new StringBuffer();
//                    while ( (c = in.read()) != -1){
//                        sb.append((char)c);
//                    }
//                    in.close();
//                    Log.d("brad", sb.toString());
//                }catch(Exception ee){
//                    Log.d("brad", ee.toString());
//                }
//            }
//        }.start();
        mesg.setText("");
    MyTread mt1 = new MyTread();
    mt1.start();
}

    private class MyTread extends Thread {//開執行緒
        @Override
        public void run() {//RUN方法
            try {
                URL url = new URL("http://data.coa.gov.tw/Service/OpenData/EzgoTravelFoodStay.aspx");//讀取的網址
                HttpURLConnection conn =  (HttpURLConnection) url.openConnection();//連線
                conn.connect();
                BufferedReader buf =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));//讀取字串或串流
                data = buf.readLine();
                buf.close();
                parseJSON();
                Log.d("brad", data);



//                InputStream in = conn.getInputStream();
//                int c; StringBuffer sb = new StringBuffer();
//                while ( (c = in.read()) != -1){
//                    sb.append((char)c);
//                }
//                in.close();
//                Log.d("brad", sb.toString());
            }catch(Exception ee){
                Log.d("brad", ee.toString());
            }
        }
    }
    private void parseJSON(){//解析JSON資料
        sb = new StringBuffer();
        try{
        JSONArray root =new JSONArray(data);//使用JOSN陣列去抓取資料 如有問題則拋出例外
        for(int i =0; i<root.length();i++) {
            JSONObject row = root.getJSONObject(i);//轉JOSN物件
            String name = row.getString("Name");//要顯示的字串資料
            String addr = row.getString("Address");//要顯示的字串資料
            sb.append(name + " -> " + addr + "\n");//寫入要呈現的JOSN資料
        }
            handler.sendEmptyMessage(0);//把資料秀出來
        }catch (Exception e){
            Log.i("BBB",e.toString());
        }
    }
        private class UIHandler extends Handler{//與執行緒有關連
        @Override
        public void handleMessage(Message msg) {//接收訊息
            super.handleMessage(msg);
            mesg.setText(sb);
        }
    }
    public void test2(View v){

    }
}
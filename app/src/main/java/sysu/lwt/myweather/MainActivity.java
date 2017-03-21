package sysu.lwt.myweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 12136 on 2017/3/21.
 */

public class MainActivity extends AppCompatActivity {
    // 定义需要使用的webService地址
    private static String url = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather";
    RecyclerView recyclerView;
    View view2;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView设置
        recyclerView = (RecyclerView) findViewById(R.id.weather_horizontal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        Button search = (Button) findViewById(R.id.butt);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view2 = view;
                EditText input = (EditText) findViewById(R.id.input);
                String city = input.getText().toString();
                //String id = "0aa70402052742b7a15c03003e69d1d7";
                Map<String, String> params = new HashMap<>();
                params.put("theCityCode", city);
                params.put("theUserID", "");
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    dodo(params);
                } else {
                    Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void dodo(final Map<String, String> params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //http请求要求
                byte[] data = getRequestData(params).toString().getBytes();
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "begin to connection");
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true); //打开输入流，以便从服务器获取数据
                    connection.setDoOutput(true); //打开输出流，以便向服务器提交数据
                    connection.setUseCaches(false); // 不使用缓存
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    //设置请求体的类型是文本类型
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //设置请求体的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    OutputStream out = connection.getOutputStream();
                    out.write(data);
                    int response = connection.getResponseCode(); //状态码为200表示连接成功
                    Log.d("state","The response is: "+ response);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder responses = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responses.append(line);
                    }
                    //Log.i("content", responses.toString());
                    XmlPullParser(responses.toString());
                } catch (IOException e) {
                    Log.i("error", "failed to connect");
                    e.printStackTrace();
                }
                // XmlPullParser操作
                // Message消息传递
            }
        }).start();
    }

    public static StringBuffer getRequestData(Map<String, String> params) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    private void XmlPullParser(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));

            int eventType = parser.getEventType();
            List<String> list = new ArrayList<>();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: //开始读取XML文档
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if ("string".equals(name)) {
                            String str = parser.nextText();
                            Log.i("key", str);
                            list.add(str);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }

            Message message = new Message();
            message.what = 0;
            message.obj = list;
            handler.sendMessage(message);
        } catch (Exception e) {
            Log.i("error", "cannot parse xml");
        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    List<String> list = (List) msg.obj;

                    if (list.get(0).equals("查询结果为空")) {
                        Toast.makeText(MainActivity.this, "当前城市不存在，请重新输入", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    TextView city_name = (TextView) findViewById(R.id.city_name);
                    TextView update_time = (TextView) findViewById(R.id.u_time);
                    TextView wendu = (TextView) findViewById(R.id.wendu_now);
                    TextView total_wd = (TextView) findViewById(R.id.quantian);
                    TextView shidu = (TextView) findViewById(R.id.shidu);
                    TextView air = (TextView) findViewById(R.id.kongqizhiliang);
                    TextView wind = (TextView) findViewById(R.id.fengli);
                    ListView listView = (ListView) findViewById(R.id.list_v);

                    RelativeLayout re1 = (RelativeLayout) findViewById(R.id.id1);
                    RelativeLayout re2 = (RelativeLayout) findViewById(R.id.id2);
                    re1.setVisibility(view2.getVisibility());
                    re2.setVisibility(view2.getVisibility());

                    city_name.setText(list.get(1));
                    String time = list.get(3);
                    update_time.setText(time.substring(time.length()-8, time.length())+" 更新");
                    String weather = list.get(4);
                    if (weather.equals("今日天气实况：暂无实况")) {
                        wendu.setText("暂无实况");
                        shidu.setText("暂无实况");
                        wind.setText("暂无实况");
                    } else {
                        wendu.setText(weather.substring(weather.indexOf("温") + 2, weather.indexOf("风") - 1));
                        shidu.setText("湿度：" + weather.substring(weather.indexOf("度") + 2, weather.length()));
                        wind.setText(weather.substring(weather.indexOf("力") + 2, weather.indexOf("湿") - 1));
                    }
                    String air_q = list.get(5);
                    if (air_q.equals("空气质量：暂无预报；紫外线强度：暂无预报")) {
                        air.setText("暂无实况");
                    } else {
                        air.setText("空气质量：" + air_q.substring(air_q.length() - 2, air_q.length() - 1));
                    }
                    total_wd.setText(list.get(8));

                    String zhishu = list.get(6);
                    List<Map<String, Object>> m = null;
                    if (zhishu.equals("暂无预报")) {
                        String yu = "暂无预报";
                        String[] Name = {"紫外线指数","感冒指数","穿衣指数","洗衣指数","运动指数","空气污染指数"};
                        m = new ArrayList<>();
                        Map<String, Object> temp;
                        for (int i = 0; i < Name.length; i++) {
                            temp = new LinkedHashMap<>();
                            temp.put("z_name", Name[i]);
                            temp.put("z_zhishu", yu);
                            m.add(temp);
                        }
                    } else {
                        String[] str_array = zhishu.split("：|。");
                        m = new ArrayList<>();
                        Map<String, Object> temp;
                        for (int i = 0, j = 1; i < str_array.length && j < str_array.length; i += 2, j += 2) {
                            temp = new LinkedHashMap<>();
                            temp.put("z_name", str_array[i]);
                            temp.put("z_zhishu", str_array[j]);
                            m.add(temp);
                        }
                    }

                    SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, m, R.layout.item,
                            new String[] {"z_name", "z_zhishu"},
                            new int[] {R.id.z_name, R.id.item_zhishu});
                    listView.setAdapter(simpleAdapter);


                    ArrayList<Weather> weather_list = new ArrayList<>();

                    for (int i = 7, j = 8; i < list.size() && j < list.size(); i+=5, j+=5) {
                        String a = list.get(i);
                        String[] x = a.split(" ");
                        String b = list.get(j);
                        Weather wea = new Weather(x[0], x[1], b);
                        weather_list.add(wea);
                    }
                    int length = weather_list.size();
                    System.out.println("长度：" + length);
                    WeatherAdapter adapter = new WeatherAdapter(MainActivity.this, weather_list);
                    recyclerView.setAdapter(adapter);

                    break;
            }
        }
    };
}

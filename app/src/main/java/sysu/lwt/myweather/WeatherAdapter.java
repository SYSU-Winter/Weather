package sysu.lwt.myweather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 12136 on 2017/3/21.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private ArrayList<Weather> weather_list;
    private LayoutInflater mInflater;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, Weather item);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public WeatherAdapter(Context context, ArrayList<Weather> items) {
        super();
        weather_list = items;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * WeatherAdapter是继承自RecyclerView.Adapter的，必须重写
     * onCreateViewHolder(), onBindViewHolder(), getItemCount()方法
     */

    // 用于创建ViewHolder实例，在这个方法中，将weather_item布局加载进来
    // 然后创建一个ViewHolder实例，并把加载出来的布局传入到构造函数中，
    // 最后将ViewHolder的实例返回
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.weather_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.Date = (TextView) view.findViewById(R.id.w_day);
        holder.Weather_description = (TextView) view.findViewById(R.id.w_weather);
        holder.Temperature = (TextView) view.findViewById(R.id.w_wendu);
        return holder;
    }

    // 用于对RecyclerView子项的数据进行赋值，
    // 会在每个子项被滚动到屏幕内时执行
    // 这里通过参数i得到当前项的weather实例，然后再将数据设置到ViewHolder的TextView中
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.Date.setText(weather_list.get(i).getData());
        viewHolder.Weather_description.setText(weather_list.get(i).getWeather_description());
        viewHolder.Temperature.setText(weather_list.get(i).getTemperature());
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i, weather_list.get(i));
                }
            });
        }
    }

    // 用于告诉RecyclerView一共有多少子项
    @Override
    public int getItemCount() {
        return weather_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder的构造函数中传入一个View参数，这个参数就是RecyclerView子项
        // 的最外层布局，这样
        // 我们就可以通过findViewById()的方法来获取到布局中的各种控件的实例了
        public ViewHolder(View itemView) {
            super(itemView);
        }
        TextView Date;
        TextView Weather_description;
        TextView Temperature;
    }
}


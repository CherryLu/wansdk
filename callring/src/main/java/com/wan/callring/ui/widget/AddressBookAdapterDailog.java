package com.wan.callring.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wan.callring.R;
import com.wan.callring.bean.Contact;

import java.util.List;

/**
 * 同一人多个号码的情况
 * @author zgb
 */
public class AddressBookAdapterDailog extends BaseAdapter {
	private List<Contact> list = null;
	private Context mContext;
	public AddressBookAdapterDailog(Context mContext, List<Contact> list) {
		this.mContext = mContext;
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<Contact> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {

		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
        if(list.size()==0){
            return view;
        }
		ViewHolder viewHolder = null;
		final Contact phoneBean = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.item_choose_phonenum_layout, null);
			viewHolder.tv_phone = (TextView) view.findViewById(R.id.textView_phone);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tv_phone.setText(phoneBean.getNumber());
		return view;

	}

	class ViewHolder {
		TextView tv_phone;
	}


}

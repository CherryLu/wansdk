package com.wan.callring.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.wan.callring.R;
import com.wan.callring.bean.Contact;
import com.wan.callring.bean.ContactBean;
import com.wan.callring.ui.widget.ChooseCallNumDialog;
import com.wan.callring.ui.widget.ChooseCallTypeDialog;
import com.wan.callring.utils.CharacterParser;
import com.wan.callring.utils.Preferences;

import java.util.List;


/**
 * 
 * @author zgb
 */
public class AddressBookAdapter extends BaseAdapter implements SectionIndexer {
	private List<ContactBean> list = null;
	private Context mContext;
	Dialog noWifiDialog;
	CharacterParser characterParser = CharacterParser.getInstance();
	public AddressBookAdapter(Context mContext, List<ContactBean> list) {
		this.mContext = mContext;
		this.list = list;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<ContactBean> list) {
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
		final ContactBean phoneBean = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.callshow_phone_item_adapter, null);
			viewHolder.catalog = (TextView) view.findViewById(R.id.catalog);
			viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			viewHolder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.catalog.setVisibility(View.VISIBLE);
			viewHolder.catalog.setText(phoneBean.getSortLetters());
		} else {
			viewHolder.catalog.setVisibility(View.GONE);
		}
		Log.e("getSortLetters",""+phoneBean.getSortLetters()+"--"+phoneBean.getName());
		if(phoneBean.spanableType==0){
			SpannableStringBuilder spannableString = new SpannableStringBuilder(phoneBean.getPhone());
			int index =phoneBean.getPhone().indexOf(phoneBean.spannableString);
			if(index!=-1)
			spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_orange)), index,index+phoneBean.spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.tv_phone.setText(spannableString);
			viewHolder.tv_name.setText(phoneBean.getName());
		}else if(phoneBean.spanableType==1){
			SpannableStringBuilder spannableString = new SpannableStringBuilder(phoneBean.getName());
			int index =phoneBean.spannableString.length();
			spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_orange)), 0,index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.tv_name.setText(spannableString);
			viewHolder.tv_phone.setText(phoneBean.getPhone());
		}else if(phoneBean.spanableType==2){
			String name = phoneBean.getName();
			int index = characterParser.getSpanSellingIndex(name,phoneBean.spannableString);
			SpannableStringBuilder spannableString = new SpannableStringBuilder(name);
			spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_orange)), 0,index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.tv_name.setText(spannableString);
			viewHolder.tv_phone.setText(phoneBean.getPhone());
		}else {
			viewHolder.tv_name.setText(phoneBean.getName());
			viewHolder.tv_phone.setText(phoneBean.getPhone());
		}
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//发起语音的时候，判断是否为WiFi环境，若不是给予提示
//				if(!NetworkUtil.isWifi(mContext)){
//					noWifiDialog = DialogFactoryUtils.noWifiDialog(mContext);
//					noWifiDialog.show();
//					TextView tvNoWifiContinue = (TextView) noWifiDialog.findViewById(R.id.no_wifi_continue);
//					tvNoWifiContinue.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View view) {
//							noWifiDialog.dismiss();
//							BackGroundCallState msg = new BackGroundCallState(LittlecService.CALL_MAKE_AUDIO);
//							msg.call_number = contact.getUserName();
//							msg.call_show_name = contact.getContactName();
//							msg.callType = VoIPConstant.CallType.CALLTYPE_1V1_AUDIO;
//							EventBus.getDefault().post(msg);
//						}
//					});
//
//				} else {
				String myMobile = Preferences.getString(Preferences.UserMobile,null);
				if(TextUtils.isEmpty(myMobile)){
					//未登录
					return;
				}
				if(phoneBean.contactList.size()>1){
					showChooseCallNumDialog(phoneBean.getName(),phoneBean.contactList);
				}else {
					showChooseCallTypeDialog(phoneBean.getName(),phoneBean.getPhone().replace(" ",""));
				}

//					BackGroundCallState msg = new BackGroundCallState(StartHuanXinService.CALL_MAKE_AUDIO);
//					msg.call_number = phoneBean.getPhone().replace(" ","");
//					msg.call_show_name = phoneBean.getPhone().replace(" ","");
//					msg.callType = CallType.CALLTYPE_1V1_VIDEO;
//					EventBus.getDefault().post(msg);
			}
		});
		return view;

	}

	class ViewHolder {
		TextView tv_name;
		TextView tv_phone;
		TextView catalog;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}


	ChooseCallTypeDialog chooseCallTypeDialog;
	String name;
	String phone;
	public void showChooseCallTypeDialog(String name, String phone){
		this.name = name;
		this.phone = phone;
		if (chooseCallTypeDialog==null){
			chooseCallTypeDialog = new ChooseCallTypeDialog(mContext);
			chooseCallTypeDialog.setOnSelectListener(onItemSelectedListener);
		}
		chooseCallTypeDialog.setTextView(name+"["+phone+"]");
		chooseCallTypeDialog.show();
	}

	private ChooseCallTypeDialog.onItemSelectedListener onItemSelectedListener = new ChooseCallTypeDialog.onItemSelectedListener() {
		@Override
		public void onSelected(int item) {
			switch (item){
				case 0:

					break;
				case 1:

					break;
			}
		}
	};


	ChooseCallNumDialog chooseCallNumDialog;
	public void showChooseCallNumDialog(String name, List<Contact> list){
		if (chooseCallNumDialog==null){
			chooseCallNumDialog = new ChooseCallNumDialog(mContext);
			chooseCallNumDialog.setOOnChooseNumListener(new ChooseCallNumDialog.OnChooseNumListener() {
				@Override
				public void onChoosed(String name, String phone) {
					showChooseCallTypeDialog(name,phone);
				}
			});
		}
		chooseCallNumDialog.setTextView(name);
		chooseCallNumDialog.setListview(list);
		chooseCallNumDialog.show();
	}
}

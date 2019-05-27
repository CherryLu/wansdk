package com.wan.callring.ui.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wan.callring.R;
import com.wan.callring.bean.ContactBean;
import com.wan.callring.ui.BaseFragment;
import com.wan.callring.ui.adapter.KeyboardAdapter;
import com.wan.callring.ui.widget.banner.Banner;
import com.wan.callring.utils.Cn2Spell;
import com.wan.callring.utils.Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyBoardFragment extends BaseFragment implements View.OnClickListener {
    ImageButton imageButton_1;
    ImageButton imageButton_2;
    ImageButton imageButton_3;
    ImageButton imageButton_4;
    ImageButton imageButton_5;
    ImageButton imageButton_6;
    ImageButton imageButton_7;
    ImageButton imageButton_8;
    ImageButton imageButton_9;
    ImageButton imageButton_0;
    ImageButton imageButton_10;
    ImageButton imageButton_11;
    ImageButton imageButton_call;
    ListView listview;

    Banner view_banner;

    EditText editText;
    ImageView iv_backspace;
    RelativeLayout rl_dowm;
    ImageView imageview_bottom_keyboard;
    LinearLayout ll_keyboard;

    KeyboardAdapter keyboardAdapter;

    List<ContactBean> list = new ArrayList<ContactBean>();

    String[] chareacters = new String[]{"abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};

    AsyncQueryHandler asyncQueryHandler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_keyboard,container,false);
        Preferences.init(getContext());
        initView();
        return mRootView;
    }


    private void initView(){

        view_banner = (Banner)mRootView.findViewById(R.id.view_banner);
        imageButton_1 = (ImageButton)mRootView.findViewById(R.id.imageButton_1);
        imageButton_2 = (ImageButton)mRootView.findViewById(R.id.imageButton_2);
        imageButton_3 = (ImageButton)mRootView.findViewById(R.id.imageButton_3);
        imageButton_4 = (ImageButton)mRootView.findViewById(R.id.imageButton_4);
        imageButton_5 = (ImageButton)mRootView.findViewById(R.id.imageButton_5);
        imageButton_6 = (ImageButton)mRootView.findViewById(R.id.imageButton_6);
        imageButton_7 = (ImageButton)mRootView.findViewById(R.id.imageButton_7);
        imageButton_8 = (ImageButton)mRootView.findViewById(R.id.imageButton_8);
        imageButton_9 = (ImageButton)mRootView.findViewById(R.id.imageButton_9);
        imageButton_0 = (ImageButton)mRootView.findViewById(R.id.imageButton_0);
        imageButton_10 = (ImageButton)mRootView.findViewById(R.id.imageButton_10);
        imageButton_11 = (ImageButton)mRootView.findViewById(R.id.imageButton_11);
        imageButton_call = (ImageButton)mRootView.findViewById(R.id.imageButton_call);
        listview = (ListView)mRootView.findViewById(R.id.listview);
        editText = (EditText)mRootView.findViewById(R.id.ed_mun);
        iv_backspace = (ImageView)mRootView.findViewById(R.id.iv_backspace);
        ll_keyboard = (LinearLayout)mRootView.findViewById(R.id.ll_keyboard);
        rl_dowm = (RelativeLayout) mRootView.findViewById(R.id.rl_dowm);
        imageview_bottom_keyboard = (ImageView)mRootView.findViewById(R.id.imageview_bottom_keyboard);
        editText.setInputType(InputType.TYPE_NULL);
        imageButton_1.setOnClickListener(this);
        imageButton_2.setOnClickListener(this);
        imageButton_3.setOnClickListener(this);
        imageButton_4.setOnClickListener(this);
        imageButton_5.setOnClickListener(this);
        imageButton_7.setOnClickListener(this);
        imageButton_6.setOnClickListener(this);
        imageButton_8.setOnClickListener(this);
        imageButton_9.setOnClickListener(this);
        imageButton_0.setOnClickListener(this);
        imageButton_10.setOnClickListener(this);
        imageButton_11.setOnClickListener(this);
        imageButton_call.setOnClickListener(this);
        iv_backspace.setOnClickListener(this);
        imageview_bottom_keyboard.setOnClickListener(this);
        ll_keyboard.setOnClickListener(this);
        rl_dowm.setOnClickListener(this);

        keyboardAdapter = new KeyboardAdapter(getActivity(),new ArrayList<ContactBean>());
        listview.setAdapter(keyboardAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>0){
                    iv_backspace.setVisibility(View.VISIBLE);
//                        doSearch(charSequence.toString());
                    ArrayList<ContactBean> fileterList = (ArrayList<ContactBean>) doSearch(charSequence.toString());
                    keyboardAdapter.updateListView(fileterList);
                }else {
                    iv_backspace.setVisibility(View.GONE);
                    keyboardAdapter.updateListView(new ArrayList<ContactBean>());//list
                }
                listview.setSelection(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        asyncQueryHandler = new MyAsyncQueryHandler(getActivity().getContentResolver());
        //网络请求  Banner
        init();
    }


    /**
     * 初始化数据库查询参数
     */
    private void init() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");

    }


    /**获取EditText光标所在的位置*/
    private int getEditTextCursorIndex(EditText mEditText){
        return mEditText.getSelectionStart();
    }
    /**向EditText指定光标位置插入字符串*/
    private void insertText(EditText mEditText, String mText){
        mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
    }
    /**向EditText指定光标位置删除字符串*/
    private void deleteText(EditText mEditText){
        if(mEditText.getText().toString()!=null&&mEditText.getText().toString().length()>0){
            mEditText.getText().delete(getEditTextCursorIndex(mEditText)-1, getEditTextCursorIndex(mEditText));
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.imageButton_1) {
            insertText(editText, "1");
        } else if (i == R.id.imageButton_2) {
            insertText(editText, "2");
        } else if (i == R.id.imageButton_3) {
            insertText(editText, "3");
        } else if (i == R.id.imageButton_4) {
            insertText(editText, "4");
        } else if (i == R.id.imageButton_5) {
            insertText(editText, "5");
        } else if (i == R.id.imageButton_6) {
            insertText(editText, "6");
        } else if (i == R.id.imageButton_7) {
            insertText(editText, "7");
        } else if (i == R.id.imageButton_8) {
            insertText(editText, "8");
        } else if (i == R.id.imageButton_9) {
            insertText(editText, "9");
        } else if (i == R.id.imageButton_0) {
            insertText(editText, "0");
        } else if (i == R.id.imageButton_10) {
            insertText(editText, "*");
        } else if (i == R.id.imageButton_11) {
            insertText(editText, "#");
        } else if (i == R.id.imageButton_call) {
         /*   if (editText.getText().toString() != null && !"".equals(editText.getText().toString() + "")) {
//                    Preferences.saveString(Preferences.CALLSHOW_NUM,""+editText.getText().toString());
//                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+editText.getText().toString()+""));
//                    startActivity(intent);
                String myMobile = Preferences.getString(Preferences.UserMobile, null);
                if (TextUtils.isEmpty(myMobile)) {
                    activity.startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                showChooseCallTypeDialog(editText.getText().toString(), editText.getText().toString());
//                    BackGroundCallState msg = new BackGroundCallState(StartHuanXinService.CALL_MAKE_AUDIO);
//                    msg.call_number = editText.getText().toString();
//                    msg.call_show_name = editText.getText().toString();
//                    msg.callType = CallType.CALLTYPE_1V1_VIDEO;
//                    EventBus.getDefault().post(msg);
            } else {
                Toast.makeText(CallPhoneFragment_keyboard.this.getActivity(), "请输入号码", Toast.LENGTH_SHORT).show();
            }*/
        } else if (i == R.id.iv_backspace) {
            deleteText(editText);
        } else if (i == R.id.imageview_bottom_keyboard) {
            imageview_bottom_keyboard.setVisibility(View.GONE);
            ll_keyboard.setVisibility(View.VISIBLE);
        } else if (i == R.id.rl_dowm) {
            imageview_bottom_keyboard.setVisibility(View.VISIBLE);
            ll_keyboard.setVisibility(View.GONE);
        }
    }

    /**
     * 模糊查询
     * @param str
     * @return
     */
    private List<ContactBean> doSearch(String str) {
        List<ContactBean> filterList = new ArrayList<ContactBean>();// 过滤后的list
        //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (ContactBean contact : list) {
                if (contact.getPhone() != null && contact.getName() != null) {
                    if (contact.simpleNumber.contains(simpleStr) ) {//|| contact.getName().contains(str)
                        if (!filterList.contains(contact)) {
                            contact.spanableType =0;
                            contact.spannableString = simpleStr;
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        for(int i=0;i<list.size();i++){
            ContactBean contact = list.get(i);
            String pinyin = contact.getPinyin();
            if(pinyin.length()>=str.length()){
                boolean isContains = true;
                String spannableString = "";
                for(int j=0;j<str.length();j++){
                    String ch = str.substring(j, j + 1);
                    if(ch.equals("2")||ch.equals("3")||ch.equals("4")||ch.equals("5")||ch.equals("6")||
                            ch.equals("7")||ch.equals("8")||ch.equals("9")){
                        String chareacte = chareacters[Integer.parseInt(ch)-2];
                        if(!(chareacte+ch).contains((pinyin.charAt(j)+"").toLowerCase())){
                            isContains =false;
                        }else{
                            spannableString+=pinyin.charAt(j)+"";
                        }
                    }else{
                        if(!ch.equals((pinyin.charAt(j)+"").toLowerCase())){
                            isContains =false;
                        }else{
                            spannableString+=pinyin.charAt(j)+"";
                        }
                    }
                }
                if(isContains){
                    if (!filterList.contains(contact)) {
                        contact.spanableType = 1;
                        contact.spannableString = spannableString;
                        filterList.add(contact);
                    }
                }

            }

            String allPinyin = contact.getAllPinyin();
            if(allPinyin.length()>=str.length()){
                boolean isContains = true;
                String spannableString = "";
                for(int j=0;j<str.length();j++){
                    String ch = str.substring(j, j + 1);
                    if(ch.equals("2")||ch.equals("3")||ch.equals("4")||ch.equals("5")||ch.equals("6")||
                            ch.equals("7")||ch.equals("8")||ch.equals("9")){
                        String chareacte = chareacters[Integer.parseInt(ch)-2];
                        if(!(chareacte+ch).contains((allPinyin.charAt(j)+"").toLowerCase())){
                            isContains =false;
                        }else {
                            spannableString+=allPinyin.charAt(j)+"";
                        }
                    }else{
                        if(!ch.equals((allPinyin.charAt(j)+"").toLowerCase())){
                            isContains =false;
                        }else{
                            spannableString+=allPinyin.charAt(j)+"";
                        }
                    }
                }
                if(isContains){
                    if (!filterList.contains(contact)) {
                        contact.spanableType = 2;
                        contact.spannableString = spannableString;
                        filterList.add(contact);
                    }
                }
            }
        }


        if(filterList.size()>1){
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(filterList, comparator);
        }
        return filterList;
    }

    class PinyinComparator implements Comparator {

        @Override
        public int compare(Object o, Object t1) {
            ContactBean contact1 = (ContactBean) o;
            ContactBean contact2 = (ContactBean) t1;
            //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
            if (contact1.getSortLetters().equals("@")
                    || contact2.getSortLetters().equals("#")) {
                return -1;
            } else if (contact1.getSortLetters().equals("#")
                    || contact2.getSortLetters().equals("@")) {
                return 1;
            } else {
                return contact1.getSortLetters().compareTo(contact2.getSortLetters());
            }
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, final Cursor cursor) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (cursor != null && cursor.getCount() > 0) {
                        Map<Integer, ContactBean> contactIdMap = new HashMap<Integer, ContactBean>();
                        cursor.moveToFirst(); // 游标移动到第一项
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            String name = cursor.getString(1);
                            String number = cursor.getString(2);
                            String sortKey = cursor.getString(3);
                            int contactId = cursor.getInt(4);
                            Long photoId = cursor.getLong(5);
                            String lookUpKey = cursor.getString(6);

                            if (contactIdMap.containsKey(contactId)) {
                                // 无操作
                            } else {
                                // 创建联系人对象
                                ContactBean contact = new ContactBean();
                                contact.setName(name.trim().replace(" ",""));
                                contact.setPhone(number.trim().replace(" ",""));
                                contact.setSortLetters(sortKey);

//                        contact.setPhotoId(photoId);
//                        contact.setLookUpKey(lookUpKey);
                                list.add(contact);

                                contactIdMap.put(contactId, contact);
                            }
                        }
                        if (list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                // 汉字转换成拼音
                                String sortLe = list.get(i).getSortLetters();
                                Log.e("repickStr",sortLe);
                                String reg = "[\u4e00-\u9fa5]";
                                Pattern pat = Pattern.compile(reg);
                                Matcher mat=pat.matcher(sortLe);
                                String pinyi = mat.replaceAll("");
                                String[] repickStr = pinyi.split(" ");
                                String pinyin="";
                                String allPy="";
                                for(int m=0;m<repickStr.length;m++){
                                    Log.e("repickStr",repickStr[m]);
                                    if(!TextUtils.isEmpty(repickStr[m])){
                                        pinyin =pinyin+repickStr[m].substring(0, 1);
                                        allPy = allPy+repickStr[m];
                                    }
                                }
                                if(list.get(i).getSortLetters().equals(list.get(i).getName())){
                                    pinyin = Cn2Spell.getPinYinHeadChar(list.get(i).getName());
                                    allPy = Cn2Spell.getPinYin(list.get(i).getName());
                                }
//                         = characterParser.getHeadSelling(list.get(i).getName());
//                        String allPy = characterParser.getSelling(list.get(i).getName());
//                        if(list.get(i).getName().equals("姣")){
//                            pinyin.replace("z","j");
//                            allPy.replace("zuo","jiao");
//                        }
                                list.get(i).setPinyin(pinyin);
                                list.get(i).setAllPinyin(allPy);
                                Log.e("sortString",""+pinyin+" : "+allPy);
                                if(TextUtils.isEmpty(pinyin)){
                                    list.get(i).setSortLetters("#");
                                }else{
                                    String sortString = pinyin.substring(0, 1).toUpperCase();
                                    // 正则表达式，判断首字母是否是英文字母
                                    if (sortString.matches("[A-Z]")) {
                                        Log.e("sortString",""+list.get(i).getName()+"  "+sortString.toUpperCase());
                                        list.get(i).setSortLetters(sortString.toUpperCase());
                                    } else {
                                        list.get(i).setSortLetters("#");
                                    }
                                }
                            }
                            Collections.sort(list, new PinyinComparator());
//                    addressBookAdapter.updateListView(list);
//                    tv_nocontact.setVisibility(View.INVISIBLE);
                        }else{
//                    tv_nocontact.setVisibility(View.VISIBLE);
                        }
                        cursor.close();
                    }
                }
            }).start();

            super.onQueryComplete(token, cookie, cursor);
        }

    }
}

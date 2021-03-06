package com.wan.callring.ui.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wan.callring.R;
import com.wan.callring.bean.Contact;
import com.wan.callring.bean.ContactBean;
import com.wan.callring.ui.adapter.AddressBookAdapter;
import com.wan.callring.ui.adapter.ChooseAddressBookAdapter;
import com.wan.callring.ui.callback.SelectCallBack;
import com.wan.callring.ui.fragment.ContactFragment;
import com.wan.callring.ui.widget.SideBar;
import com.wan.callring.utils.Cn2Spell;
import com.wan.callring.utils.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChoseConstantActivity extends BaseActivity implements View.OnClickListener, SelectCallBack {

    ListView lv_addressbook;
    ChooseAddressBookAdapter addressBookAdapter;
    TextView tv_nocontact;
    List<ContactBean> list = new ArrayList<ContactBean>();
    SideBar sideBar;

    EditText et_search;
    ImageView ivClearText;
    TextView confirm;


    AsyncQueryHandler asyncQueryHandler;

    private List<ContactBean> contactBeans = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constant);
        initView();
    }


    private void initView(){
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        lv_addressbook = (ListView)findViewById(R.id.lv_addressbook);
        tv_nocontact = (TextView)findViewById(R.id.tv_nocontact);
        sideBar = (SideBar)findViewById(R.id.sidrbar);
        et_search = (EditText)findViewById(R.id.et_search);
        ivClearText = (ImageView)findViewById(R.id.ivClearText);
        addressBookAdapter = new ChooseAddressBookAdapter(this,list);
        addressBookAdapter.setSelectCallBack(this);
        lv_addressbook.setAdapter(addressBookAdapter);

        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if(lv_addressbook.getChildCount()==0){
                    return;
                }
                // 该字母首次出现的位置
                int position = addressBookAdapter.getPositionForSection(s.charAt(0));

                if (position != -1) {
                    lv_addressbook.setSelection(position);
                }

            }
        });

        asyncQueryHandler = new ChoseConstantActivity.MyAsyncQueryHandler(getContentResolver());
        /**清除输入字符**/
        ivClearText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                et_search.setText("");
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable e) {

                String content = et_search.getText().toString();
                if ("".equals(content)) {
                    ivClearText.setVisibility(View.INVISIBLE);
                } else {
                    ivClearText.setVisibility(View.VISIBLE);
                }
                if (content.length() > 0) {
                    ArrayList<ContactBean> fileterList = (ArrayList<ContactBean>) search(content);
                    addressBookAdapter.updateListView(fileterList);
                    //mAdapter.updateData(mContacts);
                } else {
                    addressBookAdapter.updateListView(list);
                }
                lv_addressbook.setSelection(0);

            }

        });


        init();

    }

    /**
     * 初始化数据库查询参数
     */
    private void init() {

        Uri uri = ContactsContract.Contacts.CONTENT_URI; // 联系人Uri；//ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY ,
                ContactsContract.CommonDataKinds.Phone.EXTRA_ADDRESS_BOOK_INDEX_COUNTS};
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, null, null, null,
                "sort_key COLLATE LOCALIZED asc");
    }

    /**
     * 模糊查询
     * @param str
     * @return
     */
    private List<ContactBean> search(String str) {
        List<ContactBean> filterList = new ArrayList<ContactBean>();// 过滤后的list
        //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (ContactBean contact : list) {
                if (contact.getPhone() != null && contact.getName() != null) {
                    if (contact.simpleNumber.contains(simpleStr) || contact.getName().contains(str)) {
                        if (!filterList.contains(contact)) {
//                            contact.spanableType =0;
//                            contact.spannableString = simpleStr;
                            filterList.add(contact);
                        }
                    }
                }
            }
        }else {
            for (ContactBean contact : list) {
                if (contact.getPhone() != null && contact.getName() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    boolean isNameContains = contact.getName().toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));

                    boolean isSortKeyContains = contact.getSortLetters().toLowerCase(Locale.CHINESE).replace(" ", "")
                            .contains(str.toLowerCase(Locale.CHINESE));

                    boolean isSimpleSpellContains = contact.sortToken.simpleSpell.toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));

                    boolean isWholeSpellContains = contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE)
                            .contains(str.toLowerCase(Locale.CHINESE));
                    boolean pinyinContains = contact.getPinyin().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE));
                    boolean allpinyinContains = contact.getAllPinyin().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE));
                    if (isNameContains || isSortKeyContains || isSimpleSpellContains || isWholeSpellContains||pinyinContains||allpinyinContains) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.confirm) {//确定 带数据返回
        }
    }

    @Override
    public void selectedConstant(ContactBean contactBean) {//选中
        contactBeans.add(contactBean);
    }

    @Override
    public void unselectedConstant(ContactBean contactBean) {//取消
        contactBeans.remove(contactBean);
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
                    List<ContactBean> listBean = new ArrayList<ContactBean>();
                    if (cursor != null && cursor.getCount() > 0) {
                        Map<Integer, ContactBean> contactIdMap = new HashMap<Integer, ContactBean>();
                        cursor.moveToFirst(); // 游标移动到第一项
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));//1
//                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1));
                            String sortKey = cursor.getString(cursor.getColumnIndex("sort_key"));
                            int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            if (contactIdMap.containsKey(contactId)) {
                                // 无操作
                            } else {
                                // 创建联系人对象
                                ContactBean contact = new ContactBean();
                                contact.setName(name);
//                        contact.setPhone(number);
                                contact.setSortLetters(sortKey);
//                        contact.setPhotoId(photoId);
//                        contact.setLookUpKey(lookUpKey);
                                listBean.add(contact);

                                contactIdMap.put(contactId, contact);
                                // 查看联系人有多少个号码，如果没有号码，返回0
                                int phoneCount = cursor
                                        .getInt(cursor
                                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                                if (phoneCount > 0) {
                                    Cursor phoneCursor =null;
                                    try{
                                        // 获得联系人的电话号码列表
                                        phoneCursor = getContentResolver().query(
                                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                null,
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                        + "=" + contactId, null, null);
                                        if(phoneCursor.moveToFirst())
                                        {
                                            do
                                            {
                                                //遍历所有的联系人下面所有的电话号码
                                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                                Contact contactBean = new Contact();
                                                contactBean.setDisplay_name(name);
                                                contactBean.setNumber(phoneNumber);
                                                if(contact.getPhone() == null){
                                                    contact.setPhone(phoneNumber);
                                                }
                                                contact.contactList.add(contactBean);
                                            }while(phoneCursor.moveToNext());
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }finally {
                                        if(phoneCursor!=null){
                                            phoneCursor.close();
                                        }
                                    }

                                }
                            }
                        }
                        if (listBean.size() > 0) {
//                    Collections.sort(list, pinyinComparator);
                            for (int i = 0; i < listBean.size(); i++) {
                                // 汉字转换成拼音
                                String sortLe = listBean.get(i).getSortLetters();
//                        Log.e("repickStr",sortLe);
                                String reg = "[\u4e00-\u9fa5]";
                                Pattern pat = Pattern.compile(reg);
                                Matcher mat=pat.matcher(sortLe);
                                String[] repickStr = mat.replaceAll("").split(" ");
                                String pinyin="";
                                String allPy="";
                                for(int m=0;m<repickStr.length;m++){
//                            Log.e("repickStr",repickStr[m]);
                                    if(!TextUtils.isEmpty(repickStr[m])){
                                        pinyin =pinyin+repickStr[m].substring(0, 1);
                                        allPy = allPy+repickStr[m];
                                    }
                                }

                                if(listBean.get(i).getSortLetters().equals(listBean.get(i).getName())){
                                    pinyin = Cn2Spell.getPinYinHeadChar(listBean.get(i).getName());
                                    allPy = Cn2Spell.getPinYin(listBean.get(i).getName());
                                }

                                listBean.get(i).setPinyin(pinyin);
                                listBean.get(i).setAllPinyin(allPy);
//                        String pinyin = characterParser.getSelling(list.get(i).getName());
                                if(TextUtils.isEmpty(pinyin)){
                                    listBean.get(i).setSortLetters("#");
                                }else {
                                    String sortString = pinyin.substring(0, 1).toUpperCase();
                                    // 正则表达式，判断首字母是否是英文字母
                                    if (sortString.matches("[A-Z]")) {
                                        listBean.get(i).setSortLetters(sortString.toUpperCase());
                                    } else {
                                        listBean.get(i).setSortLetters("#");
                                    }
                                }
                            }

                            Collections.sort(listBean, new PinyinComparator());
                            list = listBean;
                            asyncQueryHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    addressBookAdapter.updateListView(list);
                                    tv_nocontact.setVisibility(View.INVISIBLE);
                                }
                            });

                        }else{
                            asyncQueryHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_nocontact.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                        cursor.close();
                    }
                }
            }).start();

            super.onQueryComplete(token, cookie, cursor);
        }

    }
}

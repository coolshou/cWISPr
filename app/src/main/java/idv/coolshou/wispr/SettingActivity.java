package idv.coolshou.wispr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import idv.coolshou.wispr.pref.ServiceItem;
import idv.coolshou.wispr.pref.ServiceItemList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/14/12
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingActivity extends Activity {

    private ArrayList<HashMap<String,String>> settingList = new ArrayList<HashMap<String,String>>();
    ListView serviceList = null;

    ServiceItemList service_pref;
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        setContentView(R.layout.setting);

        serviceList = (ListView)findViewById(R.id.serviceList);

        service_pref = new ServiceItemList(this.getApplicationContext());
        service_pref.loadFromPref();

        buildSettingList();

        Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(mAddButtonClickListener);

        CheckBox checkActive = (CheckBox)findViewById(R.id.buttonActive);
        checkActive.setChecked( service_pref.isActive() );
        checkActive.setOnCheckedChangeListener(mActiveChangeListener);
        
        registerForContextMenu(serviceList);
    }
    
    static final private int MENU_MODIFY = 0;
    static final private int MENU_DELETE = 1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (view.getId() == R.id.serviceList) {
            menu.setHeaderTitle(R.string.menuContextHeader);
            menu.add(Menu.NONE, MENU_MODIFY, MENU_MODIFY, R.string.menuContextModify);
            menu.add(Menu.NONE, MENU_DELETE, MENU_DELETE, R.string.menuContextDelete);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case MENU_DELETE:
                onContextMenuDelete(info.position);
                break;
            case MENU_MODIFY:
                onContextMenuModify(info.position);
                break;
        }
        return true;

    }
    
    private void onContextMenuModify(int pos) {
        ServiceItem item = service_pref.getAt(pos);
        if (item == null)
            return;
        String ssid = item.getSSID();
        int iTaiwanIdx = -1;
        for (int i = 0; i < TwnServices.length; i++) {
            if (ssid.equals(TwnServices[i])) {
                iTaiwanIdx = i;
                break;
            }
        }
        
        if (iTaiwanIdx != -1) {
            if (mTaiwanEditingDialog == null)
                mTaiwanEditingDialog = createEditingDialogTwn();
            mCurrentDialog = mTaiwanEditingDialog;
            editUserTwn.setText(item.getUsername());
            editPassTwn.setText(item.getPassword());
            spinnerServiceTwn.setSelection(iTaiwanIdx);
        }
        else {
            if (mGenericEditingDialog == null)
                mGenericEditingDialog = createEditingDialog();
            mCurrentDialog = mGenericEditingDialog;

            editService.setText(item.getServiceName());
            editSSID.setText(item.getSSID());
            editUser.setText(item.getUsername());
            editPass.setText(item.getPassword());
            checkCase.setChecked(item.isCaseSensitive());
            checkFullMatch.setChecked(item.isFullyMatch());
        }
        mEditingEntry = pos;
        mCurrentDialog.show();
    }
    
    private void onContextMenuDelete(int pos) {
        if (pos < 0 || pos >= settingList.size())
            return;
        service_pref.deleteItem(pos);
        buildSettingList();
        service_pref.saveToPref();
    }

    private void onAddButtonTwnClick() {
        if (mTaiwanEditingDialog == null)
            mTaiwanEditingDialog = createEditingDialogTwn();
        mCurrentDialog = mTaiwanEditingDialog;
        editUserTwn.setText("");
        editPassTwn.setText("");
        spinnerServiceTwn.setSelection(0);
        mEditingEntry = -1;
        mCurrentDialog.show();
    }
    
    private void onAddButtonGenericClick() {
        if (mGenericEditingDialog==null)
            mGenericEditingDialog = createEditingDialog();
        
        mCurrentDialog = mGenericEditingDialog;
        editService.setText("");
        editSSID.setText("");
        editUser.setText("");
        editPass.setText("");
        checkCase.setChecked(false);
        checkFullMatch.setChecked(false);
        mEditingEntry = -1;
        mCurrentDialog.show();        
    }
    
    private void onAddButtonClick() {
        final CharSequence[] items = {
                getString(R.string.strGenericWispr),
                getString(R.string.strTaiwanPublicWispr)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.strPickServiceType));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0)
                    onAddButtonGenericClick();
                else
                    onAddButtonTwnClick();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    CheckBox.OnCheckedChangeListener mActiveChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            service_pref.setActive(b);
        }
    };

    Button.OnClickListener mAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onAddButtonClick();
        }
    };

    private void toastMsg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    final private String [] TwnServices = new String[] {
            WISPrUtility.SSID_ITAIWAN,
            WISPrUtility.SSID_TPE_FREE,
            WISPrUtility.SSID_NEW_TAIPEI,
            WISPrUtility.SSID_WIFLY,
            WISPrUtility.SSID_TWNGSM,
            WISPrUtility.SSID_FET_MOBILE,
            WISPrUtility.SSID_TWROAM,
            WISPrUtility.SSID_CYBER_CITIZENS,
            WISPrUtility.SSID_CHT_WIFI
    };

    final private String [] TwnServicesText = new String[] {
            WISPrUtility.SSID_ITAIWAN,
            WISPrUtility.SSID_TPE_FREE,
            WISPrUtility.SSID_NEW_TAIPEI,
            WISPrUtility.SSID_WIFLY,
            "台灣大哥大用戶",
            "遠傳行動用戶",
            "台灣漫遊認證交換中心",
            "台北市政府網路市民",
            "中華電信用戶"
    };

    private void onEditingDialogTwnOK() {
        if (mCurrentDialog == null)
            return;
        
        try {
            String user = editUserTwn.getText().toString();
            String pass = editPassTwn.getText().toString();
            if (user.trim().equals("")) {
                toastMsg(getString(R.string.strEmptyUserName));
                editUserTwn.requestFocus();
                return;
            }
            if (pass.trim().equals("")) {
                toastMsg(getString(R.string.strEmptyPassword));
                editPassTwn.requestFocus();
                return;
            }
            
            int idx = spinnerServiceTwn.getSelectedItemPosition();
            String service = TwnServices[idx];
            String ssid = service;
            boolean match = true;
            boolean caseSen = false;

            if (mEditingEntry == -1)
                service_pref.addItem(service, ssid, user, pass, match, caseSen);
            else {
                ServiceItem item = new ServiceItem(service, ssid, match, caseSen, user, pass);
                service_pref.setItem(mEditingEntry, item);
            }
            buildSettingList();
            service_pref.saveToPref();
        }
        catch (Exception e) {

        }
        onEditingDialogCancel();

    }

    private void onEditingDialogOK() {
        if (mCurrentDialog == null)
            return;

        try {
            String service = editService.getText().toString();
            if (service.trim().equals("")) {
                toastMsg(getString(R.string.strEmptyService));
                editService.requestFocus();
                return;
            }
            String ssid = editSSID.getText().toString();
            if (ssid.trim().equals("")) {
                toastMsg(getString(R.string.strEmptyPassword));
                editSSID.requestFocus();
                return;
            }
            String user = editUser.getText().toString();
            if (user.trim().equals("")) {
                toastMsg(getString(R.string.strEmptyUserName));
                editUser.requestFocus();
                return;
            }
            String pass = editPass.getText().toString();
            if (pass.trim().equals("")) {
                toastMsg(getString(R.string.strEmptyPassword));
                editPass.requestFocus();
                return;
            }
            boolean match = checkFullMatch.isChecked();
            boolean caseSen = checkCase.isChecked();

            if (mEditingEntry == -1)
                service_pref.addItem(service, ssid, user, pass, match, caseSen);
            else {
                ServiceItem item = new ServiceItem(service, ssid, match, caseSen, user, pass);
                service_pref.setItem(mEditingEntry, item);
            }
            buildSettingList();
            service_pref.saveToPref();
        }
        catch (Exception e) {

        }
        onEditingDialogCancel();
    }

    private void onEditingDialogCancel() {
        mEditingEntry = -1;
        if (mCurrentDialog != null)
            mCurrentDialog.dismiss();
    }

    int mEditingEntry = -1;
    Dialog mCurrentDialog = null;
    Dialog mGenericEditingDialog = null;
    Dialog mTaiwanEditingDialog = null;
    EditText editService, editSSID, editUser, editPass;
    CheckBox checkFullMatch, checkCase;
    EditText editUserTwn, editPassTwn;
    Spinner spinnerServiceTwn;

    private Dialog createEditingDialogTwn() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.list_edit_twn);
        dialog.setTitle(getString(R.string.strTaiwanPublicConfigure));

        dialog.setOwnerActivity(this);

        Button button = (Button)dialog.findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onEditingDialogTwnOK();
            }
        });
        button = (Button)dialog.findViewById(R.id.buttonCancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onEditingDialogCancel();
            }
        });
        
        spinnerServiceTwn = (Spinner)dialog.findViewById(R.id.spinnerServiceType);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TwnServicesText);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceTwn.setAdapter(adapter);

        editUserTwn = (EditText)dialog.findViewById(R.id.textUsername);
        editPassTwn = (EditText)dialog.findViewById(R.id.textPassword);

        return dialog;        
    }
    
    private Dialog createEditingDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.list_edit);
        dialog.setTitle(getString(R.string.strGenericConfigure));

        dialog.setOwnerActivity(this);

        Button button = (Button)dialog.findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onEditingDialogOK();
            }
        });
        button = (Button)dialog.findViewById(R.id.buttonCancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                onEditingDialogCancel();
            }
        });

        editService = (EditText)dialog.findViewById(R.id.textServiceName);
        editSSID = (EditText)dialog.findViewById(R.id.textSSID);
        editUser = (EditText)dialog.findViewById(R.id.textUsername);
        editPass = (EditText)dialog.findViewById(R.id.textPassword);
        checkCase = (CheckBox)dialog.findViewById(R.id.checkCaseSensitive);
        checkFullMatch = (CheckBox)dialog.findViewById(R.id.checkFullyMatch);

        return dialog;
    }

    private void buildSettingList() {
        settingList = service_pref.buildMapList();
        ListAdapter settingListAdapter = new SimpleAdapter(this, settingList,
                android.R.layout.simple_list_item_2,
                new String[] { "service","user" },
                new int[] { android.R.id.text1, android.R.id.text2 });
        serviceList.setAdapter(settingListAdapter);
    }
}

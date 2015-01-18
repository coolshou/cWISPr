package idv.coolshou.wispr;

import idv.coolshou.wispr.util.HttpUtils;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import idv.coolshou.wispr.R;

/**
 * Created by IntelliJ IDEA.
 * User: morven@livemail.tw
 * Date: 2/24/12
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class DebugActivity extends Activity {
    
    EditText mUriText;
    TextView mXmlView;
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.debug);
        
        mUriText = (EditText)findViewById(R.id.debugTextUrl);
        mXmlView = (TextView)findViewById(R.id.debugXmlView);
        //mXmlView.setTextIsSelectable(true);

        Button button = (Button)findViewById(R.id.debugButtonGo);
        ((Button)findViewById(R.id.debugTest1)).setOnClickListener(mClickTest1);
        ((Button)findViewById(R.id.debugTest2)).setOnClickListener(mClickTest2);
        ((Button)findViewById(R.id.debugTest3)).setOnClickListener(mClickTest3);

        button.setOnClickListener(mClickListener);
        mUriText.setText("http://www.gov.tw");

        mXmlView.setText("" +
                "https://apc.aptilo.com/pas/QWFNH/showsession.htm?key=\n" +
                "https://cas.taipei.gov.tw/cas-web/login?service=http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx\n" +
                "http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx\n"
        );

    }
    
    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onDebugButton();
        }
    };

    Button.OnClickListener mClickTest1 = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            onDebugButtonT1();
        }
    };

    Button.OnClickListener mClickTest2 = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            onDebugButtonT2();
        }
    };

    Button.OnClickListener mClickTest3 = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            onDebugButtonT3();
        }
    };

    private void getData(String uri) {
        try {
            String xml = HttpUtils.getUrl(uri, 1);
            if (xml != null)
                mXmlView.setText(xml);
            else
                mXmlView.setText("");
        }
        catch (Exception e) {
            mXmlView.setText(e.getMessage());
        }
    }

    private void onDebugButtonT3() {
        final String uri = "https://apc.aptilo.com/pas/QWFNH/showsession.htm?key=";
        getData(uri);
    }

    private void onDebugButtonT2() {
        final String uri = "https://cas.taipei.gov.tw/cas-web/login?service=http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx";
        getData(uri);
    }

    private void onDebugButtonT1() {
        final String uri = "http://www.wifly.com.tw/wiflylogin/CyberCitizens.aspx";
        getData(uri);
    }

    private void onDebugButton() {
        String uri = mUriText.getText().toString().trim();
        getData(uri);
    }
}

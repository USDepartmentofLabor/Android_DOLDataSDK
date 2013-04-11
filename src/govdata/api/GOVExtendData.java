package govdata.api;

import gov.SDK.sample.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

@SuppressWarnings("unused")
public class GOVExtendData extends Activity {
	
	
	String dataset = "http://services.faa.gov/airport/status/IAD?format=xml";
        //Intent FCCDATA = new Intent(this, GOVExtendData.class);
		//startActivity(FCCDATA);
		


	 @Override
public void onCreate(Bundle savedInstanceState) {
		 
		 
		Log.d("Calling the data call: ", dataset );
	super.onCreate(savedInstanceState);
	 WebView webview = new WebView(this);
	 setContentView(R.layout.webextend);
	 setContentView(webview);
	 webview.loadUrl(dataset);
		
	}
	
	
}





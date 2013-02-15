package gov.dol.doldata.api;

import gov.dol.SDK.sample.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ExtendData extends Activity {
	
	//Add your FCC URL call inside of the string url below.
String url = "ENTER GOVERNMENT DATA SET URL HERE";
	

//The code below does a simple webView call in android presenting the data set. This code can be modified by the developer to present the data in any format of their choosing.
		   
	   @Override
		public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
			WebView webview = new WebView(this);
			setContentView(R.layout.webextend);
		    setContentView(webview);
		    webview.loadUrl(url);
		 
		}
		
}





package govdata.api;

import java.util.List;
import java.util.Map;

public interface GOVDataRequestCallback {
	//Return results
	public void GOVDataResultsCallback(List<Map<String, String>> results);
	
	public void GOVDataResultsCallback(String results);
	
	//Error Callback
	public void GOVDataErrorCallback(String error);
}
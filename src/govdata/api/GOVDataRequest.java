/**
 * Created by the U.S. Department of Labor
 * This source is released to the Public domain
 */
package govdata.api;



import gov.SDK.sample.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import  govdata.api.GOVExtendData;



@SuppressWarnings("unused")
public class GOVDataRequest {
	// instance variables
	private GOVDataRequestCallback callback;
	private GOVDataContext context;
	

	/**
	 * @return the context
	 */
	public GOVDataContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(GOVDataContext context) {
		this.context = context;
	}

	public GOVDataRequestCallback getCallback() {
		return callback;
	}

	/**
	 * Constructor
	 * 
	 * @param callback
	 * @param context
	 */
	public GOVDataRequest(GOVDataRequestCallback callback,
			GOVDataContext context) {
		super();
		this.callback = callback;
		this.context = context;
	}

	/**
	 * Set Callback
	 * 
	 * @param callback
	 */
	public void setCallback(GOVDataRequestCallback callback) {
		this.callback = callback;
	}

	/**
	 * Main method to make API calls
	 * 
	 * @param method
	 * @param arguments
	 */
	public void callAPIMethod(String method, HashMap<String, String> arguments) {

		
		 StringBuffer url = new StringBuffer(context.getApiHost()
                 + context.getApiURI() + "/" + method);
		 
        StringBuffer queryString = new StringBuffer();
		
		if(context.getApiHost().equalsIgnoreCase("http://api.dol.gov/")) {

    
              Log.d("The DOL Validation is correct", context.getApiHost()
	                  + context.getApiURI() + context.getApiSecret() + context.getApiKey());
                                          
           

    // Enumerate the arguments and add them to the request
       if (arguments != null) {
               for (HashMap.Entry<String, String> entry : arguments.entrySet()) {
                      String key = entry.getKey();
                      String value = "";
                    try {
                            value = URLEncoder.encode(entry.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                    }

                    if (key.equals("top") || key.equals("skip")
                                    || key.equals("select") || key.equals("orderby")
                                    || key.equals("filter")) {
                            // If it is the first parameter we append a ?, if not we
                            // concatenate with &
                            if (queryString.length() == 0) {
                                    queryString.append("?");
                            } else {
                                    queryString.append("&");
                            }
                            // append the querystring key and value
                            queryString.append("$" + key + "=" + value);
                    }
                    else
                    {
                            if (queryString.length() == 0) {
                                    queryString.append("?");
                            } else {
                                    queryString.append("&");
                            }
                            queryString.append(key + "=" + value);
                    }

            }
    }

    // If there are valid arguments then append it to the URL
    if (queryString.length() > 0) {
            url.append(queryString.toString());
    }
  
  
    new RequestTask().execute(url.toString());
    Log.d("Request Task is being called", url.toString());
         
}
    else if((!context.getApiHost().isEmpty()) && (!context.getApiKey().isEmpty()) && (context.getApiLogin().isEmpty())) {
    	 StringBuffer dataset =  new  StringBuffer(context.getApiHost()  + context.getApiKey() + context.getApiData());
    	String data = dataset.toString();
    	Log.d("The ApiKey and ApiHost validation", data);
    	new RequestTaskApiHost().execute(data);
                        
                        
                           
           
    } else if(!context.getApiHost().isEmpty() && (context.getApiKey().isEmpty()) && (context.getApiLogin().isEmpty())) {
    	
    	           if (context.getApiData().isEmpty()) {
                        String dataset = context.getApiHost();
                        Log.d("The ApiKey and ApiHost only validation", dataset);
                        new RequestTaskApiHost().execute(dataset);
    	           } else {
    	        	   
    	        	   StringBuffer dataset = new StringBuffer(context.getApiHost() + context.getApiData());
    	        	   String data  = dataset.toString();
                       Log.d("The ApiKey and ApiHost only validation", data);
                       new RequestTaskApiHost().execute(data);
    	           }
                      
                          
             } else if(!context.getApiHost().isEmpty()   && (!context.getApiKey().isEmpty()) && (!context.getApiLogin().isEmpty())) {
            	 // Call go.usa.gov
            		 StringBuffer dataset =  new StringBuffer (context.getApiHost()
                          + context.getApiLogin() + context.getApiKey() + context.getApiData());
            		   String data = dataset.toString();
            	    	Log.d("The ApiLogin validation", data);
            	    	    new RequestTaskApiHost().execute(data);
               
            	 
      }
           
    
                   
  }
          
  





	
	/**
	 * Helper class for storing the AsyncTask results
	 * @author antonionieves
	 *
	 */
	private class RequestResults {
		private boolean isError;
		private String result;
		public RequestResults(boolean isError, String result) {
			super();
			this.isError = isError;
			this.result = result;
		}
	}
	
	/**
	 * Triggers an asynchronous HTTP request to the OData API
	 * @author antonionieves
	 *
	 */
	private class RequestTask extends AsyncTask<String, Void, RequestResults> {

		
		@Override
		protected RequestResults doInBackground(String... params) {
			try {
				//Httpclient
				HttpClient hclient = new DefaultHttpClient();
				HttpGet request = new HttpGet(params[0]);

				// Authorization Header
				String authHeader = "";

				//Try to get an authorization header
				try {
					authHeader = GOVAPIUtils.getRequestHeader(params[0], context.getApiHost(), context.getApiKey().toLowerCase(), context.getApiSecret());
				} catch (final Exception e) {
					// Send error to callback
					
					return new RequestResults(true, e.getLocalizedMessage());
				}
				
				//At this oint we have the hader text. Add it to the request
				request.addHeader("Authorization", authHeader);
				
				//Specify desired format for the OData service
				request.addHeader("Accept", "application/json");

				//Execute request
				HttpResponse response = hclient.execute(request);

				//Request completed. Check status code
				int statusCode = response.getStatusLine().getStatusCode();
				
				//If 200, return results to callback
				if (statusCode == HttpStatus.SC_OK) {
					String str = EntityUtils.toString(response.getEntity());
					return new RequestResults(false, str);

				} else {
					//HTTP status code is not 200; return error.
					String errorMessage;

					switch (statusCode) {
					case 401:
						errorMessage = "Unauthorized";
						break;
					case 400:
						errorMessage = "Bad Request";
						break;
					case 404:
						errorMessage = "Request not found";
						break;
					case 500:
						errorMessage = "Server could not process request";
						break;	
					case 504:
						errorMessage = "Request timed out";
						break;
					default:
						errorMessage = "Error " + statusCode + " returned";
						break;
					}
					return new RequestResults(true, errorMessage);
				}
			} catch (IOException e) {
				return new RequestResults(true, e.getLocalizedMessage());
			}
		}
	

		/**\
		 *  Called after AsyncTask has completed
		 *  From here we must call the callback helpers
		 */
		@Override
		protected void onPostExecute(RequestResults r) {
			
			if (r.isError) {
				callbackWithError(r.result);
			} else {
				callbackWithResults(r.result);
			}
		}
		
	}
	
	
	private class RequestTaskApiHost extends AsyncTask<String, Void, RequestResults> {

		
		@Override
		protected RequestResults doInBackground(String... params) {
			try {
				//Httpclient
				
				
				HttpClient hclient = new DefaultHttpClient();
				HttpGet request = new HttpGet(params[0]);
				
	
				HttpResponse response = hclient.execute(request);

				//Request completed. Check status code
				int statusCode = response.getStatusLine().getStatusCode();
				
				//If 200, return results to callback
				if (statusCode == HttpStatus.SC_OK) {
					String str = EntityUtils.toString(response.getEntity());
					return new RequestResults(false, str);

				} else {
					//HTTP status code is not 200; return error.
					String errorMessage;

					switch (statusCode) {
					case 401:
						errorMessage = "Unauthorized";
						break;
					case 400:
						errorMessage = "Bad Request";
						break;
					case 404:
						errorMessage = "Request not found";
						break;
					case 500:
						errorMessage = "Server could not process request";
						break;	
					case 504:
						errorMessage = "Request timed out";
						break;
					default:
						errorMessage = "Error " + statusCode + " returned";
						break;
					}
					return new RequestResults(true, errorMessage);
				}
			} catch (IOException e) {
				return new RequestResults(true, e.getLocalizedMessage());
			}
		}
	

		/**\
		 *  Called after AsyncTask has completed
		 *  From here we must call the callback helpers
		 */
		@Override
		protected void onPostExecute(RequestResults r) {
			
			if (r.isError) {
				callbackWithError(r.result);
			} else {
				callbackAPiWithResults(r.result);
			}
		}
		
	}

	/**
	 * Callback method to return results to the caller
	 * 
	 * @param results
	 */
	private void callbackAPiWithResults(final String results) {
		//Parse JSON
		String objects =  results;
		// return results to the callback
		callback.GOVDataResultsCallback(objects);
	}

	
	private void callbackWithResults(final String results) {
		//Parse JSON
		List<Map<String, String>> objects = GOVAPIUtils.parseJSON(results);
		// return results to the callback
		callback.GOVDataResultsCallback(objects);
	}
	/**
	 * Callback method to return errors to the caller
	 * 
	 * @param error
	 */
	private void callbackWithError(final String error) {
		// Return error to the callback
		callback.GOVDataErrorCallback(error);
	}
	
	
}

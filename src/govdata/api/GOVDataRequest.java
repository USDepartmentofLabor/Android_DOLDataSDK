/**
 * Created by the U.S. Department of Labor
 * This source is released to the Public domain
 */
package govdata.api;



import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.SDK.sample.R;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.*;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.HttpsURLConnection;



@SuppressWarnings("unused")
public class GOVDataRequest {
	// instance variables
	private GOVDataRequestCallback callback;
	private GOVDataContext context;
	private String data;
    private String query;
    private String contentType;
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
	 * @throws Exception
	 * @throws InvalidKeyException
	 */
	@SuppressLint("DefaultLocale")
	public void callAPIMethod(String method, HashMap<String, String> arguments) throws InvalidKeyException, Exception {


		 StringBuffer url = new StringBuffer(context.getApiHost()
                 + context.getApiURI() + "/" + method);
		 StringBuffer query = new StringBuffer();

        StringBuffer queryString = new StringBuffer();
        StringBuffer queryData = new StringBuffer();
        String login = "";
        String longURL = "";

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

                    if(context.getApiHost().equalsIgnoreCase("http://go.usa.gov")) {
                      String keyLong = entry.getKey();
                      String valueLong = entry.getValue();
                       login = key;
                       longURL= valueLong;

                    }

                     if(queryData.length() == 0) {
                        queryData.append("&");
                        queryData.append(key + "=" + value);
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

	 if (queryString.length() > 0) {
         query.append(queryData.toString());
 }

    if(context.getApiHost().equalsIgnoreCase("http://api.dol.gov")) {

    	 new RequestTask().execute(url.toString());



}  else if(context.getApiHost().equalsIgnoreCase("http://go.usa.gov")) {



         data = context.getApiHost()  + context.getApiURI() + "/" + method + '?' + "login=" + login + "&apiKey=" + context.getApiKey() + "&longUrl=" + longURL;

         new RequestTask().execute(data.toString());

    } else if(context.getApiHost().equalsIgnoreCase("http://www.ncdc.noaa.gov")) {

    	// NOAA National Climatic Data Center

    	  data = context.getApiHost()  + context.getApiURI() + "/" + method + '?' + "token=" + context.getApiKey() + query.toString();

    	  new RequestTask().execute(data.toString());

    } else if (context.getApiHost().equalsIgnoreCase("http://api.eia.gov") || context.getApiHost().equalsIgnoreCase("http://developer.nrel.gov") || context.getApiHost().equalsIgnoreCase("http://api.stlouisfed.org") || context.getApiHost().equalsIgnoreCase("http://healthfinder.gov"))
    		{


    // Energy EIA API (beta)
	//		# Energy NREL
	//		# St. Louis Fed
	//		# NIH Healthfinder

    	 data = context.getApiHost()  + context.getApiURI() + "/" + method + '?' + "api_key=" + context.getApiKey() + query.toString();

    	 new RequestTask().execute(data.toString());

    }

    else if (context.getApiHost().equalsIgnoreCase("http://api.census.gov") || context.getApiHost().equalsIgnoreCase("http://pillbox.nlm.nih.gov")) {


    //	# Census.gov
	//	# NIH Pillbox

    data = context.getApiHost()  + context.getApiURI() + "/" + method + '?' + "key=" + context.getApiKey() + query.toString();

    new RequestTask().execute(data.toString());

    } else if(context.getApiHost().equalsIgnoreCase("https://quarry.dol.gov")) {
 		  url = new StringBuffer(context.getApiHost()+context.getApiURI());

   	 new RequestTask().execute(url.toString());


    } else {


    	new RequestTask().execute(url.toString());

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
			Log.i("REQUEST RESULTS", result);

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
		@SuppressLint("DefaultLocale")
		@Override
		protected RequestResults doInBackground(String... params) {
			try {

				HttpClient hclient = new DefaultHttpClient();
				HttpGet request = null;

			  if(context.getApiHost().equalsIgnoreCase("http://api.dol.gov")) {

				request = new HttpGet(params[0]);
				// Authorization Header
				String authHeader = null;

				 try {
					// Authorization Header
					authHeader = GOVAPIUtils.getRequestHeader(params[0], context.getApiHost(), context.getApiKey().toLowerCase(), context.getApiSecret());

				 } catch (final Exception e) {

					// Send error to callback
					return new RequestResults(true, e.getLocalizedMessage());
				}


					//At this oint we have the hader text. Add it to the request
					request.addHeader("Authorization", authHeader);

					//Specify desired format for the OData service
					request.addHeader("Accept", "application/json");

				 }else if(context.getApiHost().equalsIgnoreCase("https://quarry.dol.gov")){

					 try {

						 X509HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
						 DefaultHttpClient client = new DefaultHttpClient();
						 SchemeRegistry registry = new SchemeRegistry();
						 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
						 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
						 registry.register(new Scheme("https", socketFactory, 443));
						 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
						 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

						 // Set verifier
						 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

						 // Example send http request
						 final String url = "https://10.49.1.43/get/violation";
						 HttpGet HttpGet = new HttpGet(url);
						 HttpResponse response = httpClient.execute(HttpGet);

						//StringBuffer url = new StringBuffer(context.getApiHost()+context.getApiURI());

						request = new HttpGet(url.toString());
						//At this point we have the header text. Add it to the request
						request.addHeader("X-API-KEY", context.getApiKey().toLowerCase());
						//Specify desired format for the OData service
						request.addHeader("Accept", "application/json");

					 } catch (final Exception e) {
							// Send error to callback
							Log.i("quarry", "BAD REQUEST");

							return new RequestResults(true, e.getLocalizedMessage());
					}

				 }

				HttpResponse response = hclient.execute(request);

				//Request completed. Check status code
				int statusCode = response.getStatusLine().getStatusCode();

				 Header entity = response.getFirstHeader("Content-Type");
				Log.d("what is the status code: ", entity.toString());

				contentType = entity.toString();
				Log.d("what is the contenttype: ", contentType);
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

			if(contentType.startsWith("text/")) {

				 callbackWithResultsRaw(r.result);
			} else if(contentType.startsWith("application/json")) {
				Log.d("PrePost Call: ", r.result);
			    callbackWithResultsJSON(r.result);


			} else if (contentType.startsWith("application/xml")) {
				Log.d("PrePost Call: ", r.result);
				callbackWithResultsXML(r.result);
			} else {

								Log.d("PrePost Call: ", r.result);
							   callbackWithResultsRaw(r.result);

				}

		}


	/**
	 * Callback method to return results to the caller
	 *
	 * @param results
	 */
	private void callbackWithResultsRaw(final String results) {

		// return results to the callback
		callback.GOVDataResultsCallback(results);
	}


	private void callbackWithResultsJSON(final String results) {
		//Parse JSON
		List<Map<String, String>> objects = GOVAPIUtils.parseJSON(results);
		// return results to the callback
		if ((objects != null) && !objects.isEmpty()) {
		callback.GOVDOLDataResultsCallback(objects);
		} else {
			callbackWithResultsRaw(results);
		}
	}



	private void callbackWithResultsXML(final String results) {
		//Parse JSON

		Map<String, String> objects = GOVAPIUtils.parseXML(results);
		// return results to the callback
		if ((objects != null) && !objects.isEmpty()) {
			callback.GOVDataResultsCallback(objects);
			} else {
				callbackWithResultsRaw(results);
			}

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
}

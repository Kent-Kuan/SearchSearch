package com.momoForLINE.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class WebUtils {
	private static HttpURLConnection getConnect(String urlString, String method, Map<String, String> porperties){
		  HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(urlString);
				urlConnection = (HttpURLConnection)url.openConnection();
				urlConnection.setRequestMethod(method);
				if("POST".equals(method)) urlConnection.setDoOutput(true);
				if(porperties!=null){
					for(Map.Entry<String, String> entry:porperties.entrySet()){
						urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return urlConnection;
	  }
	  
	  public static String getUrl(String urlString, Map<String, String> porperties){
		  HttpURLConnection urlConnection = getConnect(urlString, "GET", porperties);
		  StringBuffer sb = new StringBuffer();
		  String line ;
		  try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
			while((line=rd.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		  return sb.toString();
	  }
	  
	  public static List<String> getCookieForAPI(String urlString, Map<String, String> porperties){
		  HttpURLConnection urlConnection = getConnect(urlString, "GET", porperties);
		  for(Map.Entry<String, List<String>> headers : urlConnection.getHeaderFields().entrySet()){
			  if("cookie".equalsIgnoreCase(headers.getKey()) || "Set-Cookie".equalsIgnoreCase(headers.getKey())){
				  return headers.getValue();
			  }
		  }
		  throw new NullPointerException("Cookies is NULL!");
	  }
	  
	  public static String postUrl(String urlString, JSONObject data, Map<String, String> porperties){
		  HttpURLConnection urlConnection = getConnect(urlString, "POST", porperties);
		  StringBuffer sb = new StringBuffer();
		  String line ;
		  try {
			OutputStream out = urlConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
			bufferedWriter.write(data.toString());
			bufferedWriter.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
			while((line=rd.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			try{
				BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), "utf-8"));
				while((line=rd.readLine())!=null){
					sb.append(line);
					System.out.println(sb.toString());
				}
			}catch (IOException e2) {
				e2.printStackTrace();
			}
			
		}
		  return sb.toString();
	  }
	  
	  public static String postUrl(String urlString, String data, Map<String, String> porperties){
		  HttpURLConnection urlConnection = getConnect(urlString, "POST", porperties);
		  StringBuffer sb = new StringBuffer();
		  String line ;
		  try {
			OutputStream out = urlConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
			bufferedWriter.write(data);
			bufferedWriter.flush();
			BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
			while((line=rd.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		  return sb.toString();
	  }
}

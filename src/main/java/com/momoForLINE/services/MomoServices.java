package com.momoForLINE.services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.momoForLINE.utils.WebUtils;

@Service
@ConfigurationProperties(prefix="momo")
public class MomoServices {
	
	private String searchURL;
	private String token;
	private final int count = 3;
	
	public void setSearchURL(String serachURL){
		this.searchURL = serachURL+"=";
	}
	
	
	public void setToken(String token) {
		this.token = token;
	}


	public String momoSearch(String keyword){
		String response = getResponse(searchURL+keyword);
		Document doc = Jsoup.parse(response);
		Elements elements = new Elements(doc).select("article.prdListArea li");
		JSONArray jsonArray = combinationProduct(elements);
		JSONObject templateObj = new JSONObject();
		templateObj.put("type", "carousel");
		templateObj.put("columns", jsonArray);
		return templateObj.toString();
	}
	
	private String getResponse(String url){
		return WebUtils.getUrl(url, null);
	}
	
	private JSONArray combinationProduct(Elements elements){
		JSONArray columnsArray = new JSONArray();
		try {
		  for(int i=0; i<count; i++){
			JSONObject columnObject = new JSONObject();
			columnObject.put("title", getElementAttr(elements.get(i), ".prdName", "text"));
			columnObject.put("thumbnailImageUrl", getElementAttr(elements.get(i), "img[src*=/goodsimg/]", "src"));
			columnObject.put("text", "Descripion!");
			JSONObject actionObject = new JSONObject();
			actionObject.put("type", "uri");
			actionObject.put("label", "Open!");
			String uri = getElementAttr(elements.get(i), "a[href*=/goods.momo]", "href").replaceAll("(.*)&.*", "$1");
			actionObject.put("uri", "https://m.momoshop.com.tw"+uri);
			JSONArray actionsArray = new JSONArray();
			actionsArray.put(actionObject);
			columnObject.put("actions", actionsArray);
			columnsArray.put(columnObject);
		  } 
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columnsArray;
	}
	
	private String getElementAttr(Element element, String query, String attr){
		if("text".equals(attr)){
			return element.select(query).text();
		}else{
			return element.select(query).attr(attr);
		}
	}
	
	public void replyToLINE(JSONObject requestBody){
		JSONArray jsonArray = requestBody.optJSONArray("events");
		JSONObject jsonObject = new JSONObject();
		String replyToken;
		String keyword;
		for(int i=0, size=jsonArray.length(); i<size; i++){
			jsonObject = jsonArray.optJSONObject(i);
		    replyToken = jsonObject.optString("replyToken");
		    keyword = extractKeyWord(jsonObject.optJSONObject("message").optString("text"));
		    if("".equals(keyword))break;
		    replyToLINE(replyToken, momoSearch(keyword));
		}
	}
	
	private void replyToLINE(String replyToken, String message){
		JSONObject postData = new JSONObject();
		JSONObject messageObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		messageObj.put("type", "template");
		messageObj.put("altText", "Hello World!");
		messageObj.put("template", new JSONObject(message));
		jsonArray.put(messageObj);
		postData.put("replyToken", replyToken);
		postData.put("messages", jsonArray);
		WebUtils.postUrl("https://api.line.me/v2/bot/message/reply", postData, getLINEproperties());
	}
	
	private Map<String, String> getLINEproperties(){
		Map<String, String> porperties = new HashMap<String, String>();
		porperties.put("Content-Type", "application/json");
		porperties.put("Authorization", "Bearer " + token);
		return porperties;
	}
	
	private String extractKeyWord(String text){
		if(text.matches("搜搜,.*")){
			return text.replaceAll("搜搜,()", "$1");
		}
		return "";
	}
}

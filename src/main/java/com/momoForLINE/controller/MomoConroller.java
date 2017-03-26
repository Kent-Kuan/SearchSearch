package com.momoForLINE.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.momoForLINE.services.MomoServices;

@Controller
public class MomoConroller {
	
	
	
	@Autowired
	MomoServices momoservices;
	
	@GetMapping(value="/momoSearch/{keyWord}")
	@ResponseBody
	public String momoSearch(@PathVariable("keyWord") String keyword){
		String result = "";
		try {
			String encodeKeyword = URLEncoder.encode(keyword,"UTF-8");
			result = momoservices.momoSearch(encodeKeyword);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result = "���A���o�Ϳ��~--UnsupportedEncodingException";
		}
		return result;
	}
}

package com.music.title.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetMusicTitleMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
		    // 1. URL 선언
		    String connUrl = "https://www.lyrics.co.kr/charts/tops?site=melon";
		
		    // 2. HTML 가져오기
		    Document doc = Jsoup.connect(connUrl).get();
		
		    // 3. 가져온 HTML Document 를 확인하기
//		    System.out.println(doc.toString());				// 전체 HTML 출력
		    
		    // 태그 데이터 추출시 Class는 '.' 사용 ID는 '#' 사용 
		    Elements musicInfo = doc.select(".timeline-header");
		    Elements hr = musicInfo.select("[href]");
		    String[] mInfo = new String[2];
		    
		    for( Element elem : hr ){
		    	if(null != elem.text()) {
		    		// 노래 제목이 '(+)'로 구분되어 있음  예)임재현 (+)사랑에 연습이 있었다면 (Prod. 2soo)
		    		mInfo = elem.text().split("\\(\\+\\)");
		    		mInfo[0] = mInfo[0].trim();
		    		mInfo[1] = mInfo[1].trim();
			    	System.out.println(mInfo[0]);
			    	System.out.println(mInfo[1]);
			    	
		    	}
		    }
		    
		    
		    // Call Elasticsearch
		    // ref : https://yookeun.github.io/elasticsearch/2017/11/05/elastic-api/
		    Map<String, Object> result = new HashMap<String, Object>();
		    String jsonString;
		    

		
		} catch (IOException e) {
		    // Exp : Connection Fail
		    e.printStackTrace();
		}
	}
}

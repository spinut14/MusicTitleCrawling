package com.music.title.main;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.music.title.vo.AnalyzeQueryVO;
import com.music.title.vo.AnalyzeResVO;

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
		    AnalyzeQueryVO jsonVO = new AnalyzeQueryVO();
		    jsonVO.setAnalyzer("lyric_analyzer");
		    jsonVO.setText("진정인");
		    System.out.println("call ES Start");
		    callElasticApi("GET", "/sch_lyrics/_analyze", jsonVO, null);
		    
		    System.out.println("call ES End");


		
		} catch (IOException e) {
		    // Exp : Connection Fail
		    e.printStackTrace();
		}
	}
	
	private static void callElasticApi(String method, String url, Object obj, String jsonData) {
		String host = "13.125.238.20";
		int port = 9200;

		try{
            //엘라스틱서치에서 제공하는 response 객체
            Response response = null;
            String jsonString;
            Gson gson = null;
            if(null != jsonData) {
            	jsonString = jsonData;
            }else {
            	
            	gson = new Gson();
                jsonString = gson.toJson(obj);
            }
            System.out.println("jsonString : " +jsonString);
            RestClient restClient = RestClient.builder(
            	    new HttpHost(host, port, "http")).build();
            Request request = new Request(method, url);
            request.addParameter("pretty", "true");
            request.setEntity(new NStringEntity(jsonString, ContentType.APPLICATION_JSON));
            
            response = restClient.performRequest(request);
            
            
//            curl -XGET "http://localhost:9200/sch_lyrics/_analyze" -H 'Content-Type: application/json' -d'
//            {
//              "analyzer": "lyric_analyzer",
//              "text" :"진정인"
//            }'
            //앨라스틱서치에서 리턴되는 응답코드를 받는다
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("status Code : " + statusCode);
            //엘라스틱서치에서 리턴되는 응답메시지를 받는다
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("response Body : " + responseBody);
            AnalyzeResVO resVO = gson.fromJson(responseBody, AnalyzeResVO.class);
            System.out.println(resVO.toString());
//            result.put("resultCode", statusCode);
//            result.put("resultBody", responseBody);
        } catch (Exception e) {
//            result.put("resultCode", -1);
//            result.put("resultBody", e.toString());
        	e.printStackTrace();
        }
	}
}

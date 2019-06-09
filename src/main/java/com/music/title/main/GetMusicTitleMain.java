package com.music.title.main;

import java.io.File;
import java.io.FileWriter;
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
import com.music.title.vo.CompletionVO;
import com.music.title.vo.TokenVO;

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
					
					// 가수와 노래제목 Analyze 하기
					analyzeData(mInfo[0], false);
					analyzeData(mInfo[1], true);
					
//					System.out.println(mInfo[0]);
//					System.out.println(mInfo[1]);

				}
			}

		} catch (IOException e) {
			// Exp : Connection Fail
			e.printStackTrace();
		}
		System.out.println("program exit");
	}

	/**
	 * 가수 및 노래제목 Analyze
	 * @param str
	 * @param isTitle
	 */
	private static void analyzeData(String str, boolean isTitle) {
		// json 문자열 생성위해 객체 생성
		AnalyzeQueryVO jsonVO = new AnalyzeQueryVO();
		jsonVO.setAnalyzer("lyric_analyzer");
		jsonVO.setText(str);
		AnalyzeResVO resVO = callElasticApi("GET", "/sch_lyrics/_analyze", jsonVO, null);
		
		if(null == resVO) {
			System.out.println("Occured Error");
		}
		
		// 파일 생성
		makeJsonBulkFile(resVO, isTitle);
	
	}

	/**
	 * 토큰 분석된 단어를 bulk 파일로 생성한다.
	 * @param inVO
	 */
	private static void makeJsonBulkFile(AnalyzeResVO inVO, boolean isTitle) {

		try {

			File file = null;
			
			if(isTitle) {
				file = new File("/Users/spinut/Documents/study/elasticsearchStudy/sample_data/titleToken.json");
			}else {
				file = new File("/Users/spinut/Documents/study/elasticsearchStudy/sample_data/singerToken.json");
			}
			if(!file.exists()) {
				file.createNewFile();
			}
			FileWriter wr = new FileWriter(file, true);
			CompletionVO compVO = null;
			Gson gson = new Gson();
			for(TokenVO tVO : inVO.getTokens()) {
				compVO = new CompletionVO();
				compVO.setMusicTitle(tVO.getToken());
				compVO.setTitleNgram(tVO.getToken());
				compVO.setTitleNgramEdge(tVO.getToken());
				compVO.setTitleNgramEdgeBack(tVO.getToken());
				wr.write(gson.toJson(compVO, CompletionVO.class));
				wr.write("\n");
			}

			wr.flush();
			wr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	/**
	 * Call Elasticsearch Analyzer
	 * 노래 제목을 분석하여 리턴한다. 자동완성 처리 위한 기초 데이터추출
	 * 예)신혼가전특별전
	 *    1) 신혼
	 *    2) 신혼가전
	 *    3) 신혼가전특별전
	 * @param method
	 * @param url
	 * @param obj
	 * @param jsonData
	 */

	private static AnalyzeResVO callElasticApi(String method, String url, Object obj, String jsonData) {
		String host = "13.125.238.20";
		int port = 9200;

		try{
			// 엘라스틱서치에서 제공하는 response 객체
			Response response = null;
			// 요청 보낼시 넘겨줄 json 데이터
			String jsonString;
			Gson gson = null;
			if(null != jsonData) {
				jsonString = jsonData;
			}else {
				gson = new Gson();
				jsonString = gson.toJson(obj);
			}

			System.out.println("jsonString : " +jsonString);

			// ES 전송을 위한 restclient 객체 생성
			RestClient restClient = RestClient.builder(
					new HttpHost(host, port, "http")).build();

			// Request 객체 설정
			Request request = new Request(method, url);
			request.addParameter("pretty", "true");
			request.setEntity(new NStringEntity(jsonString, ContentType.APPLICATION_JSON));

			// ES 요청 후 결과 수신
			response = restClient.performRequest(request);

			//앨라스틱서치에서 리턴되는 응답코드를 받는다
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("status Code : " + statusCode);

			//엘라스틱서치에서 리턴되는 응답메시지를 받는다
			String responseBody = EntityUtils.toString(response.getEntity());
			System.out.println("response Body : " + responseBody);

			// 응답받은 json을 객체로 변환하여 리턴한다.
			AnalyzeResVO resVO = gson.fromJson(responseBody, AnalyzeResVO.class);

			restClient.close();
			
			return resVO;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

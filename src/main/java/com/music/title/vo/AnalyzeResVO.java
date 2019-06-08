package com.music.title.vo;

import java.util.List;

public class AnalyzeResVO {

	private List<TokenVO> tokens;

	public List<TokenVO> getTokens() {
		return tokens;
	}

	public void setTokens(List<TokenVO> tokens) {
		this.tokens = tokens;
	}

	@Override
	public String toString() {
		return "AnalyzeResVO [tokens=" + tokens + "]";
	}
	
}

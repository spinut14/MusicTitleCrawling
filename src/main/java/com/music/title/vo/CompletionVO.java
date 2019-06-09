package com.music.title.vo;

public class CompletionVO {
	private String musicTitle;
	private String titleNgram;
	private String titleNgramEdge;
	private String titleNgramEdgeBack;
	public String getMusicTitle() {
		return musicTitle;
	}
	public void setMusicTitle(String musicTitle) {
		this.musicTitle = musicTitle;
	}
	public String getTitleNgram() {
		return titleNgram;
	}
	public void setTitleNgram(String titleNgram) {
		this.titleNgram = titleNgram;
	}
	public String getTitleNgramEdge() {
		return titleNgramEdge;
	}
	public void setTitleNgramEdge(String titleNgramEdge) {
		this.titleNgramEdge = titleNgramEdge;
	}
	public String getTitleNgramEdgeBack() {
		return titleNgramEdgeBack;
	}
	public void setTitleNgramEdgeBack(String titleNgramEdgeBack) {
		this.titleNgramEdgeBack = titleNgramEdgeBack;
	}
	@Override
	public String toString() {
		return "CompletionVO [musicTitle=" + musicTitle + ", titleNgram=" + titleNgram + ", titleNgramEdge="
				+ titleNgramEdge + ", titleNgramEdgeBack=" + titleNgramEdgeBack + "]";
	}
}

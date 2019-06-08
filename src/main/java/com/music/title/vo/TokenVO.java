package com.music.title.vo;

public class TokenVO {

	private String token;
	private int start_offset;
	private int end_offset;
	private String type;
	private int position;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getStart_offset() {
		return start_offset;
	}
	public void setStart_offset(int start_offset) {
		this.start_offset = start_offset;
	}
	public int getEnd_offset() {
		return end_offset;
	}
	public void setEnd_offset(int end_offset) {
		this.end_offset = end_offset;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	@Override
	public String toString() {
		return "TokenVO [token=" + token + ", start_offset=" + start_offset + ", end_offset=" + end_offset + ", type="
				+ type + ", position=" + position + "]";
	}
}

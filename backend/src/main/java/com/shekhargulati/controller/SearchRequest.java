package com.shekhargulati.controller;

public class SearchRequest {
	private int maxSize;
	private String seed;
	
	public void setSize(int size) {
		this.maxSize = size;
	}
	
	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	public int getSize() {
		return this.maxSize;
	}
	
	public String getSeed() {
		return this.seed;
	}
}

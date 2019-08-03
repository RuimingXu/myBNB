package com.shekhargulati.pack;

import java.util.ArrayList;

public class WorkerResult {
	private Pack workResult;
	private ArrayList<String> subUrl;
	
	public WorkerResult(Pack pack, ArrayList<String> subUrl) {
		this.workResult = pack;
		this.subUrl = subUrl;
	}
	
	public Pack getResult() {
		return this.workResult;
	}
	
	public ArrayList<String> getSubUrl() {
		return this.subUrl;
	}
}

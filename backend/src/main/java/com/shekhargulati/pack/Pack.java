package com.shekhargulati.pack;

import java.util.Objects;

/*
 * The wrapped object for front-end display
 */
public class Pack {
	private String houseID;
	private String address;
	private String rent;

	private String description;
	private String postDate;

	private String latitude = null;
	private String longitude = null;

	public static class Builder {
		private String houseID;
		private String address;
		private String rent;

		private String description = null;
		private String postDate = null;


		public Builder(String id, String ad, String r) {
			houseID = id;
			address = ad;
			rent = r;
		}

		public Builder setDescription(String des) {
			description = des;
			return this;
		}

		public Builder setDate(String date) {
			postDate = date;
			return this;
		}

		public Pack build() {
			return new Pack(this);
		}


	}

	public Pack(Builder b) {
		this.houseID = b.houseID;
		this.address = b.address;
		this.rent = b.rent;
		this.postDate = b.postDate;
		this.description = b.description;
	}


	public void setLongitude(String data) {
		this.longitude = data;
	}
	public void setLatitude(String data) {
		this.latitude = data;
	}
	public String getID() {
		return this.houseID;
	}
	
	public String getAddress() {
		return this.address;
	}

	public String getRent() {
		return this.rent;
	}

	public String getPostDate() { return this.postDate;}

	public String getDescription() { return this.description;}

	public String getLatitude() { return this.latitude;}

	public String getLongitude() { return this.longitude;}
	
	public String toString() {
		return ("ID: " + this.houseID + "\n" +
				"address: " + this.address + "\n" +
				"rent: " + this.rent + "\n" +
				"postDate: " + this.postDate + "\n" +
				"description: " + this.description + "\n" +
				"longitude: " + this.longitude + "\n" +
				"latitude: " + this.latitude + "\n");
	}

	@Override
	public boolean equals(Object pack) {
		if (pack.getClass() != Pack.class) {
			return false;
		}
		Pack p = (Pack) pack;
		return (this.hashCode() == pack.hashCode() &&
				this.houseID.equals(p.houseID));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.houseID);
	}
	
}

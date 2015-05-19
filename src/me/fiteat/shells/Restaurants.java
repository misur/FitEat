package me.fiteat.shells;

import java.io.Serializable;

public class Restaurants implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String username;
	private String password;
	private String email;
	private String name;
	private String country;
	private String city;
	private String logo;
	private String qrCode;

	public Restaurants(String id, String username, String password,
			String email, String name, String country, String city,
			String logo, String qrCode) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
		this.country = country;
		this.city = city;
		this.logo = logo;
		this.qrCode = qrCode;
	}

	public Restaurants() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	@Override
	public String toString() {
		return "Restaurants [id=" + id + ", username=" + username
				+ ", password=" + password + ", email=" + email + ", name="
				+ name + ", country=" + country + ", city=" + city + ", logo="
				+ logo + ", qrCode=" + qrCode + "]";
	}

}

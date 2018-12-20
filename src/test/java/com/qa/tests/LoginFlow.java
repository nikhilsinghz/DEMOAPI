package com.qa.test;



	
import static io.restassured.RestAssured.given;

import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

	//Note :- Need to  Validate the number of values getting in jason response

	public class LoginFlow {
		
		//String BaseUrl = "http://35.157.221.79:5000";
		String BaseUrl = "http://acc.secure.zelfstroom.nl:32750";
		String ReturnUrl = ("/connect/authorize/callback?client_id=zelfstroomapp&scope=openid%20profile%20roles%20gateway_rights%20offline_access%20userId&response_type=code&redirect_uri=nl.zelfstroom.app%3A%2F%2Fcallback&response_mode=form_post&state=30906730-14d1-4083-ac3b-b763dd528acd&code_challenge=qjrzSW9gMiUgpUvqgEPE4_-8swvyCtfOVvg55o5S_es&code_challenge_method=S256");
	    String LocationUrl = null;
	    Map<String, String> LoginCookies;
	    public static String jsonAsString;
	    
		
		@Test(priority = 1)
		public void loginPost() {
			RestAssured.baseURI = BaseUrl;

			Response res = RestAssured.given()
					.redirects().max(12).redirects().follow(true).
					param("button", "login").
					param("Username", "Han").
					param("Password", "Vermolen").
					param("ReturnUrl", ReturnUrl).
				when()
					.post("/account/login").then().assertThat().statusCode(302).extract().response(); // Expected status code is "302", URL will redirect and set to callback url
			
			String responseStringLogin = res.asString();
			System.out.println(responseStringLogin);
			
			LoginCookies = res.getCookies();		
			
			RestAssured.requestSpecification = new RequestSpecBuilder().addCookies(LoginCookies).build();
			
			System.out.println("login post status code" + res.statusCode());																						
			LocationUrl = res.getHeader("Location");
			System.out.println("Raw Location URL :- " + LocationUrl);
			
			
//			//String encodeURL=encode(LocationUrl);
//			
//			
//			System.out.println("Encoded " + encodeURL);
//	
//			
//			System.out.println(encodeURL);
//			System.out.println();
//			System.out.println(LoginCookies);
			
			
			String response = given().urlEncodingEnabled(true).get("LocationUrl").asString();
			System.out.println("Print Response" +  response );

			Response res2 = given()
					.cookies(LoginCookies)
					.param("redirect_uri" , "nl.zelfstroom.app://callback")
					.param("client_id", "zelfstroomapp")
					.param("scope" , "openid profile roles gateway_rights userId offline_access")
					.param("response_type" , "code")
					.param("response_mode", "form_post")
					.param("state","30906730-14d1-4083-ac3b-b763dd528acd")
					.param("code_challenge", "qjrzSW9gMiUgpUvqgEPE4_-8swvyCtfOVvg55o5S_es")
					.param("code_challenge_method", "S256")
					.when().get("/connect/authorize/callback")
				    .then().assertThat().statusCode(200).extract().response();
			
			System.out.println("Generate code status code" + res2.statusCode());
			
			System.out.println("Res2 COMEBACK" + res2.getBody().asString());
			
			
			//declaring Array (Printing and Extracting the CODE values individually) 
			String[] temp;
			
			//making string ariabkle for storing the response 
			String str = res2.getBody().asString();
			
			//splitting the "Input" from the code value 
		    String delimeter = "input";
		    
		    // Actual splitting 
		    temp = str.split(delimeter);
		    
		    // System.out.println("Array  " + Arrays.toString(temp));
		    
		    // System.out.println("Array  " + temp[1]);
		    
		    
		    // creating one more array which store the substrings values
		    String[] temp2;
		    String delimeter2 = "value"; 
		    temp2 = temp[1].split(delimeter2);
		    
		    //Printing the unique CODE  
		    System.out.println("Code " + temp2[1].substring(2, temp2[1].length() - 6));
		    String Code =  temp2[1].substring(2, temp2[1].length() - 6);
		    
						
//		Genrated ID_Token , 
			
			Response res3 = given()
					.param("scope", "openid profile roles gateway_rights offline_access userId")
					.param("client_id", "zelfstroomapp") //yes
					.param("client_secret", "rhBNDktLtn4b6l9WTgmtzCvlUAPgFC") //yes
					.param("code", Code)
					.param("redirect_uri" , "nl.zelfstroom.app://callback")//yes
					.param("grant_type", "authorization_code")
					.param("code_verifier", "M25iVXpKU3puUjFaYWg3T1NDTDQtcW1ROUY5YXlwalNoc0hhakxifmZHag")
					.when().post("/connect/token")
				    .then().assertThat().statusCode(200).contentType(ContentType.JSON).extract().response();
			        System.out.println("Generate code status code" + res3.statusCode());
			        System.out.println("Res COMEBACK" + res3.getBody().asString());
			        
			        //Printing out id_token from the complete response
			        String id_token = res3.jsonPath().getString("id_token");
			        System.out.println("This is Id_Token:- " + id_token);
			        
			        //Printing out access_token from the complete response
			        String access_token = res3.jsonPath().getString("access_token");
			        System.out.println("This is access_Token:-  " + access_token);
			        

			Response res4 = given()
					        .header("Authorization", "Bearer" + " " + access_token)
							.when().get("/connect/userinfo")
						    .then().assertThat().statusCode(200).extract().response();
			                System.out.println( "Bearer" + " " + "access_token");
					        System.out.println("Generate code status code" + res4.statusCode());
					        System.out.println("UserInfo" + res4.getBody().asString());
					        
					        
		  // Getting name of the user
		     String given_name = res4.jsonPath().getString("given_name");
		     System.out.println("This is user_name:- " + given_name);
		     
		        
		     
		  // Lastname of the user 
		     String family_name = res4.jsonPath().getString("family_name");
		     System.out.println("This is family_name:- " + family_name);
			        
			
		
			
		}
		
	    }



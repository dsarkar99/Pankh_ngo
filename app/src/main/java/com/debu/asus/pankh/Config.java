/**
 * Configuration File 
 * Edit this data according to your Requirement
 * 
 * @author DroidOXY
 */

package com.debu.asus.pankh;



public class Config
{

	// Adxmi AppId
	static String AppId ="12fabad923e031bd";

	// Adxmi AppSecret
	static String AppSecret ="c8fd8e4f7b0d3730";

	// Server URL ie., Webpanel Hosted Url
	static String Base_Url = "http://akshaychatterjee.in/earn/pocket/";
	// static String Base_Url = "http://example.com/pocket/";
	
	// Daily Reward Points
	static int daily_reward = 35;
	

						
    //Titles for MainActivity
	static String[] titles ={"Daily Check-In",
			                    "Watch Videos",
			                     "Earning Wheel",

			                     "Instructions",
								"Redeem",
								"Contact Us"};
								
	//Description dor MainActuvity Titles
	static String[] description={"Open Daily and Earn 35 Points",
			                     "Watch Videos to Earn 20 Points",
								"Spin and Earn Points",
			                      "How to Earn Points",
								"Turn your Points into Cash",
								"Advertise with Us"};

	//---------------------------------------------------
	//Images for Redeem Activity

	

    // Splash screen delay (milliseconds) 1500 = 1.5 seconds
    static int splash_delay = 3000;
	
    // Google Analytics OPTIONAL
	static String analytics_property_id = "UA-76982496-1";

	// Share text and link for Share Button
    static String share_text = "Hello, its easy to earn now. Just download the App and watch videos to earn money:";
    static String share_link = "https://bit.ly/2yZLdOh";
	
	// APP RATING
	static String rate_later = "Perhaps Later";
    static String rate_never = "No Thanks";
    static String rate_yes="Rate Now";
    static String rate_message = "We hope you enjoy using %1$s. Would you like to help us by rating us in the Store?";
	static String rate_title = "Enjoying our app?";
	
	
	// Do not Edit Req_Url
	static String Req_Url = Base_Url+"receive_req.php";
}
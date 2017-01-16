package vn.hamalaja.mvc;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonObject;

/**
 * @author lamhm
 *
 */
public class RestfbApplication {
	public static void main(String[] args) {
		String accessToken = "EAACEdEose0cBAEoI37YJBUzaW8rZC0b7Ox5i8SCwFZAvETtc5UYI6whXee1mCLwADgLikZAwrZAF0RjCSCBJM6xUb3kxtvwbqW6TiZB03ZAvselkE3ZCwgkZC0cEOAcUmgIepGmKJf5JvvXnf4Ho9iENhQznZBGNu4TYbQiGOUGsm3wZDZD";
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_6);
		JsonObject user = facebookClient.fetchObject("me", JsonObject.class, Parameter.with("fields", "name,id"), Parameter.with("type", "large"),
				Parameter.with("redirect", "false"));
		System.out.println(user.toString());

		JsonObject picture = facebookClient.fetchObject("/me/picture", JsonObject.class, Parameter.with("type", "large"), Parameter.with("redirect", "false"));
		System.out.println(picture.toString());
		// https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=ya29.CjHVA3bOGnTHNEp28mmGd1lqgsE9Wuj1HGTWNxkWKTIi-a_ZdzCY31o_WbjHfvnzkalh
	}
}

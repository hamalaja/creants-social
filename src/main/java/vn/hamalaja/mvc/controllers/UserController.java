package vn.hamalaja.mvc.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import vn.hamalaja.mvc.services.AuthenticationService;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.json.JsonObject;

@Controller
@RequestMapping("/user")
public class UserController {
	private static final String FB_APP_ID = "758402830930110";
	private static final String GOOGLE_APP_ID = "232573012315-uuep50aqfh908j9iu35ssdg3t6g79696.apps.googleusercontent.com";
	private static final String FB_APP_SECRET = "0fd676859f0a66fa199eccfe3ba429f1";
	private static final String GOOGLE_APP_SECRET = "WXVUjPTvoPimh0qZu4fhoeAH";
	private static final String REDIRECT_URI = "http://localhost:8080/user/login/fb";
	private static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/user/login/google";
	@Autowired
	private AuthenticationService authService;

	@Value("${foo}")
	private int foo;

	@Autowired
	private RestTemplate restTemplate;


	@RequestMapping(value = "/login/{token}", method = RequestMethod.GET)
	public ModelAndView login(@PathVariable(required = true) String token) {
		ModelAndView mv = new ModelAndView("login");
		mv.addObject("user", authService.verify(token));
		return mv;
	}


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {

		model.put("title", "Vi du");
		model.put("msg", "Mo ta");

		return "login";
	}


	@RequestMapping(value = "/login/fb", method = RequestMethod.GET)
	public String loginFB(@RequestParam(value = "code", required = false) String code, Map<String, Object> model) throws UnsupportedEncodingException,
			RestClientException, URISyntaxException {
		if (code == null) {
			return "redirect:http://www.facebook.com/dialog/oauth?" + "client_id=" + FB_APP_ID + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8")
					+ "&scope=email";
		}

		String forObject = restTemplate.getForObject(new URI("https://graph.facebook.com/oauth/access_token?" + "client_id=" + FB_APP_ID + "&redirect_uri="
				+ URLEncoder.encode(REDIRECT_URI, "UTF-8") + "&client_secret=" + FB_APP_SECRET + "&code=" + code), String.class);
		String[] items = StringUtils.split(forObject, "&");
		String accessToken = items[0].replaceFirst("access_token=", "");
		FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_5);
		JsonObject fetchObject = fbClient.fetchObject("me", JsonObject.class);
		model.put("title", fetchObject.toString());
		return "login";
	}


	@RequestMapping(value = "/login/google", method = RequestMethod.GET)
	public String loginGoogle(@RequestParam(value = "code", required = false) String code, Map<String, Object> model) throws Exception {
		if (code == null) {
			return "redirect:https://accounts.google.com/o/oauth2/auth?" + "client_id=" + GOOGLE_APP_ID + "&redirect_uri="
					+ URLEncoder.encode(GOOGLE_REDIRECT_URI, "UTF-8") + "&scope=email&response_type=code";
		}
		// http://blog.sodhanalibrary.com/2014/11/login-with-google-with-java-tutorial.html#.WFixALKLTiw
		System.out.println("[FATAL]" + code);
		String urlParameters = "code=" + code + "&client_id=" + GOOGLE_APP_ID + "&client_secret=" + GOOGLE_APP_SECRET + "&redirect_uri="
				+ URLEncoder.encode(GOOGLE_REDIRECT_URI, "UTF-8") + "&grant_type=authorization_code";

		URL url = new URL("https://accounts.google.com/o/oauth2/token");
		URLConnection urlConn = url.openConnection();
		urlConn.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
		writer.write(urlParameters);
		writer.flush();
		writer.close();

		String outputString = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			outputString += line;
		}
		reader.close();

		JsonObject jo = new JsonObject(outputString);
		url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + jo.getString("access_token"));
		urlConn = url.openConnection();
		outputString = "";
		reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		line = "";
		while ((line = reader.readLine()) != null) {
			outputString += line;
		}
		reader.close();

		model.put("title", outputString);
		return "login";
	}

}

package codes.id21110815.web.util;

import javax.servlet.http.Cookie;

public class CookieUtil {

	public static String getCookieValue(Cookie[] cookies, String cookieName) {
		String cookieValue = "";
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					cookieValue = cookie.getValue();
				}
			}
		}
		return cookieValue;
	}
}

package vn.hamalaja.mvc.services;

import vn.hamalaja.mvc.om.User;

/**
 * @author lamhm
 *
 */
public interface IAuthenticationService {
	User verify(String token);
}

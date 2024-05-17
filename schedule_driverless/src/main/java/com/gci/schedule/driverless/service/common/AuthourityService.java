package com.gci.schedule.driverless.service.common;



import com.gci.schedule.driverless.bean.enums.AuthMessage;
import com.gci.schedule.driverless.bean.schedule.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface AuthourityService {
	public AuthMessage initSession(String sessionId, String path, HttpSession session, HttpServletRequest req);
	public AuthMessage initSessionNew(String paramString, HttpSession session, HttpServletRequest req);

	public AuthMessage checkAuthServerParam(String sessionId, String path);
	public AuthMessage checkAuthServerParamNew(String paramString);

	public AuthMessage checkSession(HttpSession session);
	public AuthMessage checkSessionNew(HttpSession session);

	public User getUserByUserIdAndMenuId(Long userId, Long menuId);

}

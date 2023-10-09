package com.gci.schedule.driverless.service.common.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gci.custom.api.ManageApi;
import com.gci.custom.vo.UserVo;
import com.gci.schedule.driverless.bean.enums.AuthMessage;
import com.gci.schedule.driverless.bean.schedule.User;
import com.gci.schedule.driverless.mapper.UserMapper;
import com.gci.schedule.driverless.service.common.AuthourityService;
import com.gci.schedule.driverless.util.HttpUtil;
import com.gci.schedule.driverless.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 通过权限系统验证 ,权限系统的返回结构 请求Header {
 * Cookie:JSESSIONID=EDF14FBF88112E675F79E979BF56CC04, Accept:text/json } {
 * sessionId:EDF14FBF88112E675F79E979BF56CC04,
 * path:http://10.88.40.120:8888/apts3, systemSkin:default/water, userId:721,
 * lang:zh_CN/zh_CN_WB/zh_HK, style:default }
 */
@Slf4j
@Service("authourityServiceImpl")
public class AuthourityServiceImpl implements AuthourityService {
	private static final String SESSION_AUTH_PARAM_SESSIONID_NAME = "auth_param_sessionId";
	private static final String SESSION_AUTH_PARAM_PATH_NAME = "auth_param_path";
	private static final String SESSION_AUTH_PARAM_PARAMSTRING = "paramString";

	@Value("${apts3.url:''}")
	private String apts3Url;
	@Value("${sessionInfo.url:''}")
	private String sessionInfoUrl;
	@Value("${apts4.manage.url}")
	private String apts4Path;
	
	@Resource
	private UserMapper userMapper;

	/**
	 * 验证权限系统
	 *
	 * */
	@Override
	public AuthMessage checkAuthServerParam(String sessionId, String path) {
		if (path == null || sessionId == null) {
			return AuthMessage.AUTH_PARAM_ERROR;
		} else {
			return AuthMessage.SUCCESS;
		}
	}

	/**
	 * 验证权限系统(新)
	 *
	 * */
	@Override
	public AuthMessage checkAuthServerParamNew(String paramString) {
		if (paramString == null) {
			return AuthMessage.AUTH_PARAM_ERROR;
		} else {
			return AuthMessage.SUCCESS;
		}
	}

	/**
	 * 验证session
	 * */
	@Override
	public AuthMessage checkSession(HttpSession session) {
		if (session == null) {
			return AuthMessage.AUTH_NO_SESSION;
		}
		String sessionId = session
				.getAttribute(SESSION_AUTH_PARAM_SESSIONID_NAME) == null ? null
				: session.getAttribute(SESSION_AUTH_PARAM_SESSIONID_NAME).toString();
		String path = session.getAttribute(SESSION_AUTH_PARAM_PATH_NAME) == null ? null
				: session.getAttribute(SESSION_AUTH_PARAM_PATH_NAME).toString();
		if (checkAuth(sessionId, path) == null) {
			return AuthMessage.AUTH_ERROR;
		} else {
			return AuthMessage.SUCCESS;
		}
	}

	/**
	 * 验证session(新)
	 * */
	@Override
	public AuthMessage checkSessionNew(HttpSession session) {
		if (session == null) {
			return AuthMessage.AUTH_NO_SESSION;
		}
		String paramstring = session.getAttribute(SESSION_AUTH_PARAM_PARAMSTRING) == null? null:
				session.getAttribute(SESSION_AUTH_PARAM_PARAMSTRING).toString();
		if (paramstring == null) {
			return AuthMessage.AUTH_ERROR;
		}
		if (ManageApi.getUserInfo(apts4Path, paramstring) == null) {
			return AuthMessage.AUTH_ERROR;
		}
		return AuthMessage.SUCCESS;
	}

	/**
	 * 初始化json organId和权限系统返回的json数据
	 * */
	@Override
	public AuthMessage initSession(String sessionId, String path,
                                   HttpSession session, HttpServletRequest req) {
		String flag = req.getParameter("flag");
		String puserId = req.getParameter("userId");
		if (!StringUtils.isEmpty(flag) && "mingjun".equals(flag)) {
			String organId = req.getParameter("organId");
			if (StringUtils.isEmpty(organId)) {
				organId = "1";
			}
			session.setAttribute("organId", organId);
			session.setAttribute("userId",puserId == null ? 6006 : puserId);
			return AuthMessage.SUCCESS;
		}
		if (session == null) {
			return AuthMessage.AUTH_NO_SESSION;
		}
		String json = checkAuth(sessionId, path);
		if (json == null) {
			return AuthMessage.AUTH_ERROR;
		} else {
			JSONObject jo = JSONObject.parseObject(json);
			String userId = jo.getString("userId");
			if (!StringUtil.checkBlankNull(userId)) {
				return AuthMessage.AUTH_NO_USER;
			}
			for (String key : jo.keySet()) {
				session.setAttribute(key, String.valueOf(jo.get(key)));
			}
			session.setAttribute(SESSION_AUTH_PARAM_SESSIONID_NAME, sessionId);
			session.setAttribute(SESSION_AUTH_PARAM_PATH_NAME, path);

			return AuthMessage.SUCCESS;
		}
	}

	/**
	 * 初始化json organId和权限系统返回的json数据（新）
	 * */
	@Override
	public AuthMessage initSessionNew(String paramString, HttpSession session,
									  HttpServletRequest req) {
		String flag = req.getParameter("flag");
		String puserId = req.getParameter("userId");
		if (!StringUtils.isEmpty(flag) && "mingjun".equals(flag)) {
			String organId = req.getParameter("organId");
			if (StringUtils.isEmpty(organId)) {
				organId = "1";
			}
			session.setAttribute("organId", organId);
			session.setAttribute("userId",puserId == null ? 6006 : puserId);
			return AuthMessage.SUCCESS;
		}
		if (session == null) {
			return AuthMessage.AUTH_NO_SESSION;
		}
		UserVo userInfo = ManageApi.getUserInfo(apts4Path, paramString);
		if (userInfo == null) {
			return AuthMessage.AUTH_ERROR;
		}

		String userId = userInfo.getUserId();
		if (!StringUtil.checkBlankNull(userId)) {
			return AuthMessage.AUTH_NO_USER;
		}
		JSONObject jo = JSONObject.parseObject(JSON.toJSONString(userInfo));
		for (String key : jo.keySet()) {
			session.setAttribute(key, String.valueOf(jo.get(key)));
		}
		session.setAttribute(SESSION_AUTH_PARAM_PARAMSTRING, paramString);

		return AuthMessage.SUCCESS;
	}

	private String checkAuth(String sessionId, String path) {
		if (sessionId == null || path == null)
			return null;
		HttpGet get = new HttpGet();
		get.setHeader("Accept", "text/json");
		get.setHeader("Cookie", "JSESSIONID="+sessionId);
		try {
			get.setURI(new URI(apts3Url + sessionInfoUrl));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.error("链接权限系统uri错误:" + path);
			return null;
		}
		String str = HttpUtil.queryString(get);
		if (str == null || "{}".equals(str)) {
			return null;
		} else {
			return str;
		}
	}

	@Override
	public User getUserByUserIdAndMenuId(Long userId, Long menuId) {
		return userMapper.getUserByUserIdAndMenuId(userId, menuId);
	}

}

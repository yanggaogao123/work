package com.gci.schedule.driverless.bean.enums;


public enum AuthMessage {
	AUTH_ERROR("权限验证失败,当前用户未登录", false), //
	AUTH_LINK_ERROR("权限验证失败,权限验证服务器访问失败", false), //
	AUTH_PARAM_ERROR("权限验证失败,权限验证参数错误", false), 
	AUTH_SEVER_PATH_ERROR("权限验证失败,权限验证服务器地址不是有效的地址", false), 
	AUTH_NO_SESSION("权限验证失败,未获取到session", false), 
	AUTH_NO_USER("权限验证失败,未获取到用户信息", false), 
	SUCCESS("成功", true);
	private String message;
	private boolean success;

	private AuthMessage(String message, boolean success) {
		this.message = message;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public boolean getSuccess() {
		return success;
	}
}

package iot.mike.malayans.module.client;

import com.alibaba.fastjson.JSON;

public class ToolsUtil {
	private ToolsUtil() {}
	
	/**
	 * 将类转换为Json串
	 * @param target目标类 
	 * @return JSON Str
	 */
	public static String getJsonOrder(Object target) {
		String json_Order = JSON.toJSONString(target);
		return json_Order;
	}
}

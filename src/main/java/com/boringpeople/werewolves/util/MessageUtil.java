package com.boringpeople.werewolves.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.boringpeople.werewolves.message.MessageType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.boringpeople.werewolves.message.Message;

public class MessageUtil {

	private static SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");

    public static MessageType getMessageType(byte[] data){
        JSONObject json = new JSONObject(new String(data));
        return MessageType.transform(json.optString("type"));
    }

	public static <T extends Message> T deSerializeMessage(byte[] data, T msg) {
		if (data == null || data.length < 1) {
			return null;
		}
		return deSerializeMessage(new String(data), msg);
	}

	public static <T extends Message> T deSerializeMessage(String str, T msg) {

		try {
			JSONObject json = new JSONObject(str);
			if (msg.type == MessageType.transform(json.optString("type"))) {
				JSONArray jarray = json.names();
				Class<?> zlass = msg.getClass();
				Map<String, Field> fieldmap = new HashMap<String, Field>();
				for (Field field : zlass.getFields()) {
					fieldmap.put(field.getName(), field);
				}
				for (int i = 0; i < jarray.length(); i++) {
					Object t = jarray.get(i);
					if (t != null) {
						String key = t.toString();
						if ("type".equals(key)) {
							continue;
						}
						if (fieldmap.containsKey(key)) {
							Field field = fieldmap.get(key);
							field.setAccessible(false);
							try {
								if (field.getType().isAssignableFrom(Date.class)) {
									String value = json.optString(key);
									if (value != null && !"".equals(value.trim())) {
										field.set(msg, _dateFormat.parse(value));
									}
								} else {
									Object value = json.opt(key);
									field.set(msg, value);
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				}
				return msg;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String serializeMessage(Message msg) {
		JSONObject json = new JSONObject();
		Field[] fields = msg.getClass().getFields();
		for (Field field : fields) {

			try {
				if (field.getType().isAssignableFrom(Date.class)) {
					Object t=field.get(msg);
					json.put(field.getName(), _dateFormat.format(t == null ? new Date() : t));
				} else {
					json.put(field.getName(), field.get(msg));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json.toString();
	}
}

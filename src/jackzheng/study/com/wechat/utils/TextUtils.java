package jackzheng.study.com.wechat.utils;

public class TextUtils {
	public static boolean isEmpty(String str) {
		if(str == null ) {
			return true;
		}else if(str.isEmpty()){
			return true;
		}
		return false;
		
	}
}

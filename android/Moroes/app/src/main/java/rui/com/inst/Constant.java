package rui.com.inst;

public interface Constant {
	String DATA_LAST_IN_SCOPE = "LastInScope";
	String DATA_LAST_OUT_SCOPE = "LastOutScope";

	String DATA_IN_SCOPE_RIGHT_BEFORE = "InScopeRightBefore";
	int IN_SCOPE_RIGHT_BEFORE_YES = 0;
	int IN_SCOPE_RIGHT_BEFORE_NO = 1;
	int IN_SCOPE_RIGHT_BEFORE_UNKNOWN = -1;

	String SHARE_PREFERENCE_NAME = "MoroesSP";
	String DB_NAME = "db.moroes";
	String TABLE_NAME_LOCATION = "location";

	int DEFAULT_DETECT_DISTANCE = 100;

	double SCOPE_LATITUDE = 41.728767d;
	double SCOPE_LONGTITUDE = 123.453747d;
}

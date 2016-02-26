package rui.com.inst;

/**
 *
 */
public class MoroesOrder {
	private static MoroesOrder OPTION;

	private MoroesOrder() {
		//init for it
	}

	public static MoroesOrder option() {
		if (OPTION == null) {
			OPTION = new MoroesOrder();
		}

		return OPTION;
	}


}

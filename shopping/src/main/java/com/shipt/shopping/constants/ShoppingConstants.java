package com.shipt.shopping.constants;

public class ShoppingConstants {
	
	public static enum orderStatusType{
		READY (1), ON_ITS_WAY (2), DELIVERED (3);
		
		private final int type;
		private static orderStatusType[] orderStatusTypes = values();
		
		private orderStatusType(int type) {
			this.type = type;
		}
		
		public int getValue() {
			return this.type;
		}
		
		public static String valueOf(int type) {
			for(orderStatusType orType: orderStatusTypes) {
				if(orType.getValue() == type) {
					return orType.toString();
				}
			}
			return null;
		}
	}
	
	public static enum productGroupType{
		DAY (1), WEEK (2), MONTH (3);
		
		private final int type;
		private static productGroupType[] productGroupTypes = values();
		
		private productGroupType(int type) {
			this.type = type;
		}
		
		public int getValue() {
			return this.type;
		}
		
		public static String valueOf(int type) {
			for(productGroupType prType: productGroupTypes) {
				if(prType.getValue() == type) {
					return prType.toString();
				}
			}
			return null;
		}
	}
}

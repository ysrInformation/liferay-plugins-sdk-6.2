package com.htmsd.util;

public class NumberWordConverter {

	public static final String[] units = {
		"", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
		"Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen",
		"Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
	};

	public static final String[] tens = {
		"",        // 0
		"",        // 1
		"Twenty",  // 2
		"Thirty",  // 3
		"Forty",   // 4
		"Fifty",   // 5
		"Sixty",   // 6
		"Seventy", // 7
		"Eighty",  // 8
		"Ninety"   // 9
	};

	public static String convert(final int n) {
		if (n < 0) {
			return "minus " + convert(-n);
		}

		if (n < 20) {
			return units[n];
		}

		if (n < 100) {
			return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
		}

		if (n < 1000) {
			return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
		}

		if (n < 1000000) {
			return convert(n / 1000) + " Thousand" + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
		}

		if (n < 1000000000) {
			return convert(n / 1000000) + " Million" + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
		}

		return convert(n / 1000000000) + " Billion"  + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
	}

}

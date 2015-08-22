function formatPrice(number) {
	accounting.settings.currency.symbol = '&#8377;';
	var text = '';
	text = accounting.formatMoney(Math.abs(number));
	return text;
}
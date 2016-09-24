function formatPrice(number) {
	accounting.settings.currency.symbol = '&#8377;';
	var text = '';
	text = accounting.formatMoney(Math.abs(number));
	return text;
}

function showPageInPopup(url, height, width, title){
	AUI().use('aui-base','liferay-util-window','aui-io-plugin-deprecated',function(A){
    	var popup = Liferay.Util.Window.getWindow(
                {
                    dialog: {
                        centered: true,
                        constrain2view: true,
                        modal: true,
                        resizable: false,
                        width: width
                    }
                }).plug(A.Plugin.DialogIframe,
                     {
                     autoLoad: true,
                     iframeCssClass: 'dialog-iframe',
                     uri:url
                }).render();
    		popup.show();
    		popup.titleNode.html(title);
	});
}
/*===============================================================+
 |               Junction of Web UI and the Backend              |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

var ZeT  = JsX.once('zet/app.js')
var ZeTA = JsX.once('zet/arrays.js')

/**
 * Each user (login for a person) has collection
 * of application links with default items from
 * the standard menu. This function provides them.
 */
function getUserLinks(domain, login)
{
	var isSystem = (login.getCode() == 'System')

	var main = [

		{
			text  : 'Документы',
			hint  : 'Основные документы',
			icon  : 'retrade-document-icon',
			color : 'G'
		},

		{
			text  : 'Документы изменения цен',
			icon  : 'retrade-price-delta-icon'
		},

		{
			text  : 'Платёжные документы',
			icon  : 'retrade-payment-icon'
		},

		{
			text  : 'Мои отчёты',
			icon  : 'retrade-reports-icon'
		}
	]


	var catalogs = [

		{
			text  : 'Товары',
			hint  : 'Товары, товары на складах, цены товаров',
			icon  : 'retrade-goods-icon'
		},

		{
			text  : 'Каталог товаров',
			hint  : 'Иерархический каталог товаров',
			icon  : 'retrade-goods-tree-icon'
		},

		{
			text  : 'Единицы измерения',
			hint  : 'Справочник единиц измерения',
			icon  : 'retrade-measure-icon'
		},

		{
			text  : 'Склады',
			hint  : 'Справочник складов',
			icon  : 'retrade-trade-store-icon'
		},

		{
			text  : 'Контрагенты',
			hint  : 'Справочник контрагентов',
			icon  : 'retrade-contractor-icon'
		},

		{
			text  : 'Прайс-листы',
			hint  : 'Справочник прайс-листов',
			icon  : 'retrade-prices-icon'
		},

		{
			text  : 'Прайс-листы контрагентов',
			hint  : 'Справочник сопоставления прайс-листов контаргентам',
			icon  : 'retrade-prices-icon'
		},

		{
			text  : 'Прайс-листы контрагентов',
			hint  : 'Справочник сопоставления прайс-листов контаргентам',
			icon  : 'retrade-prices-icon'
		}
	]


	var system = !isSystem?(null):[

		{
			text  : 'Данные и отчёты',
			icon  : 'retrade-reports-icon',
			color : 'O'
		},

		{
			text  : 'Пользователи',
			icon  : 'retrade-user-icon',
			color : 'O'
		},

		{
			text  : 'Правила доступа',
			icon  : 'retrade-secrule-icon',
			color : 'O'
		},

		{
			text  : 'Множества правил',
			icon  : 'retrade-secset-icon',
			color : 'O'
		}
	]

	return ZeT.o2s(ZeTA.combine(
		main, catalogs, system
	))
}
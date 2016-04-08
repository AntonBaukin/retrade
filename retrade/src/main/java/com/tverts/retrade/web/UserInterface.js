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
 *
 * Format of open.box objects equals to one of
 * retrade_open_window(), which passes it to
 * ReTrade.desktop.calcWindowBox().
 * See my-links.xhtml script for additional
 * fields of open object.
 */
function genUserLinks(authLogin)
{
	var isSystem = (authLogin.getCode() == 'System')

	var main = [

		{
			id    : 'documents',
			text  : 'Документы',
			hint  : 'Основные документы',
			icon  : 'retrade-document-icon',
			color : 'G',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:docs',
				link   : '/docs/documents'
			}
		},

		{
			id    : 'reprace-docs',
			text  : 'Документы изменения цен',
			icon  : 'retrade-price-delta-icon',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:reprice-docs',
				link   : '/reprice-docs/list'
			}
		},

		{
			id    : 'my-reports',
			text  : 'Мои отчёты',
			icon  : 'retrade-reports-icon',
			open  : {
				domain : 'window:main-menu:my-reports',
				box    : { widthpt: 560, heightpt: 260 },
				link   : '/datas/my-reports'
			}
		}
	]


	var catalogs = [

		{
			id    : 'goods-nav',
			text  : 'Товары и услуги',
			hint  : 'Таблица товаров и услуг с навигацией',
			icon  : 'retrade-goods-icon',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:goods-nav',
				link   : '/goods/list-nav'
			}
		},

		{
			id    : 'goods-stores-prices',
			text  : 'Товары, склады, цены',
			hint  : 'Товары, товары на складах, цены товаров',
			icon  : 'retrade-goods-icon',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:goods-stores-prices',
				link   : '/goods/goods-stores-prices'
			}
		},

		{
			id    : 'goods-tree',
			text  : 'Категории товаров',
			hint  : 'Иерархический каталог категорий товаров',
			icon  : 'retrade-goods-tree-icon',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:goods-tree',
				link   : '/goods/goods-tree'
			}
		},

		{
			id    : 'measures',
			text  : 'Единицы измерения',
			hint  : 'Справочник единиц измерения',
			icon  : 'retrade-measure-icon',
			open  : {
				domain : 'window:main-menu:measures',
				box    : { widthpt: 400, heightpt: 320 },
				link   : '/goods/list-measures'
			}
		},

		{
			id    : 'stores',
			text  : 'Склады',
			hint  : 'Справочник складов',
			icon  : 'retrade-trade-store-icon',
			open  : {
				domain : 'window:main-menu:stores',
				box    : { widthpt: 360, heightpt: 240 },
				link   : '/stores/list-win'
			}
		},

		{
			id    : 'firms',
			text  : 'Контрагенты',
			hint  : 'Справочник контрагентов',
			icon  : 'retrade-contractor-icon',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:firms',
				link   : '/firms/list'
			}
		},

		{
			id    : 'price-lists',
			text  : 'Прайс-листы',
			hint  : 'Справочник прайс-листов',
			icon  : 'retrade-prices-icon',
			open  : {
				domain : 'window:main-menu:price-lists',
				box    : { widthpt: 360, heightpt: 240 },
				link   : '/price-lists/list-win'
			}
		},

		{
			id    : 'price-lists-firms',
			text  : 'Прайс-листы контрагентов',
			hint  : 'Справочник сопоставления прайс-листов контаргентам',
			icon  : 'retrade-prices-icon',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:firms-price-lists',
				link   : '/firms/list-lists'
			}
		}
	]


	var system = !isSystem?(null):[

		{
			id    : 'system: good attrs',
			text  : 'Атрибуты товаров',
			icon  : 'retrade-good-attrs-icon',
			color : 'O',
			open  : {
				domain : 'window:main-menu:good-attrs',
				box    : { widthpt: 460, heightpt: 380 },
				link   : '/goods/attrs'
			}
		},

		{
			id    : 'system: reports',
			text  : 'Данные и отчёты',
			icon  : 'retrade-reports-icon',
			color : 'O',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:datas',
				link   : '/datas/list'
			}
		},

		{
			id    : 'system: users',
			text  : 'Пользователи',
			icon  : 'retrade-user-icon',
			color : 'O',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:settings:users',
				link   : '/settings/users'
			}
		},

		{
			id    : 'system: secure rules',
			text  : 'Правила доступа',
			icon  : 'retrade-secrule-icon',
			color : 'O',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:settings:secure-rules',
				link   : '/settings/secure-rules'
			}
		},

		{
			id    : 'system: secure sets',
			text  : 'Множества правил',
			icon  : 'retrade-secset-icon',
			color : 'O',
			open  : {
				panel  : 'center',
				domain : 'desktop:main-menu:settings:secure-sets',
				link   : '/settings/secure-sets'
			}
		}
	]

	return ZeT.o2s(ZeTA.combine(
		main, catalogs, system
	))
}
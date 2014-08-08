/*===============================================================+
 |                                                               |
 |   Ext JS Data Models for ReTrade                              |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/

ZeT.init('init: retrade.data', function()
{
	Ext.require('Ext.util.Format')
	Ext.syncRequire('Ext.data.Model')

	//~: make animation slower
	Ext.fx.Manager.interval = 40; //<- 25 fps

	//<: form validation response model

	Ext.define('retrade.model.FormValidation', {
	  extend: 'Ext.data.Model',

	  idProperty: 'target',

	  fields: [

	    {name: 'target', type: 'string', mapping: '@target'},
	    {name: 'error', type: 'string'}

	  ]
	})

	//>: form validation response model


	//<: retrade documents view model, columns and reader

	Ext.define('retrade.model.DocumentView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'docViewKey', type: 'string'},

	    {name: 'docTypeLo',  type: 'string'},
	    {name: 'docStateLo', type: 'string'},

	    {name: 'docCode',    type: 'string'},
	    {name: 'docDate',    type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'docText',    type: 'string'},
	    {name: 'docCost',    type: 'string'},

	    {name: 'storeKey',   type: 'string'},
	    {name: 'storeCode',  type: 'string'},
	    {name: 'storeName',  type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.DocumentView', function()
	{
		return [

		 {
		   text: "Код документа", dataIndex: 'docCode', sortable: false,
		   width: extjsf.ex(22), renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Дата и время", dataIndex: 'docDate', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16)
		 },

		 {
		   text: "Тип документа", dataIndex: 'docTypeLo', sortable: false,
		   width: extjsf.ex(28)
		 },

		 {
		   text: "Состояние", dataIndex: 'docStateLo', sortable: false,
		   width: extjsf.ex(18)
		 },

		 {
		   text: "Сумма", dataIndex: 'docCost', sortable: false,
		   width: extjsf.ex(16), align: 'right',
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.define('retrade.readers.DocumentView', {

	  type: 'xml', root: 'documents', record: 'document',
	  totalProperty: 'documentsNumber'

	})

	//>: retrade documents view model, columns and reader


	//<: invoice view model, columns and reader

	Ext.define('retrade.model.InvoiceView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',        type: 'string'},
	    {name: 'invoiceType',      type: 'string'},
	    {name: 'invoiceTypeName',  type: 'string'},
	    {name: 'invoiceStateName', type: 'string'},
	    {name: 'invoiceCode',      type: 'string'},
	    {name: 'invoiceDate',      type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'goodsCost',        type: 'string'}

	  ]
	})

	ZeT.defineDelay('retrade.columns.InvoiceView', function()
	{
		return [

		 {
		   text: 'Код накладной', dataIndex: 'invoiceCode', sortable: false,
		   width: extjsf.ex(18), flex: 0
		 },

		 {
		   text: "Дата и время", dataIndex: 'invoiceDate', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Тип накладной", dataIndex: 'invoiceTypeName', sortable: false,
		   width: extjsf.ex(24), flex: 1
		 },

		 {
		   text: "Состояние", dataIndex: 'invoiceStateName', sortable: false,
		   width: extjsf.ex(18), flex: 1
		 },

		 {
		   text: "Сумма", dataIndex: 'goodsCost', sortable: false,
		   width: extjsf.ex(12), align: 'right',
		  renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceBuySellView', function()
	{
		return [

		 {
		   text: 'Код накладной', dataIndex: 'invoiceCode', sortable: false,
		   width: extjsf.ex(18), flex: 1
		 },

		 {
		   text: "Дата и время", dataIndex: 'invoiceDate', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Состояние", dataIndex: 'invoiceStateName', sortable: false,
		   width: extjsf.ex(18), flex: 0
		 },

		 {
		   text: "Сумма", dataIndex: 'goodsCost', sortable: false,
		   width: extjsf.ex(16), align: 'right',
		  renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceMoveView', function()
	{
		return [

		 {
		   text: 'Код накладной', dataIndex: 'invoiceCode', sortable: false,
		   width: extjsf.ex(18), flex: 0
		 },

		 {
		   text: "Дата и время", dataIndex: 'invoiceDate', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Состояние", dataIndex: 'invoiceStateName', sortable: false,
		   width: extjsf.ex(18), flex: 1
		 }
		];
	})

	ZeT.define('retrade.readers.DateCloseInvoices', {

	  type: 'xml', root: 'date-close-invoices', record: 'invoice'

	})

	//>: invoice view model, columns and reader


	//<: invoice good model, columns and reader

	Ext.define('retrade.model.InvoiceGood', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',       type: 'string'},
	    {name: 'goodUnit',        type: 'string'},
	    {name: 'goodCode',        type: 'string'},
	    {name: 'goodName',        type: 'string'},
	    {name: 'goodVolume',      type: 'string'},
	    {name: 'goodVolumeDelta', type: 'string'},
	    {name: 'volumeUnitName',  type: 'string'},
	    {name: 'volumeInteger',   type: 'boolean'},
	    {name: 'volumeCost',      type: 'string'},
	    {name: 'unitCost',        type: 'string'},
	    {name: 'goodPrice',       type: 'string'},
	    {name: 'priceList',       type: 'string'},
	    {name: 'priceListCode',   type: 'string'},
	    {name: 'priceListName',   type: 'string'},
	    {name: 'moveOn',          type: 'string'},
	    {name: 'needCalc',        type: 'string'},
	    {name: 'calcDate',        type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'goodSemiReady',   type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.InvoiceGood', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Цена", dataIndex: 'volumeCost', sortable: false,
		   width: extjsf.ex(16), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceGoodEdited', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Цена всего", dataIndex: 'volumeCost', sortable: false,
		   width: extjsf.ex(16), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: "Цена 1ед", dataIndex: 'unitCost', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceSellGood', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 2
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Прайс-лист", dataIndex: 'priceListName', sortable: false,
		   width: extjsf.ex(14), flex: 1, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  '№', rec.get('priceListCode'), '; ', v
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: "Цена", dataIndex: 'volumeCost', sortable: false,
		   width: extjsf.ex(16), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	function needCalcRenderer(v, meta)
	{
		var t; if(ZeT.i$x(v) || ZeTS.ises(v))
		{
			v = '';
			t = 'По умолчанию: производство через данную позицию будет продолжено, если указанный товар является продуктом, но не полуфабрикатом';
		}
		else if((v === 'true') || (v === true))
		{
			v = 'да';
			t = 'Да: производство продукта в данной позиции будет продолжено, даже если указанный товар является полуфабрикатом';
		}
		else
		{
			v = 'нет';
			t = 'Нет: производство продукта в данной позиции не будет продолжено в любом случае (прямое списание)';
		}

		meta.tdAttr = 'title="' + Ext.String.htmlEncode(t) + '"';
		return v;
	}

	ZeT.defineDelay('retrade.columns.InvoiceSellGoodEdited', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 2
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Прайс-лист", dataIndex: 'priceListName', sortable: false,
		   width: extjsf.ex(14), flex: 1, renderer: function(v, meta, rec)
		   {
				var t; if(!ZeTS.ises(rec.get('priceList')))
					t = ZeTS.cat('Прайс-лист №', rec.get('priceListCode'), ': ', v);
				else { v = ''; t = 'Продажа по указанной цене (без прайс-листа)' }

				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(t), '"' );
				return v;
		   }
		 },

		 {
		   text: "Цена всего", dataIndex: 'volumeCost', sortable: false,
		   width: extjsf.ex(16), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: "Цена 1ед", dataIndex: 'unitCost', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: "А/п", dataIndex: 'needCalc', sortable: false,
		   width: extjsf.ex(8), flex: 0, renderer: needCalcRenderer
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceMoveGood', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceMoveAltGood', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Тип", dataIndex: 'goodSemiReady', sortable: false,
		   width: extjsf.ex(6), flex: 0, renderer: calcSemiReadyRenderer
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceMoveGoodEdited', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.VolumeCheckFixed', function()
	{
		return [

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Ост. объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Коррекция", dataIndex: 'goodVolumeDelta', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Статус", dataIndex: 'goodVolumeDelta', sortable: false,
		   width: extjsf.ex(14), flex: 0, renderer : function(v)
		   {
		     if(ZeTS.ises(v)) return '';
		     var n = parseFloat(v);
		     return (n == 0)?('совпал'):(n < 0)?('недостаток'):('избыток');
		   }
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.VolumeCheckEdit', function()
	{
		return [

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Отс. объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.InvoiceResGood', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 2
		 },

		 {
		   text: "Тип", dataIndex: 'goodSemiReady', sortable: false,
		   width: extjsf.ex(6), flex: 0, renderer: calcSemiReadyRenderer
		 },

		 {
		   text: "Формула от", dataIndex: 'calcDate', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Объём", dataIndex: 'goodVolume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'volumeUnitName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }

		];
	})

	ZeT.define('retrade.readers.InvoiceGood', {
	  type: 'xml', root: 'goods', record: 'good'
	})

	ZeT.define('retrade.readers.InvoiceResGood', {
	  type: 'xml', root: 'results', record: 'good'
	})

	//<: invoice good model, columns and reader


	//<: good unit view

	Ext.define('retrade.model.GoodUnitView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',    type: 'string'},
	    {name: 'goodCode',     type: 'string'},
	    {name: 'goodName',     type: 'string'},
	    {name: 'measureKey',   type: 'string'},
	    {name: 'measureCode',  type: 'string'},
	    {name: 'measureName',  type: 'string'},
	    {name: 'restCost',     type: 'string'},
	    {name: 'price',        type: 'string'},
	    {name: 'volume',       type: 'string'},
	    {name: 'semiReady',    type: 'string'},
	    {name: 'integer',      type: 'boolean'},
	    {name: 'cost',         type: 'string'},
	    {name: 'storeVolume',  type: 'string'},
	    {name: 'storeCode',    type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.GoodUnitViewShort', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(22), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.GoodUnitViewInvoiceBuy', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.GoodUnitView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: 'Объём', dataIndex: 'volume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: 'Себест.', dataIndex: 'restCost', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.GoodUnitStoreView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: 'Объём', dataIndex: 'volume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.GoodUnitPriceView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: 'Цена', dataIndex: 'price', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.define('retrade.readers.GoodUnitView', {

	  type: 'xml', root: 'good-units', record: 'good-unit',
	  totalProperty: 'goodsNumber'

	})

	//>: good unit view


	//<: good calc view

	Ext.define('retrade.model.GoodCalcView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',    type: 'string'},
	    {name: 'goodUnit',     type: 'string'},
	    {name: 'openTime',     type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'closeTime',    type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'semiReady',    type: 'boolean'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.GoodCalcView', function()
	{
		return [

		 {
		   text: "Открыта", dataIndex: 'openTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Закрыта", dataIndex: 'closeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Тип товара", dataIndex: 'semiReady', sortable: false,
		   width: extjsf.ex(18), flex: 0, renderer: function(v)
		   {
				return v?('Полуфабрикат'):('Продукт/ингредиент');
		   }
		 }
		];
	})

	ZeT.define('retrade.readers.GoodCalcView', {
	  type: 'xml', root: 'good-calcs', record: 'good-calc'
	})

	//>: good calc view


	//<: good calc part view

	Ext.define('retrade.model.GoodCalcPartView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',     type: 'string'},
	    {name: 'goodUnit',      type: 'string'},
	    {name: 'goodCode',      type: 'string'},
	    {name: 'goodName',      type: 'string'},
	    {name: 'goodSemiReady', type: 'string'},
	    {name: 'measureName',   type: 'string'},
	    {name: 'integer',       type: 'boolean'},
	    {name: 'volume',        type: 'string'},
	    {name: 'semiReady',     type: 'string'}
	  ]
	})

	function calcSemiReadyRenderer(v, meta)
	{
		var t; if(ZeT.i$x(v) || ZeTS.ises(v))
		{
			v = 'с';
			t = 'Сырьё (закупаемые товары)';
		}
		else if((v === 'true') || (v === true))
		{
			v = 'п/т';
			t = 'Полуфабрикат (закупаемые и-или производимые товары)';
		}
		else
		{
			v = 'п-и';
			t = 'Продукт/ингредиент (производимые товары)';
		}

		meta.tdAttr = 'title="' + Ext.String.htmlEncode(t) + '"';
		return v;
	}

	ZeT.defineDelay('retrade.columns.GoodCalcPartView', function()
	{
		return [

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: "Объём", dataIndex: 'volume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'measureName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Тип", dataIndex: 'goodSemiReady', sortable: false,
		   width: extjsf.ex(6), flex: 0, renderer: calcSemiReadyRenderer
		 },

		 {
		   text: "Прод.", dataIndex: 'semiReady', sortable: false,
		   width: extjsf.ex(8), flex: 0, renderer: needCalcRenderer
		 }
		];
	})

	ZeT.define('retrade.readers.GoodCalcPartView', {
	  type: 'xml', root: 'calc-parts', record: 'calc-part'
	})

	//>: good calc part view


	//<: measure unit view

	Ext.define('retrade.model.MeasureUnitView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',    type: 'string'},
	    {name: 'code',         type: 'string'},
	    {name: 'name',         type: 'string'},
	    {name: 'classCode',    type: 'string'},
	    {name: 'classUnit',    type: 'string'},
	    {name: 'fractional',   type: 'boolean'}

	  ]
	})

	ZeT.defineDelay('retrade.columns.MeasureUnitView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: "ОКЕИ", dataIndex: 'classCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }

		];
	})

	ZeT.define('retrade.readers.MeasureUnitView', {

	  type: 'xml', root: 'measure-units', record: 'measure-unit'

	})

	//>: measure unit view


	//<: catalogue items

	Ext.define('retrade.model.CatItemView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'code',       type: 'string'},
	    {name: 'name',       type: 'string'}

	  ]
	})

	ZeT.defineDelay('retrade.columns.CatItemView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(48), flex: 1
		 }

		];
	})

	//>: catalogue items

	//~: domains
	ZeT.define('retrade.readers.DomainView', {

	  type: 'xml', root: 'domains', record: 'domain',
	  totalProperty: 'domainsNumber'

	})


	//~: trade stores
	ZeT.define('retrade.readers.TradeStoreView', {

	  type: 'xml', root: 'stores', record: 'store',
	  totalProperty: 'goodsNumber'

	})

	//>: trade stores


	//<: good prices & price lists

	Ext.define('retrade.model.GoodPriceView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',     type: 'string'},
	    {name: 'goodCode',      type: 'string'},
	    {name: 'goodName',      type: 'string'},
	    {name: 'measureKey',    type: 'string'},
	    {name: 'measureCode',   type: 'string'},
	    {name: 'measureName',   type: 'string'},
	    {name: 'price',         type: 'string'},
	    {name: 'goodPrice',     type: 'string'},
	    {name: 'priceList',     type: 'string'},
	    {name: 'priceListCode', type: 'string'},
	    {name: 'priceListName', type: 'string'}
	  ]
	})

	Ext.define('retrade.model.PriceListView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'code',       type: 'string'},
	    {name: 'name',       type: 'string'},
	    {name: 'parents',    type: 'string'},
	    {name: 'price',      type: 'string'}

	  ]
	})

	ZeT.defineDelay('retrade.columns.GoodPriceView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(22), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: 'Цена', dataIndex: 'price', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.ClientGoodPriceView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(22), flex: 2
		 },

		 {
		   text: "Прайс лист", dataIndex: 'priceListCode', sortable: false,
		   width: extjsf.ex(16), flex: 1, renderer: function(v, meta, rec)
		   {
				return v + ' ' + rec.get('priceListName');
		   }
		 },

		 {
		   text: 'Цена', dataIndex: 'price', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.PriceListView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: 'Цена', dataIndex: 'price', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.PriceListParentsView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: "Папки", dataIndex: 'parents', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 }
		];
	})

	ZeT.define('retrade.readers.PriceListView', {

	  type: 'xml', root: 'price-lists', record: 'price-list',
	  totalProperty: 'goodsNumber'

	})

	//>: good prices & price lists


	//<: price change (price history) documents

	Ext.define('retrade.model.GoodPriceHistory', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',       type: 'string'},
	    {name: 'changeTime',      type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'repriceKey',      type: 'string'},
	    {name: 'repriceName',     type: 'string'},
	    {name: 'priceCur',        type: 'string'},
	    {name: 'priceOld',        type: 'string'},
	    {name: 'priceNew',        type: 'string'},
	    {name: 'goodCode',        type: 'string'},
	    {name: 'goodName',        type: 'string'},
	    {name: 'measureName',     type: 'string'}
	  ]
	})


	ZeT.defineDelay('retrade.columns.GoodPriceHistory', function()
	{
		return [

		 {
		   text: "Дата и время", dataIndex: 'changeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Документ", dataIndex: 'repriceName', sortable: false,
		   width: extjsf.ex(36), flex: 1
		 },

		 {
		   text: 'Цена до', dataIndex: 'priceOld', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Цена после', dataIndex: 'priceNew', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})


	ZeT.define('retrade.readers.GoodPriceHistory', {

	  type: 'xml', root: 'prices-history', record: 'price-history',
	  totalProperty: 'pricesNumber'

	})


	ZeT.defineDelay('retrade.columns.PriceChange', function()
	{
		return [

		 { xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(24), flex: 1
		 },

		 {
		   text: "Ед. изм.", dataIndex: 'measureName', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: 'Цена до', dataIndex: 'priceOld', sortable: false,
		   width: extjsf.ex(14), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Цена после', dataIndex: 'priceNew', sortable: false,
		   width: extjsf.ex(14), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})


	ZeT.define('retrade.readers.PriceChange', {

	  type: 'xml', root: 'price-changes', record: 'price-change'

	})


	ZeT.defineDelay('retrade.columns.GoodPrice', function()
	{
		return [

		 {
		   text: "Код товара", dataIndex: 'goodCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: false,
		   width: extjsf.ex(24), flex: 1
		 },

		 {
		   text: "Ед. изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(8), flex: 0
		 },

		 {
		   text: 'Себест.', dataIndex: 'restCost', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Цена', dataIndex: 'price', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.define('retrade.readers.GoodPrice', {

	  type: 'xml', root: 'good-prices', record: 'good-price',
	  totalProperty: 'pricesNumber'

	})


	Ext.define('retrade.model.RepriceDoc', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',       type: 'string'},
	    {name: 'changeTime',      type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'code',            type: 'string'},
	    {name: 'priceList',       type: 'string'},
	    {name: 'changeReason',    type: 'string'}
	  ]
	})


	ZeT.defineDelay('retrade.columns.RepriceDoc', function()
	{
		return [

		 {
		   text: "Дата и время", dataIndex: 'changeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Код документа", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(20), flex: 0
		 },

		 {
		   text: "Прайс лист", dataIndex: 'priceList', sortable: false,
		   width: extjsf.ex(26), flex: 0
		 },

		 {
		   text: "Основание изменения", dataIndex: 'changeReason', sortable: false,
		   width: extjsf.ex(36), flex: 1
		 }
		];
	})


	ZeT.define('retrade.readers.RepriceDoc', {

	  type: 'xml', root: 'reprice-docs', record: 'reprice-doc',
	  totalProperty: 'repricesNumber'

	})

	//>: price change...


	//<: contractors

	Ext.define('retrade.model.ContractorView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'code',       type: 'string'},
	    {name: 'name',       type: 'string'},
	    {name: 'fullName',   type: 'string'},
	    {name: 'income',     type: 'string'},
	    {name: 'expense',    type: 'string'},
	    {name: 'balance',    type: 'string'}

	  ]
	})

	ZeT.defineDelay('retrade.columns.ContractorView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: 'Продажа', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(14), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Закупка', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(14), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Сальдо', dataIndex: 'balance', sortable: false,
		   width: extjsf.ex(14), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }

		];
	})

	ZeT.define('retrade.readers.ContractorView', {

	  type: 'xml', root: 'contractors', record: 'contractor',
	  totalProperty: 'contractorsNumber'

	})

	//>: contractors


	//<: auth logins

	Ext.define('retrade.model.AuthLoginView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'code',       type: 'string'},
	    {name: 'name',       type: 'string'},
	    {name: 'createTime', type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'closeTime',  type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'typeNameLo', type: 'string'},
	    {name: 'descr',      type: 'string'},
	    {name: 'lastName',   type: 'string'},
	    {name: 'firstName',  type: 'string'},
	    {name: 'middleName', type: 'string'},
	    {name: 'middleName', type: 'string'},
	    {name: 'genderMale', type: 'string'},
	    {name: 'email',      type: 'string'},
	    {name: 'phoneMob',   type: 'string'},
	    {name: 'phoneWork',  type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.AuthLoginView', function()
	{
		return [

		 {
		   text: "Логин", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(22), flex: 0
		 },

		 {
		   text: "Тип", dataIndex: 'typeNameLo', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Имя", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: "Открыт", dataIndex: 'createTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Блокирован", dataIndex: 'closeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.UsersView', function()
	{
		return [

		 {
		   text: "Логин", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(22), flex: 0
		 },

		 {
		   text: "Имя", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(48), flex: 1
		 },

		 {
		   text: "Открыт", dataIndex: 'createTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Блокирован", dataIndex: 'closeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 }
		];
	})

	ZeT.define('retrade.readers.AuthLoginView', {

	  type: 'xml', root: 'logins', record: 'login',
	  totalProperty: 'loginsNumber'

	})

	//>: auth logins


	//<: selection set

	ZeT.defineDelay('retrade.columns.SelSetView', function()
	{
		return [

		 {
		   text: "Объект", dataIndex: 'name',
		   sortable: false, flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 }
		];
	})

	ZeT.define('retrade.readers.SelSetView', {

	  type: 'xml', root: 'selset', record: 'item',
	  totalProperty: 'selSetSize'

	})

	//>: selection set


	//<: secure rules

	Ext.define('retrade.model.SecRuleView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey', type: 'string'},
	    {name: 'force',     type: 'string'},
	    {name: 'title',     type: 'string'},
	    {name: 'descr',     type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.SecRuleView', function()
	{
		return [
		 {
		   text: "Правило доступа", dataIndex: 'title',
		   sortable: false, flex: 1
		 }
		];
	})

	ZeT.define('retrade.readers.SecRuleView', {

	  type: 'xml', root: 'sec-rules', record: 'rule',
	  totalProperty: 'secRulesNumber'

	})

	//>: secure rules


	//<: login secure ables

	Ext.define('retrade.model.SecAbleView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'ableTime',   type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'setName',    type: 'string'},
	    {name: 'force',      type: 'string'},
	    {name: 'title',      type: 'string'},
	    {name: 'descr',      type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.SecAbleView', function()
	{
		return [

		 {
		   text: "Правило", dataIndex: 'title', sortable: false,
		   width: extjsf.ex(36), flex: 2
		 },

		 {
		   text: "Множество", dataIndex: 'setName', sortable: false,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: "Назначено", dataIndex: 'ableTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 }
		];
	})

	ZeT.define('retrade.readers.SecAbleView', {

	  type: 'xml', root: 'sec-ables', record: 'able',
	  totalProperty: 'secAblesNumber'

	})

	//>: login secure ables


	//<: secure set

	Ext.define('retrade.model.SecSetView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'createTime', type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'name',       type: 'string'},
	    {name: 'comment',    type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.SecSetView', function()
	{
		return [

		 {
		   text: "Имя множества правил", dataIndex: 'name',
		   sortable: false, width: extjsf.ex(36), flex: 1
		 },

		 {
		   text: "Создано", dataIndex: 'createTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 }
		];
	})

	ZeT.define('retrade.readers.SecSetView', {

	  type: 'xml', root: 'sec-sets', record: 'set',
	  totalProperty: 'secSetsNumber'

	})

	//>: secure set


	//<: catalogue items as tree

	Ext.define('retrade.model.CatItemTree', {
	  extend: 'Ext.data.TreeModel',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'parentKey',  type: 'string'},
	    {name: 'itemKey',    type: 'string'},
	    {name: 'code',       type: 'string'},
	    {name: 'name',       type: 'string'},
	    {name: 'leaf',       type: 'boolean'}

	  ]
	})

	ZeT.define('retrade.readers.CatItemTree', {
	  type: 'xml', root: 'tree', record: 'node'
	})

	//>: catalogue items as tree


	//<: view of aggregated volumes

	Ext.define('retrade.model.AggrVolumeView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',      type: 'string'},
	    {name: 'date',           type: 'date', dateFormat: 'd.m.Y'},
	    {name: 'dateMonthYear',  type: 'string'},
	    {name: 'volumePositive', type: 'float'},
	    {name: 'volumeNegative', type: 'float'},
	    {name: 'volumeBalance',  type: 'float'}
	  ]
	})

	ZeT.define('retrade.readers.AggrVolumeView', {
	  type: 'xml', root: 'aggr-volumes', record: 'aggr-volume',
	  totalProperty: 'size'
	})

	//>: view of aggregated volumes


	//<: columns of contractors debts

	ZeT.defineDelay('retrade.columns.ContractorsDebts', function()
	{
		return [

		 {
		   text: "Дата", dataIndex: 'date', sortable: false,
		   width: extjsf.ex(12), renderer: Ext.util.Format.dateRenderer('d.m.Y')
		 },

		 {
		   text: "Продано", dataIndex: 'volumePositive', sortable: false,
		   width: extjsf.ex(22), align: 'right',  renderer: retrade.fcurrency
		 },

		 {
		   text: "Куплено", dataIndex: 'volumeNegative', sortable: false,
		   width: extjsf.ex(22), align: 'right',  renderer: retrade.fcurrency
		 },

		 {
		   text: "Баланс", dataIndex: 'volumeBalance', sortable: false,
		   width: extjsf.ex(22), align: 'right',  renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.ContractorsDebtsMonthly', function()
	{
		return [

		 {
		   text: "Месяц", dataIndex: 'dateMonthYear', sortable: false,
		   width: extjsf.ex(12)
		 },

		 {
		   text: "Продано", dataIndex: 'volumePositive', sortable: false,
		   width: extjsf.ex(22), align: 'right',  renderer: retrade.fcurrency
		 },

		 {
		   text: "Куплено", dataIndex: 'volumeNegative', sortable: false,
		   width: extjsf.ex(22), align: 'right',  renderer: retrade.fcurrency
		 },

		 {
		   text: "Баланс", dataIndex: 'volumeBalance', sortable: false,
		   width: extjsf.ex(22), align: 'right',  renderer: retrade.fcurrency
		 }
		];
	})

	//>: columns of contractors debts


	//<: sells sessions & receipts

	Ext.define('retrade.model.SellReceiptView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'index',      type: 'int'},
	    {name: 'code',       type: 'string'},
	    {name: 'time',       type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'income',     type: 'string'},
	    {name: 'payFlag',    type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.SellReceiptView', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№', dataIndex: 'index',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код чека", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(22), flex: 0
		 },

		 {
		   text: "Дата и время", dataIndex: 'time', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Опл.", dataIndex: 'payFlag', sortable: false,
		   width: extjsf.ex(8), flex: 0, renderer: function(v)
		   {
				var c = v && (v.indexOf('C') != -1);
				var b = v && (v.indexOf('B') != -1);

				if(c && b) return 'Н+К';
				if(c)      return 'НАЛ';
				if(b)      return 'КАР';
				return '';
		   }
		 },

		 {
		   text: "Сумма", dataIndex: 'income', sortable: false,
		   width: extjsf.ex(14), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.define('retrade.readers.SellReceiptView', {

	  type: 'xml', root: 'receipts', record: 'receipt',
	  totalProperty: 'receiptsNumber'

	})

	ZeT.defineDelay('retrade.columns.SellsInvoiceView', function()
	{
		return [

		 {
		   text: "Код документа", dataIndex: 'docCode', sortable: false,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: "Тип документа", dataIndex: 'docTypeLo', sortable: false,
		   width: extjsf.ex(24)
		 },

		 {
		   text: "Склад", dataIndex: 'storeName', sortable: false,
		   width: extjsf.ex(24), flex: 1
		 },

		 {
		   text: "Сумма", dataIndex: 'docCost', sortable: false,
		   width: extjsf.ex(12), align: 'right',
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.GoodSellView', function()
	{
		return [

		 {
		   xtype: 'rownumberer', text: '№',
		   width: extjsf.ex(5), resizable: false, hideable: false
		 },

		 {
		   text: "Код", dataIndex: 'goodCode', sortable: true,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'goodName', sortable: true,
		   width: extjsf.ex(20), flex: 1
		 },

		 {
		   text: "Склад", dataIndex: 'storeCode', sortable: false,
		   width: extjsf.ex(14), flex: 0
		 },

		 {
		   text: 'Объём', dataIndex: 'volume', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0
		 },

		 {
		   text: "Изм.", dataIndex: 'measureCode', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Цена", dataIndex: 'cost', sortable: false,
		   width: extjsf.ex(12), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.define('retrade.readers.GoodSellView', {

	  type: 'xml', root: 'good-sells', record: 'good-sell'
	})

	//>: sells sessions & receipts


	//<: accounts & payment ways

	Ext.define('retrade.model.AccountView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',       type: 'string'},
	    {name: 'code',            type: 'string'},
	    {name: 'name',            type: 'string'},
	    {name: 'income',          type: 'string'},
	    {name: 'expense',         type: 'string'},
	    {name: 'balance',         type: 'string'},
	    {name: 'remarks',         type: 'string'},
	    {name: 'contractorKey',   type: 'string'},
	    {name: 'contractorCode',  type: 'string'},
	    {name: 'contractorName',  type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.AccountView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: "Контрагент", dataIndex: 'contractorName', sortable: false,
		   width: extjsf.ex(24), flex: 2, renderer: function(v, meta)
		   {
				v = ZeTS.ises(v)?('/Собственный счёт/'):(v);
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Баланс', dataIndex: 'balance', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }

		];
	})

	ZeT.defineDelay('retrade.columns.AccountViewWithoutFirm', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(22), flex: 1
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Баланс', dataIndex: 'balance', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }

		];
	})

	ZeT.define('retrade.readers.AccountView', {

	  type: 'xml', root: 'accounts', record: 'account',
	  totalProperty: 'accountsNumber'

	})

	Ext.define('retrade.model.PayWayView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',       type: 'string'},
	    {name: 'name',            type: 'string'},
	    {name: 'typeFlag',        type: 'string'},
	    {name: 'income',          type: 'string'},
	    {name: 'expense',         type: 'string'},
	    {name: 'balance',         type: 'string'},
	    {name: 'remarks',         type: 'string'},
	    {name: 'openTime',        type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'closeTime',       type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'contractorKey',   type: 'string'},
	    {name: 'contractorCode',  type: 'string'},
	    {name: 'contractorName',  type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.PayWayView', function()
	{
		return [

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Тип", dataIndex: 'typeFlag', sortable: false,
		   width: extjsf.ex(16), flex: 0, renderer: function(v)
		   {
				if(v == 'I') return 'Только приход';
				if(v == 'E') return 'Только расход';
				if(v == 'E') return 'Только расход';
				return 'Приход-расход';
		   }
		 },

		 {
		   text: "Открыт", dataIndex: 'openTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Закрыт", dataIndex: 'closeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Баланс', dataIndex: 'balance', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }

		];
	})

	ZeT.defineDelay('retrade.columns.PayWayViewWithFirm', function()
	{
		return [

		 {
		   text: "Наименование", dataIndex: 'name', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Контрагент", dataIndex: 'contractorName', sortable: false,
		   width: extjsf.ex(24), flex: 1, renderer: function(v, meta)
		   {
				v = ZeTS.ises(v)?('/Собственный счёт/'):(v);
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Тип", dataIndex: 'typeFlag', sortable: false,
		   width: extjsf.ex(16), flex: 0, hidden: true, renderer: function(v)
		   {
				if(v == 'I') return 'Только приход';
				if(v == 'E') return 'Только расход';
				if(v == 'E') return 'Только расход';
				return 'Приход-расход';
		   }
		 },

		 {
		   text: "Открыт", dataIndex: 'openTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), hidden: true, flex: 0
		 },

		 {
		   text: "Закрыт", dataIndex: 'closeTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), hidden: true, flex: 0
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Баланс', dataIndex: 'balance', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }

		];
	})

	ZeT.define('retrade.readers.PayWayView', {
	  type: 'xml', root: 'pay-ways', record: 'pay-way',
	  totalProperty: 'payWaysNumber'
	})

	ZeT.defineDelay('retrade.columns.PayWayView', function()
	{
		return [

		 {
		   text: "Наименование", dataIndex: 'objectKey', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(pk, meta, rec)
		   {
				//meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 }
		];
	})

	//>: accounts & payment ways


	//<: payments

	Ext.define('retrade.model.PaymentView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',    type: 'string'},
	    {name: 'typeNameLo',   type: 'string'},
	    {name: 'time',         type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'code',         type: 'string'},
	    {name: 'remarks',      type: 'string'},
	    {name: 'income',       type: 'string'},
	    {name: 'expense',      type: 'string'},
	    {name: 'balance',      type: 'string'},


	    //~: pay self attributes

	    {name: 'selfPayKey',                  type: 'string'},
	    {name: 'selfAccountKey',              type: 'string'},
	    {name: 'selfAccountCode',             type: 'string'},
	    {name: 'selfAccountName',             type: 'string'},
	    {name: 'selfWayKey',                  type: 'string'},
	    {name: 'selfWayName',                 type: 'string'},
	    {name: 'selfWayTypeNameLo',           type: 'string'},
	    {name: 'selfWayBankId',               type: 'string'},
	    {name: 'selfWayBankName',             type: 'string'},
	    {name: 'selfWayBankAccount',          type: 'string'},
	    {name: 'selfWayBankRemitteeAccount',  type: 'string'},
	    {name: 'selfWayBankRemitteeName',     type: 'string'},


	     //~: pay firm attributes

	    {name: 'firmPayKey',                  type: 'string'},
	    {name: 'firmAccountKey',              type: 'string'},
	    {name: 'firmAccountCode',             type: 'string'},
	    {name: 'firmAccountName',             type: 'string'},
	    {name: 'firmWayKey',                  type: 'string'},
	    {name: 'firmWayName',                 type: 'string'},
	    {name: 'firmWayTypeNameLo',           type: 'string'},
	    {name: 'firmWayBankId',               type: 'string'},
	    {name: 'firmWayBankName',             type: 'string'},
	    {name: 'firmWayBankAccount',          type: 'string'},
	    {name: 'firmWayBankRemitteeAccount',  type: 'string'},
	    {name: 'firmWayBankRemitteeName',     type: 'string'},
	    {name: 'firmKey',                     type: 'string'},
	    {name: 'firmCode',                    type: 'string'},
	    {name: 'firmName',                    type: 'string'},


	    //~: payment order

	    {name: 'orderKey',           type: 'string'},
	    {name: 'orderCode',          type: 'string'},
	    {name: 'orderTime',          type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'orderRemarks',       type: 'string'},
	    {name: 'orderTotalIncome',   type: 'string'},
	    {name: 'orderTotalExpense',  type: 'string'},
	    {name: 'orderActualIncome',  type: 'string'},
	    {name: 'orderActualExpense', type: 'string'},
	    {name: 'orderBalance',       type: 'string'},


	    //~: sells attributes

	    {name: 'sellsDeskKey',          type: 'string'},
	    {name: 'sellsDeskCode',         type: 'string'},
	    {name: 'sellsDeskName',         type: 'string'},
	    {name: 'sellsDeskRemarks',      type: 'string'},
	    {name: 'sellsPayDeskKey',       type: 'string'},
	    {name: 'sellsPayDeskName',      type: 'string'},
	    {name: 'sellsPayDeskRemarks',   type: 'string'},
	    {name: 'sellsPayDeskOpenDate',  type: 'date', dateFormat: 'd.m.Y'},
	    {name: 'sellsPayDeskCloseDate', type: 'date', dateFormat: 'd.m.Y'}
	  ]
	})

	ZeT.define('retrade.readers.PaymentView', {
	  type: 'xml', root: 'payments', record: 'payment',
	  totalProperty: 'paymentsNumber'
	})

	ZeT.defineDelay('retrade.columns.PaymentViewGen', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(16), flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Дата и время", dataIndex: 'time', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Тип", dataIndex: 'typeNameLo', sortable: false,
		   width: extjsf.ex(18), flex: 1
		 },

		 {
		   text: "Уч. счёт", dataIndex: 'selfAccountCode', sortable: false,
		   width: extjsf.ex(18), flex: 0, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  '№', v, ', ', rec.get('selfAccountName')
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: "Пл. счёт", dataIndex: 'selfWayName', sortable: false,
		   width: extjsf.ex(18), flex: 0, hidden: true, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Ордер", dataIndex: 'orderCode', sortable: false,
		   width: extjsf.ex(18), flex: 1
		 },

		 {
		   text: "Дата ордера", dataIndex: 'orderTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y'),
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})


	ZeT.defineDelay('retrade.columns.PaymentViewCon', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(16), flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Дата и время", dataIndex: 'time', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Тип", dataIndex: 'typeNameLo', sortable: false,
		   width: extjsf.ex(18), hidden: true, flex: 1
		 },

		 {
		   text: "Уч. счёт", dataIndex: 'selfAccountCode', sortable: false,
		   width: extjsf.ex(18), flex: 0, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  '№', v, ', ', rec.get('selfAccountName')
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: "Пл. счёт", dataIndex: 'selfWayName', sortable: false,
		   width: extjsf.ex(18), flex: 0, hidden: true, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Ордер", dataIndex: 'orderCode', sortable: false,
		   width: extjsf.ex(18), flex: 0
		 },

		 {
		   text: "Дата ордера", dataIndex: 'orderTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y'),
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Контрагент", dataIndex: 'firmName', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
					'№', rec.get('firmCode'), ', ', v
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	ZeT.defineDelay('retrade.columns.PaymentViewConBank', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: false,
		   width: extjsf.ex(16), flex: 1, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Дата и время", dataIndex: 'time', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16), flex: 0
		 },

		 {
		   text: "Тип", dataIndex: 'typeNameLo', sortable: false,
		   width: extjsf.ex(18), hidden: true, flex: 1
		 },

		 {
		   text: "Уч. счёт", dataIndex: 'selfAccountCode', sortable: false,
		   width: extjsf.ex(18), flex: 0, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  '№', v, ', ', rec.get('selfAccountName')
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: "Пл. счёт", dataIndex: 'selfWayName', sortable: false,
		   width: extjsf.ex(18), flex: 0, hidden: true, renderer: function(v, meta)
		   {
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(v) + '"';
				return v;
		   }
		 },

		 {
		   text: "Ордер", dataIndex: 'orderCode', sortable: false,
		   width: extjsf.ex(18), flex: 0
		 },

		 {
		   text: "Дата ордера", dataIndex: 'orderTime', sortable: false,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y'),
		   width: extjsf.ex(12), flex: 0
		 },

		 {
		   text: "Контрагент", dataIndex: 'firmName', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  '№', rec.get('firmCode'), ', ', v
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: "Счёт контрагента", dataIndex: 'firmWayBankRemitteeAccount', sortable: false,
		   width: extjsf.ex(22), flex: 1, renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  v, ', получатель: ', rec.get('firmWayBankRemitteeName'), '; в ',
				  rec.get('firmWayBankName'), 'БИК ', rec.get('firmWayBankId'),
				  ', кор. счёт ', rec.get('firmWayBankAccount')
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: "Банк контрагента", dataIndex: 'firmWayBankName', sortable: false,
		   width: extjsf.ex(22), flex: 1, hidden: true,
		   renderer: function(v, meta, rec)
		   {
				meta.tdAttr = ZeTS.cat('title="', Ext.String.htmlEncode(ZeTS.cat(
				  'БИК ', rec.get('firmWayBankId'), ', ', v,
				  ', кор. счёт ', rec.get('firmWayBankAccount')
				)), '"' );

				return v;
		   }
		 },

		 {
		   text: 'Приход', dataIndex: 'income', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 },

		 {
		   text: 'Расход', dataIndex: 'expense', sortable: false,
		   width: extjsf.ex(18), align: 'right', flex: 0,
		   renderer: retrade.fcurrency
		 }
		];
	})

	//>: payments


	//<: data sources

	Ext.define('retrade.model.DataSourceView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'did',

	  fields: [

	    {name: 'did',     type: 'string'},
	    {name: 'name',    type: 'string'},
	    {name: 'nameLo',  type: 'string'},
	    {name: 'descr',   type: 'string'},
	    {name: 'descrLo', type: 'string'},
	    {name: 'system',  type: 'boolean'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.DataSourceView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'did', sortable: true,
		   width: extjsf.ex(16), flex: 1
		 },

		 {
		   text: "Тип", dataIndex: 'system', sortable: false,
		   width: extjsf.ex(5), flex: 0, renderer: function(v, meta)
		   {
				var s = !!v; var p = true;

				var t; if(s & p) t = 'Системный, программный';
				else t = 'Пользовательский, скриптовый';

				meta.tdAttr = 'title="' + t + '"';
				return (s & p)?('СП'):('ПС');
		   }
		 },

		 {
		   text: "Наименование", dataIndex: 'nameLo', sortable: true,
		   width: extjsf.ex(22), flex: 2, renderer: function(v, meta, rec)
		   {
				var d = rec.get('descrLo');
				if(ZeTS.ises(d)) d = rec.get('descr');

				if(!ZeTS.ises(d)) meta.tdAttr = 'title="' +
					Ext.String.htmlEncode(d.replace(/\s+/g, ' ')) + '"';

				return v;
		   }
		 }
		];
	})

	ZeT.define('retrade.readers.DataSourceView', {
	  type: 'xml', root: 'data-sources', record: 'data-source'
	})

	//~: data sources: invoice: Adapted Entities Selected
	ZeT.define('retrade.readers.AdaptedEntitiesSelected', {
	  type: 'xml', root: 'entities', record: 'entity',
	  totalProperty: 'count'
	})

	//>: data sources


	//<: report templates

	Ext.define('retrade.model.ReportTemplateView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',  type: 'string'},
	    {name: 'code',       type: 'string'},
	    {name: 'name',       type: 'string'},
	    {name: 'did',        type: 'string'},
	    {name: 'system',     type: 'boolean'},
	    {name: 'remarks',    type: 'string'},
	    {name: 'hasUI',      type: 'boolean'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.ReportTemplateView', function()
	{
		return [

		 {
		   text: "Код", dataIndex: 'code', sortable: true,
		   width: extjsf.ex(12), flex: 1
		 },


		 {
		   text: 'Сист.', dataIndex: 'system', sortable: false,
		   width: extjsf.ex(6), align: 'center', renderer: function(v, meta)
		   {
				var t = (v)?('Системный шаблон'):('Пользовательский шаблон');
				meta.tdAttr = 'title="' + Ext.String.htmlEncode(t) + '"';
				return (v)?('да'):('нет');
		   }
		 },

		 {
		   text: 'Источник данных', dataIndex: 'did', sortable: true,
		   width: extjsf.ex(18), flex: 1
		 },

		 {
		   text: "Наименование", dataIndex: 'name', sortable: true,
		   width: extjsf.ex(28), flex: 1.5, renderer: function(v, meta, rec)
		   {
				if(!ZeTS.ises(rec.get('remarks')))
					meta.tdAttr = 'title="' +
					  Ext.String.htmlEncode(rec.get('remarks').replace(/\s+/g, ' ')) + '"';
				return v;
		   }
		 }
		];
	})

	ZeT.define('retrade.readers.ReportTemplateView', {

	  type: 'xml', root: 'report-templates', record: 'report-template',
	  totalProperty: 'templatesNumber'

	})


	Ext.define('retrade.model.ReportRequestView', {
	  extend: 'Ext.data.Model',

	  idProperty: 'objectKey',

	  fields: [

	    {name: 'objectKey',    type: 'string'},
	    {name: 'time',         type: 'date', dateFormat: 'd.m.Y H:i'},
	    {name: 'format',       type: 'string'},
	    {name: 'loaded',       type: 'boolean'},
	    {name: 'ready',        type: 'boolean'},
	    {name: 'template',     type: 'string'},
	    {name: 'templateCode', type: 'string'},
	    {name: 'templateName', type: 'string'},
	    {name: 'templateDid',  type: 'string'}
	  ]
	})

	ZeT.defineDelay('retrade.columns.ReportRequestView', function()
	{
		return [

		 {
		   text: 'Дата и время', dataIndex: 'time', sortable: true,
		   renderer: Ext.util.Format.dateRenderer('d.m.Y H:i'),
		   width: extjsf.ex(16)
		 },

		 {
		   text: 'Код шаблона', dataIndex: 'templateCode', sortable: true,
		   width: extjsf.ex(12), flex: 1
		 },

		 {
		   text: 'Наименование шаблона', dataIndex: 'templateName', sortable: true,
		   width: extjsf.ex(28), flex: 1.5
		 },

		 {
		   text: 'Источник данных', dataIndex: 'templateDid', sortable: true,
		   width: extjsf.ex(18), flex: 1
		 },

		 {
		   text: 'Формат', dataIndex: 'format', sortable: false,
		   width: extjsf.ex(8), flex: 0, renderer: function(v)
		   {
				switch(v) {
					case 'XLS'   : return 'Excel';
					case 'ERROR' : return 'Ошибка';
					case 'XML'   : return 'XML';
					default      : return 'PDF';
				}
		   }
		 }
		];
	})

	ZeT.define('retrade.readers.ReportRequestView', {

	  type: 'xml', root: 'report-requests', record: 'report-request',
	  totalProperty: 'requestsNumber'

	})

	//>: report templates
})


var retrade = ZeT.define('retrade',
{
	//=    Forms Validation      =//

	rdecimal         : /[\d\.]/,

	rinteger         : /[\d]/,

	vdecimal         : function(o, v)
	{
		if(!ZeT.iss(v)) { v = o; o = null; }
		v = retrade._vdecimal(v); o = o || {};

		if(ZeT.isu(v)) return !!o.blank || 'Поле должно быть заполнено!';
		if(!v) return 'Требуется положительное десятичное значение!';
		return true;
	},

	vcurrency        : function(o, v)
	{
		if(!ZeT.iss(v)) { v = o; o = null; }
		v = retrade._vdecimal(v); o = o || {};

		if(ZeT.isu(v)) return !!o.blank || 'Поле должно быть заполнено!';

		if(v && !v.match(/^\d+(\.\d{1,2})?$/))
			return "Точность цены должна быть до сотых долей!";

		if(!v) return 'Требуется десятичное денежное значение!';
		return true;
	},

	vvcurrency       : function(o, v)
	{
		if(!ZeT.iss(v)) { v = o; o = null; }
		v = retrade._vdecimal(v); o = o || {};

		if(ZeT.isu(v)) return !!o.blank || 'Поле должно быть заполнено!';

		if(v && !v.match(/^\d+(\.\d{1,5})?$/))
			return "Точность стоимости обёма должна быть до пяти знаков после запятой!";

		if(!v) return 'Требуется десятичное денежное значение стоимости обёма!';
		return true;
	},

	vvolume          : function(o, v)
	{
		if(!ZeT.iss(v)) { v = o; o = null; }
		v = retrade._vdecimal(v); o = o || {};

		if(ZeT.isu(v)) return !!o.blank || 'Поле должно быть заполнено!';
		if(!v) return 'Требуется десятичное значение объёма!';
		if(!v.match(/^\d+(\.\d{1,3})?$/))
			return 'Объём задаётся до тысячных долей!';
		return true;
	},

	vivolume         : function(o, v)
	{
		if(!ZeT.iss(v)) { v = o; o = null; }
		v = retrade._vinteger(v); o = o || {};

		if(ZeT.isu(v)) return !!o.blank || 'Поле должно быть заполнено!';
		if(!v) return 'Требуется целое значение объёма!';
		return true;
	},

	_vdecimal        : function(v)
	{
		if(ZeT.iss(v)) v = v.replace(' ', '');

		if(ZeTS.ises(v)) return undefined;
		return v.match(/^\d+(\.\d+)?$/)?(v):(null);
	},

	_vinteger        : function(v)
	{
		if(ZeT.iss(v)) v = v.replace(' ', '');

		if(ZeTS.ises(v)) return undefined;
		return v.match(/^\d+$/)?(v):(null);
	},


	//=       Formatters         =//

	fcurrency        : function(v, o)
	{
		if(ZeT.isu(v) || (v === null)) return v;
		if(!ZeT.iss(v)) v = '' + v;

		var d = v.lastIndexOf(',');
		if(d != -1) v[d] = '.';

		var s = '';
		d = v.lastIndexOf('.');

		if(o && ZeT.isn(o.point_align_val))
		{
			var l = (d != -1)?(v.length - d - 1):(0);
			while(l < o.point_align_val) { if(l == 0) v += '.'; v += '0'; l++; }
		}

		if(d != -1) s = v.substring(d);
		if(d != -1) v = v.substring(0, d);

		var p = '', x = (o && ZeT.iss(o.sep))?(o.sep):
		  "<span class = 'retrade-currency-sep'></span>";

		while(v.length > 2)
		{
			p = v.substring(v.length - 3).concat((p.length?(x):('')), p);
			v = v.substring(0, v.length - 3);
		}

		return v.concat((v.length && p.length && v != '-')?(x):(''), p, s);
	},


	/**
	 * Iterates over the DOM tree performing currency formatting on
	 * the nodes marked with 'retrade-format-to-currency' class.
	 * This class is removed after the formatting.
	 *
	 * Set opts.treetit = 1, opts.point_align = true to collect
	 * all the numbers on the first phase and on to format them
	 * adding zeros after to the fraction part till the longest
	 * one within the numbers. This allows all the digits to
	 * be aligned strictly in the same column.
	 */
	TreetFCurrency   : new ZeT.Layout.Treeter(function(node, opts)
	{
		if(!ZeTD.hasclass(node, 'retrade-format-to-currency')) return node;

		if(opts.treetit == 0)
		{
			ZeTD.classes(node, '-retrade-format-to-currency')
			node.innerHTML = retrade.fcurrency(ZeTS.trim(node.innerHTML), opts);

			return node;
		}

		if(opts['point_align'] === true)
		{
			if(!opts.point_align_val) opts.point_align_val = 0;

			var s = ZeTS.trim(node.innerHTML);
			var d = s.lastIndexOf(',');
			if(d != -1) s[d] = '.';

			if((d = s.lastIndexOf('.')) != -1)
			{
				var l = s.length - d - 1;
				if(l > opts.point_align_val) opts.point_align_val = l;
			}

			ZeT.log('l = ', l)
		}

		return node;
	})
})
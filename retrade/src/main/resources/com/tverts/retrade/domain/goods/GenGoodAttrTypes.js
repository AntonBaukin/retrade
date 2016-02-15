var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')

function genGoodTypes(ctx, gen)
{
	ZeT.each(getGoodTypes(), function(g)
	{
		//!: call back to the generator
		gen.takeGoodType(ctx, assignGoodAttr(g))
	})
}

/**
 * Takes JS-object of Good Unit attribute type,
 * creates (if required), fills (except the Domain),
 * and returns resulting AttrType instance.
 */
function assignGoodAttr(g, /* optional AttrType */ type)
{
	var GoodAttr = Java.type('com.tverts.api.retrade.goods.GoodAttr')
	var AttrType = Java.type('com.tverts.endure.core.AttrType')
	var Goods    = Java.type('com.tverts.retrade.domain.goods.Goods')


	//<: initialize good attribute

	var attr = new GoodAttr()

	//=: name
	attr.setName(ZeT.asserts(g.name))

	//=: name local
	if(!ZeTS.ises(g.nameLo))
		attr.setNameLo(g.nameLo)

	//=: is-system
	attr.setSystem(!!g.system)

	//=: is-array
	attr.setArray(!!g.array)

	//=: is-shared
	attr.setShared(!!g.shared)

	//~: ox-object
	attr.setObject(ZeT.o2s(g))

	//>: initialize good attribute


	//<: initialize attribute type

	if(!type) type = new AttrType()
	ZeT.assert(type instanceof AttrType)

	//=: type of attribute
	type.setAttrType(Goods.typeGoodAttr())

	//=: name
	type.setName(attr.getName())

	//=: local name
	type.setNameLo(attr.getNameLo())

	//=: is-system
	type.setSystem(attr.isSystem())

	//=: is-array
	type.setArray(attr.isArray())

	//=: is-shared
	type.setShared(attr.isShared())

	//=: ox-object
	type.setOx(attr)

	//>: initialize attribute type

	return type
}

/**
 * Warning: update Goods class when changing
 *   names of the system attributes!
 */
function getGoodTypes()
{
	var result = []

	//~: main attributes
	ZeT.each(getGoodTypesMain(), function(g)
	{
		g.group = { name: 'Main', nameLo: 'Основные' }
		result.push(g)
	})

	//~: cooking attributes
	ZeT.each(getGoodTypesCooking(), function(g)
	{
		g.group = { name: 'Cooking', nameLo: 'Изготовление' }
		result.push(g)
	})

	//~: certification attributes
	ZeT.each(getGoodTypesCertification(), function(g)
	{
		g.group = { name: 'Certification', nameLo: 'Сертификация' }
		result.push(g)
	})

	//~: alcohol attributes
	ZeT.each(getGoodTypesAlcohol(), function(g)
	{
		g.group = { name: 'Alcohol', nameLo: 'Алкоголь' }
		result.push(g)
	})

	return result
}

function getGoodTypesMain()
{
	return [

		{
			/**
			 * System attributes may not be edited or removed.
			 */
			system:  true,

			/**
			 * Tells that this attribute is shared with sub-goods.
			 */
			shared:  true,

			name:    'Group',
			nameLo:  'Группа',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Full Name',
			nameLo:  'Полное наименование',
			type:    'string'
		},

		{
			system:  true,
			name:    'Vendor Code',
			nameLo:  'Артикул',
			type:    'string',

			/**
			 * Tells that attribute values form an array
			 * (list) that has distinct db-record for each.
			 */
			array:   true
		},

		{
			system:  true,
			name:    'Barсode',
			nameLo:  'Баркод',
			type:    'string',
			array:   true
		},

		{
			system:  true,
			name:    'Net Weight',
			nameLo:  'Вес нетто',

			/**
			 * Stored as decimal, tells to validate as volume.
			 */
			type:    'volume'
		},

		{
			system:  true,
			name:    'Gross Weight',
			nameLo:  'Вес брутто',
			type:    'volume'
		},

		{
			system:  true,
			shared:  true,
			name:    'VAT',
			nameLo:  'НДС',

			/**
			 * Note that decimal values are alsways
			 * stored in JSON as strings, not numbers!
			 */
			type:    'decimal',

			/**
			 * The enumeration of the values from drop-down list.
			 * The optional text is displayed instead of the value.
			 */
			values:  [

				{ value: '18.0', text: '18%' },
				{ value: '10.0', text: '10%' },
				{ value: '0.0',  text: '0%' }
			]
		},

		{
			system:  true,
			shared:  true,
			name:    'Weighted',
			nameLo:  'Весовой товар',
			type:    'string',
			values:  [

				{ value: 'true', text: 'Да' },
				{ value: 'false', text: 'Нет' }
			]
		},

		{
			system:  true,
			name:    'Rest Volume',
			nameLo:  'Неснижаемый остаток',
			type:    'volume'
		}
	]
}

function getGoodTypesCooking()
{
	return [

		/**
		 * Параметры обработки
		 */
		{
			system:  true,
			name:    'Cold Cooking',
			nameLo:  'Холодная обработка',
			type:    'decimal'
		},

		{
			system:  true,
			name:    'Hot Cooking',
			nameLo:  'Горячая обработка',
			type:    'decimal'
		},

		/**
		 * Энергетическая ценность
		 */
		{
			system:  true,
			shared:  true,
			name:    'Proteins',
			nameLo:  'Белки',
			type:    'decimal'
		},

		{
			system:  true,
			shared:  true,
			name:    'Fats',
			nameLo:  'Жиры',
			type:    'decimal'
		},

		{
			system:  true,
			shared:  true,
			name:    'Carbohydrates',
			nameLo:  'Углеводы',
			type:    'decimal'
		},

		{
			system:  true,
			shared:  true,
			name:    'Energy Value',
			nameLo:  'Энергетическая ценность',
			type:    'decimal'
		}
	]
}

function getGoodTypesCertification()
{
	return [

		/**
		 * Сертификация и сроки хранения
		 */
		{
			system:  true,
			shared:  true,
			name:    'Quality Certificate',
			nameLo:  'Номер сертификата качества',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Quality Certificate Start',
			nameLo:  'Начало действия сертификата качества',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Quality Certificate End',
			nameLo:  'Окончание действия сертификата качества',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Certificate Authority',
			nameLo:  'Орган сертификации',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Compliance with Requirements',
			nameLo:  'Соответствие требованиям / ТУ',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Storage Days',
			nameLo:  'Срок хранения',
			type:    'integer'
		}
	]
}

function getGoodTypesAlcohol()
{
	return [

		{
			system:  true,
			shared:  true,
			name:    'Alcohol Content',
			nameLo:  'Содержание спирта %',
			type:    'integer'
		},

		{
			system:  true,
			shared:  true,
			name:    'Producer / Importer',
			nameLo:  'Производитель / Импортер',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'TIN Producer / Importer',
			nameLo:  'ИНН Производителя / Импортера',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
			name:    'Code Type of Alcoholic Beverages',
			nameLo:  'Код вида алкогольной продукции',
			type:    'string'
		}
	]
}

ZeT.extend({}, //<-- this resulting module object
{
	assignGoodAttr : assignGoodAttr
})
var ZeT = JsX.once('zet/app.js')

function genTestGoods(ctx, gen)
{
	var Good = Java.type('com.tverts.api.retrade.goods.Good')
	var Calc = Java.type('com.tverts.api.retrade.goods.Calc')

	ZeT.each(getTestGoods(), function(g)
	{
		//~: extract the calculation
		var c = g.calc
		if(c) delete g.calc

		//~: convert good object
		var good = ZeT.s2jo(Good, ZeT.o2s(g))

		//~: convert calc object
		var calc = (c)?ZeT.s2jo(Calc, ZeT.o2s(c)):(null)

		//~: report it to the generator
		gen.takeGood(ctx, good, calc)
	})
}

function readTestPrices(c2p)
{
	ZeT.each(getTestGoods(), function(g)
	{
		if(ZeT.iss(g.cost))
			c2p.put(g.code, ZeT.jdecimal(g.cost))
	})
}

function getTestGoods()
{
	return [

		{
			cost: '45.50',
			code: '901',
			name: 'яблоки (Молдова)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '64.20',
			code: '902',
			name: 'яблоки (семеринка)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '58.30',
			code: '903',
			name: 'яблоки (глостер, Польша)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '52',
			code: '904',
			name: 'апельсины (Египет)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '48',
			code: '905',
			name: 'апельсины (Морокко)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '145.30',
			code: '906',
			name: 'виноград (кардинал)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '132.40',
			code: '907',
			name: 'виноград (дамские пальчики)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '110.50',
			code: '908',
			name: 'виноград (киш-миш зеленый)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '125.60',
			code: '909',
			name: 'виноград (киш-миш черный)',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '112.55',
			code: '910',
			name: 'ананасы',
			xmeasure: 'шт',
			group: 'фрукты'
		},

		{
			cost: '85.65',
			code: '911',
			name: 'киви',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '34.20',
			code: '912',
			name: 'бананы',
			xmeasure: 'кг',
			group: 'фрукты'
		},

		{
			cost: '399.50',
			code: '913',
			name: 'клубника',
			xmeasure: 'кг',
			group: 'ягоды'
		},

		{
			cost: '28.60',
			code: '914',
			name: 'морковь',
			xmeasure: 'кг',
			group: 'овощи'
		},

		{
			cost: '18.30',
			code: '915',
			name: 'картофель',
			xmeasure: 'кг',
			group: 'овощи'
		},

		{
			cost: '26.20',
			code: '916',
			name: 'капуста',
			xmeasure: 'кг',
			group: 'овощи'
		},

		{
			cost: '36.50',
			code: '917',
			name: 'свекла',
			xmeasure: 'кг',
			group: 'овощи'
		},

		{
			cost: '16.58',
			code: '918',
			name: 'петрушка',
			xmeasure: '100 гр',
			group: 'зелень'
		},

		{
			cost: '16.58',
			code: '919',
			name: 'укроп',
			xmeasure: '100 гр',
			group: 'зелень'
		},

		{
			cost: '12.45',
			code: '920',
			name: 'лук зелёный',
			xmeasure: '100 гр',
			group: 'зелень'
		},

		{
			cost: '22.20',
			code: '921',
			name: 'лук репчатый',
			xmeasure: 'кг',
			group: 'зелень'
		},

		{
			cost: '26.58',
			code: '922',
			name: 'чеснок',
			xmeasure: '100 гр',
			group: 'зелень'
		},

		{
			cost: '74.85',
			code: '923',
			name: 'редис (красный)',
			xmeasure: 'кг',
			group: 'овощи'
		},

		{
			cost: '38.40',
			code: '924',
			name: 'репа зеленая',
			xmeasure: 'кг',
			group: 'овощи'
		},

		{
			cost: '89.90',
			code: '925',
			name: 'перец (Краснодар)',
			xmeasure: 'кг',
			group: 'перец'
		},

		{
			cost: '115.80',
			code: '926',
			name: 'перец чили',
			xmeasure: 'кг',
			group: 'перец'
		},

		{
			cost: '135.90',
			code: '927',
			name: 'перец красный',
			xmeasure: 'кг',
			group: 'перец'
		},

		{
			cost: '9.50',
			code: '928',
			name: 'хлеб ржаной',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '24.60',
			code: '929',
			name: 'батон нарезной',
			xmeasure: 'шт',
			group: 'хлеб'
		},

		{
			cost: '14.60',
			code: '930',
			name: 'хлеб отрубной',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '16.84',
			code: '931',
			name: 'хлеб бородинский',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '38.16',
			code: '932',
			name: 'баранки',
			xmeasure: 'уп',
			group: 'хлеб'
		},

		{
			cost: '24.15',
			code: '933',
			name: 'ватрушка с творогом',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '18.10',
			code: '934',
			name: 'рогалик',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '23.10',
			code: '935',
			name: 'круассан с шоколадом',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '26.80',
			code: '936',
			name: 'круассан с повидлом',
			xmeasure: '100 гр',
			group: 'хлеб'
		},

		{
			cost: '457.50',
			code: '801',
			name: 'говядина',
			xmeasure: 'кг',
			group: 'мясо'
		},

		{
			cost: '368.75',
			code: '802',
			name: 'свинина (отбивная)',
			xmeasure: 'кг',
			group: 'мясо'
		},

		{
			cost: '135.95',
			code: '803',
			name: 'курица (тушка)',
			xmeasure: 'кг',
			group: 'мясо'
		},

		{
			cost: '175.25',
			code: '804',
			name: 'курица (филе)',
			xmeasure: 'кг',
			group: 'мясо'
		},

		{
			cost: '145.45',
			code: '805',
			name: 'курица (ножки)',
			xmeasure: 'кг',
			group: 'мясо'
		},

		{
			code: '806',
			name: 'курица (суповая основа)',
			xmeasure: 'кг',
			group: 'мясо'
		},

		{
			cost: '195.54',
			code: '807',
			name: 'фарш мясной',
			xmeasure: 'кг',
			group: 'мясо-продукт',

			calc: {
				'semi-ready': true,

				parts: [
					{xgood: '801', volume: '2.0'},
					{xgood: '802', volume: '0.550'},
					{xgood: '928', volume: '0.200'},
					{xgood: '922', volume: '0.050'}
				]
			}
		},

		{
			cost: '215.30',
			code: '811',
			name: 'фрикадельки',
			xmeasure: 'кг',
			group: 'мясо-продукт',

			calc: {
				'semi-ready': true,
				'xsuper-good': '807',
				'sub-code': 'фр',
				'sub-volume': '1.0'
			}
		},

		{
			code: '812',
			name: 'куриный бульон (основа)',
			xmeasure: 'л',
			group: 'мясо-продукт',

			calc: {
				parts: [
					{xgood: '919', volume: '0.200'},
					{xgood: '921', volume: '1.500'},
					{xgood: '922', volume: '0.100'},
					{xgood: '806', volume: '0.350'}
				]
			}
		},

		{
			code: '813',
			name: 'говядина отварная',
			xmeasure: 'кг',
			group: 'мясо-продукт',

			calc: {
				'xsuper-good': '801',
				'sub-code': 'отв',
				'sub-volume': '1.0'
			}
		},

		{
			cost: '28.80',
			code: '821',
			name: 'суп с фрикадельками',
			xmeasure: '100 мл',
			group: 'супы',

			calc: {
				parts: [
					{xgood: '811', volume: '0.020'},
					{xgood: '812', volume: '0.060'},
					{xgood: '914', volume: '0.005'},
					{xgood: '915', volume: '0.015'}
				]
			}
		},

		{
			cost: '34.36',
			code: '822',
			name: 'борщ',
			xmeasure: '100 мл',
			group: 'супы',

			calc: {
				parts: [
					{xgood: '813', volume: '0.010'},
					{xgood: '812', volume: '0.050'},
					{xgood: '915', volume: '0.020'},
					{xgood: '916', volume: '0.020'},
					{xgood: '920', volume: '0.20'},
					{xgood: '922', volume: '0.10'}
				]
			}
		},

		{
			cost: '55.00',
			code: '701',
			name: 'кока-кола (розлив)',
			xmeasure: 'л',
			group: 'б/а напитки'
		},

		{
			cost: '45.00',
			code: '702',
			name: 'кока-кола (0.5 стакан)',
			xmeasure: 'ст 0.5',
			group: 'б/а напитки',

			calc: {
				'xsuper-good': '701',
				'sub-code': '0.5',
				'sub-volume': '0.5'
			}
		},

		{
			cost: '39.00',
			code: '703',
			name: 'кока-кола (0.33 лёд)',
			xmeasure: 'л',
			group: 'б/а напитки',

			calc: {
				'xsuper-good': '701',
				'sub-code': '0.33 лёд',
				'sub-volume': '0.33'
			}
		}
	]
}
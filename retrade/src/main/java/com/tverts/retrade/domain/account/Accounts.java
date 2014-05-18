package com.tverts.retrade.domain.account;

/* standard Java classes */

import java.util.Random;

/* com.tverts: generation */

import com.tverts.genesis.GenUtils;

/* com.tverts: endure (core) */

import com.tverts.endure.UnityType;
import com.tverts.endure.UnityTypes;

/* com.tverts: aggregation */

import com.tverts.endure.aggr.AggrValue;

/* com.tverts: support */

import com.tverts.support.EX;
import com.tverts.support.SU;


/**
 * Constants related to the {@link Account}s.
 *
 * @author anton.baukin@gmail.com
 */
public class Accounts
{
	/* account types */

	/**
	 * Account owned by the Domain (it is registered in).
	 */
	public static final String TYPE_ACCOUNT_OWN  =
	  "ReTrade: Account: Own";

	/**
	 * Account owned by the Contractor registered
	 * in the same Domain.
	 */
	public static final String TYPE_ACCOUNT_FIRM =
	  "ReTrade: Account: Contractor";

	public static final String TYPE_PAYWAY_BANK  =
	  "ReTrade: Pay Way: Bank Account";

	public static final String TYPE_PAYWAY_CASH  =
	  "ReTrade: Pay Way: Cash";

	public static final String TYPE_PAYIT_SELF   =
	  "ReTrade: Account: Self Pay It";

	public static final String TYPE_PAYIT_FIRM   =
	  "ReTrade: Account: Firm Pay It";


	/* aggregated values types */

	/**
	 * Volume aggregated type (with calculations)
	 * of the account debit-credit balance.
	 *
	 * To reduce the mess with debit-credit meaning
	 * of active and passive accounts, positive value
	 * is always the amount of money payed to the
	 * Domain, and negative is the amount payed by the
	 * Domain (to some Contractor firm).
	 *
	 * Hence, for active accounts positive value is
	 * the debit (income), and negative is the expense.
	 */
	public static final String AGGRVAL_PAYIT_BALANCE =
	  "ReTrade: Aggr Value: Pay It Balance";


	public static UnityType aggrTypePayItBalance()
	{
		return UnityTypes.unityType(
		  AggrValue.class, AGGRVAL_PAYIT_BALANCE);
	}


	/* account check calculations */

	/**
	 * Calculates the control character (digit) in 8 (zero-based)
	 * position of the bank account number with the given ID of the
	 * banking organization (IDB, with zero-based indexing).
	 *
	 * If IDB ends with '000' the account is opened in Central Bank
	 * (correspondent account), and control xxx is '0' + IDB[4-5].
	 * Control xxx for bank account is IDB[6-8].
	 */
	public static char   calcAccountCheck(String bankId, CharSequence account)
	{
		if((bankId.length() != 9) || (account.length() != 20))
			illegalAccount(bankId, account);

		final String W = "71371371371371371371";
		int          w = 0;

		String       xxx = bankId.substring(6);
		if("000".equals(xxx) || SU.eq("301", account, 0, 3))
			xxx = "0" + bankId.substring(4, 6);

		//~: calculate weights of the bank key
		for(int i = 0;(i < 3);i++)
		{
			int x = SU.digit(xxx, i);
			if(x == -1) illegalAccount(bankId, account);

			w += (SU.digit(W, i) * x) % 10;
		}

		//~: calculate weights of the account
		for(int i = 0;(i < 20);i++)
		{
			//?: {the position of check digit itself} skip it
			if(i == 8) continue;

			int x = SU.digit(account, i);

			//?: {currency clearance character}
			if((x == -1) && (i == 5))
				x = "АВСЕНКМРТХ".indexOf(account.charAt(i));
			if(x == -1) illegalAccount(bankId, account);

			w += (SU.digit(W, i) * x) % 10;
		}

		return SU.digit(((w % 10) * 3) % 10);
	}

	public static void   checkAccount(String bankId, CharSequence account)
	{
		char x = calcAccountCheck(bankId, account);

		if(account.charAt(8) != x)
			illegalAccount(bankId, account);
	}

	/**
	 * Generates account number within the bank.
	 * The structure is (without spaces):
	 *
	 *       ATYPE CUR X DEPT NNNNNNN
	 *
	 *  ATYPE   the account type 3 (primary) + 2 (secondary);
	 *  CUR     currency code (810 roubles);
	 *  DEPT    the bank department (random);
	 *  NNNNNNN internal number (random);
	 *  X       the control number.
	 *
	 */
	public static String genAccount(Random rnd, String bankId, String atype, String cur)
	{
		if(atype.length() != 5) throw EX.state();
		if(cur.length()   != 3) throw EX.state();

		StringBuilder s = new StringBuilder(20).
		  append(atype).append(cur).append('X').
		  append(GenUtils.number(rnd, 11));

		s.setCharAt(8, calcAccountCheck(bankId, s));
		return s.toString();
	}

	public static String genAccount(Random rnd, String bankId, String atype)
	{
		return genAccount(rnd, bankId, atype, "810");
	}

	public static String genAccount(Random rnd, String bankId)
	{
		return genAccount(rnd, bankId, "40702", "810");
	}

	private static final int[] TAX_ID_RUS_WEIGHTS =
	  {3,7,2,4,10,3,5,9,4,6,8,0};

	/**
	 * Calculates the control check digit(s) for the
	 * Tax ID given. 10-length IDs has one digit at the
	 * end, 12-length has two at the end.
	 *
	 * The digits are placed to the proper positions
	 * of the buffer given.
	 *
	 * Returns the check digit (the last one) of the
	 * Russian Tax ID given. 10-12 characters length.
	 */
	public static void   calcTaxIdCheckRus(StringBuilder taxId)
	{
		int tidl = taxId.length();
		if((tidl != 10) && (tidl != 12))
			illegalTaxId(taxId);

		int w0 = 0, w1 = 0;

		//~: 10-digit ID
		if(tidl == 10) for(int i = 0;(i < 9);i++)
		{
			int d = SU.digit(taxId.charAt(i));
			if(d == -1) illegalTaxId(taxId);

			w0 += d * TAX_ID_RUS_WEIGHTS[2 + i];
		}

		//~: 12-digit ID, phase 0
		if(tidl == 12) for(int i = 0;(i < 10);i++)
		{
			int d = SU.digit(taxId.charAt(i));
			if(d == -1) illegalTaxId(taxId);

			w0 += d * TAX_ID_RUS_WEIGHTS[1 + i];
		}

		w0 = w0 % 11; if(w0 == 10) w0 = 1;

		if(tidl == 10)
			taxId.setCharAt(9,  SU.digit(w0));
		if(tidl == 12)
			taxId.setCharAt(10, SU.digit(w0));

		//~: 12-digit ID, phase 1
		if(tidl == 12) for(int i = 0;(i < 11);i++)
		{
			int d = SU.digit(taxId.charAt(i));
			w1 += d * TAX_ID_RUS_WEIGHTS[i];
		}

		w1 = w1 % 11; if(w1 == 10) w1 = 1;
		if(tidl == 12)
			taxId.setCharAt(11, SU.digit(w1));
	}

	public static void   checkTaxIdRus(String taxId)
	{
		StringBuilder s = new StringBuilder(taxId);

		calcTaxIdCheckRus(s);
		if(!SU.eq(s, taxId, 0, s.length()))
			illegalTaxId(taxId);
	}

	public static String genTaxIdRus(Random rnd, boolean is12)
	{
		StringBuilder s = new StringBuilder().
		  append(GenUtils.number(rnd, is12?(12):(10)));

		calcTaxIdCheckRus(s);
		return s.toString();
	}

	private static void  illegalAccount(String bankId, CharSequence account)
	{
		throw EX.state("Illegal account format for the bank ID '",
		  bankId, "' and account '", account, "'!"
		);
	}

	private static void  illegalTaxId(CharSequence taxId)
	{
		throw EX.state("Illegal Tax ID '", taxId, "'!");
	}


	/* types of payments */

	public static boolean isIncomeAllowed(PayWay way)
	{
		return (way.getTypeFlag() != 'E');
	}

	public static boolean isExpenseAllowed(PayWay way)
	{
		return (way.getTypeFlag() != 'I');
	}
}
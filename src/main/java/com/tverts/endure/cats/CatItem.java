package com.tverts.endure.cats;

/* tverts.com: endure (core) */

import com.tverts.endure.NumericIdentity;
import com.tverts.endure.core.Domain;
import com.tverts.endure.core.DomainEntity;


/**
 * Entities implementing this interface are
 * catalogue items that have name and code
 * unique within the domain.
 *
 * @author anton.baukin@gmail.com
 */
public interface CatItem
       extends   NumericIdentity, DomainEntity,
                 CodedEntity, NamedEntity
{
	/* Catalogue Item */

	public void setDomain(Domain domain);
}
package com.tverts.endure;

/**
 * TODO comment UnitingLink
 *
 * @author anton.baukin@gmail.com
 */
public interface UnitingLink
{
	/* public: UnitingLink interface */

	public UnityType getLinkType();

	public void      setLinkType(UnityType linkType);

	public Unity     getOwner();

	public void      setOwner(Unity owner);

	public Unity     getLinked();

	public void      setLinked(Unity linked);

	public long      getLinkGroup();

	public void      setLinkGroup(long linkGroup);

	public long      getLinkHelper();

	public void      setLinkHelper(long linkHelper);
}
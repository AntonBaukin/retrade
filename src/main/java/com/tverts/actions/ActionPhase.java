package com.tverts.actions;

/**
 * The stages of action processing.
 *
 * TODO add comments to ActionPhase
 *
 * @author anton.baukin@gmail.com
 */
public enum ActionPhase
{
	/* phases of action processing */

	/**
	 * Action context bind phase.
	 */
	BIND,

	/**
	 * The initialization phase.
	 */
	OPEN,

	/**
	 * The action work phase.
	 */
	TRIGGER,

	/**
	 * The cleanup phase.
	 */
	CLOSE
}
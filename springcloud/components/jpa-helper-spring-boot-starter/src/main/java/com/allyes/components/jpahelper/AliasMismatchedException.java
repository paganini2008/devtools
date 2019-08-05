package com.allyes.components.jpahelper;

/**
 * 
 * AliasMismatchedException
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public class AliasMismatchedException extends IllegalArgumentException {

	private static final long serialVersionUID = 2735877872090011720L;

	public AliasMismatchedException(String alias) {
		super(alias);
	}

}

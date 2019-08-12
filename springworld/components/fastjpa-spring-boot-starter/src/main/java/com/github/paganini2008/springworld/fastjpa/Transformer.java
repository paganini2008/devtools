package com.github.paganini2008.springworld.fastjpa;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Selection;

/**
 * 
 * Transformer
 *
 * @author Fred Feng
 * @revised 2019-07
 * @created 2019-02
 * @version 1.0
 */
public interface Transformer<E, T> {

	T transfer(Model<E> model, List<Selection<?>> selections, Tuple tuple);

}

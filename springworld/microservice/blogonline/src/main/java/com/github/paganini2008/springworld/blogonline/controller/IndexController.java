package com.github.paganini2008.springworld.blogonline.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.paganini2008.springworld.support.vo.ResultVO;

/**
 * 
 * IndexController
 * 
 * @author Fred Feng
 * @created 2019-07
 * @revised 2019-07
 * @version 1.0
 */
@RestController
public class IndexController {

	@GetMapping("/index")
	public ResultVO<String> index(){
		return ResultVO.success("ok");
	}
	
}

package com.nhis.api.rest;

import com.nhis.comm.controller.ControllerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nhis.api.rest.Nhis_APIfacade.*;



/**
 * Created by sewoo on 2017. 1. 3..
 *	API 시작 Controller
 */

@RestController
@RequestMapping(Path)
public class Nhis_API extends ControllerSupport {


	@GetMapping(ProCode)
	public String api() throws Exception {


		return "";
	}




}

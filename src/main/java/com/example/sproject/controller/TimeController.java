package com.example.sproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("time")
public class TimeController {
	@RequestMapping("")
	public String index(Model model) {
		return "index";
	}
}
package com.example.blog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {

	private String title;
	private String contents;
	private String url;
	private String blogname;
	private String datetime;
}

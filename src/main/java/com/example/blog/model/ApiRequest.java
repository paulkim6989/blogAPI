package com.example.blog.model;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequest {
	
	private String query;
	private String sort;
	private Integer page;
	private Integer size;
	private String apiName;
	
}

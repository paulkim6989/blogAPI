package com.example.blog.model;

import java.util.List;
import java.util.Map;

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
public class Config {
	private String name;
	private String url;
	private String dataField;
	private List<Apikey> apikey;
	private Map<String,Map<String,String>> param;
	
	public void setApikey(final List<Apikey> apikey) {
       this.apikey = apikey;
   }
}

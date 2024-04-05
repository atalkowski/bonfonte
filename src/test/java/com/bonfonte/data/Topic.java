package com.bonfonte.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "topic")
@XmlType(name = "", propOrder = { "id", "name", "description" })
public class Topic {
	private Long id;
    private String name;
    private String description;
//    private Long parentId;
    
    @XmlElement(name = "id")
	public Long getId() {
		return id;
	}
    @XmlElement(name = "name")
	public String getName() {
		return name;
	}
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String show() {
		StringBuilder b = new StringBuilder( "Topic [" + id );
		b.append( "," ).append( name );
		b.append( "," ).append( description ).append( "]");
		return b.toString();
	}
}

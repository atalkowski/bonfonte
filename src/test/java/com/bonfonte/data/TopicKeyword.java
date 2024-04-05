package com.bonfonte.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "TopicKeyword")
@XmlType(name = "", propOrder = { "topicId", "keyword", "occurs" })
public class TopicKeyword {
	Long topicId;
	String keyword;
	Long occurs;

	@XmlElement(name = "topicId")
	public Long getTopicId() {
		return topicId;
	}
	@XmlElement(name = "keyword")
	public String getKeyword() {
		return keyword;
	}
	@XmlElement(name = "occurs")
	public Long getOccurs() {
		return occurs;
	}
	
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public void setOccurs(Long occurs) {
		this.occurs = occurs;
	}

}

package com.bonfonte.service;

import java.util.List;

import com.bonfonte.data.Topic;

public interface TopicService {
	List<Topic> getTopics();
	List<Topic> getSubTopics( Long topicId );
	List<Topic> getTopics( String keyword );
	
}

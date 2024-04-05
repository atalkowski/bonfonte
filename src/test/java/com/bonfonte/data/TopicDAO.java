package com.bonfonte.data;

import java.util.List;

public interface TopicDAO {
	Topic getTopicById( Long id );
	List<Topic> getRootTopics( );
	List<Topic> getSubTopics( Long id );
	List<Topic> getTopicsByKeyword( String keyword );
}

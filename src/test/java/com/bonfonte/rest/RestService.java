package com.bonfonte.rest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * A sample restful service. 
 */

@Produces({"application/xml", "application/json"})
@Consumes({ "application/xml", "application/json"})
@Path("/rest")
public interface RestService {
	/**
     * Get the version.
     * @return the version string
     * @throws Exception if it fails anywhere in transport layer
     */
    @GET
    @Path("/version")
    @Produces({"application/xml"})
    Response getVersion() throws Exception;

    /**
     * List the topics that are roots subjects within the system (having no parent).
     * @return the topics that are the main root categories
     * @throws Exception if it fails anywhere in transport layer
     */
    @GET
    @Path("/topics")
    @Produces({"application/xml"})
    Response getTopics( ) throws Exception;

    /**
     * List the topics that are roots subjects within the system (having no parent) in JSON format.
     * @return the topics that are the main root categories
     * @throws Exception if it fails anywhere in transport layer
     */
    @GET
    @Path("/topjs")
    @Produces({"application/json"})
    String getTopicsAsJSON( ) throws Exception;


    /**
     * List the topics that are roots subjects within the system (having no parent) in JSON format.
     * @return the topics that are the main root categories
     * @throws Exception if it fails anywhere in transport layer
     */
    @GET
    @Path("/topjson")
    @Produces({"application/json"})
    Response getTopicsJSON( ) throws Exception;

    
    /**
     * List the topics that match the keyword given in JSON format.
     * This one uses a rest path parameter.
     * @param keyword to search
     * @return the topics that match the topic given
     * @throws Exception if it fails anywhere in transport layer
     */
    @GET
    @Path("/subtopics/{topicId}")
    @Produces({"application/xml"})
    Response getSubTopics(
        @PathParam("topicId") Long topicId ) throws Exception;


    /**
     * Search topics that match the keyword given in XML format.
     * This one uses a query param.
     * @param keyword to search
     * @return the topics that match the topic given
     * @throws Exception if it fails anywhere in transport layer
     */
    @GET
	@Path("/search")
    @Produces({"application/xml"})
	Response getSearchResults(
	        @QueryParam("keyword") String keyword );
}




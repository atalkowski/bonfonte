package com.bonfonte.data;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import javax.xml.bind.annotation.XmlRootElement;


@Provider
@Produces("application/json")
public class JSONMarshaller<T> implements MessageBodyWriter<T> {

	@Context
	protected Providers providers;


	public long getSize(T arg0, Class<?> arg1, Type arg2, Annotation[] arg3,
			MediaType arg4) {
		return -1; // Signal that we cannot easily tell and we push the problem onto the 
		           // marshaller to chunk the output.
	}

	public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
			MediaType arg3) {
		return arg0.isAnnotationPresent(XmlRootElement.class);
	}

	public void writeTo(T target, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream outputStream) throws IOException, WebApplicationException {
		try {
			JAXBContext ctx = null;
			ContextResolver<JAXBContext> resolver =
		    	providers.getContextResolver(JAXBContext.class, mediaType);
			if (resolver != null) {
                ctx = resolver.getContext(type);
			}
			if ( ctx == null ) {
				ctx = JAXBContext.newInstance(type);
			}
			Marshaller m = ctx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(target, outputStream);
			
		} catch (JAXBException ex) {
			throw new RuntimeException(ex);
		}
		return;
	}

}
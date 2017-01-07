package com.commsen.liferay.builtwith.rest;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.commsen.liferay.builtwith.api.CheckDTO;
import com.commsen.liferay.builtwith.api.SiteChecker;
import com.commsen.liferay.builtwith.api.SiteStorage;
import com.google.gson.Gson;

//import aQute.bnd.annotation.headers.RequireCapability;

@Path("/check")
@Component (immediate = true, service = Object.class)
//@RequireCapability(ns="osgi.contract", filter="(&(osgi.contract=JavaJAXRS)(version=2))")
public class StandaloneSiteChecker {

	@Reference
	private SiteChecker siteChecker;
	
	@Reference(
			policy=ReferencePolicy.DYNAMIC, 
			policyOption=ReferencePolicyOption.GREEDY, 
			cardinality=ReferenceCardinality.MANDATORY
			)
	private volatile SiteStorage siteStorage;
	
	
	@GET
	@Path("/{domain}")
	@Produces(MediaType.APPLICATION_JSON)
	public String check(@PathParam ("domain") String domain) throws Exception {

		URI uri = new URI("http://"+domain);
		
		CheckDTO check = siteStorage.get(uri);
		if (check == null) {
			check = siteChecker.check(uri);
			siteStorage.save(check);
		}
		
		return new Gson().toJson(check);
		
	}
	

}

package de.onto_med.webprotege_rest_api.task;

import java.io.PrintWriter;

import com.google.common.collect.ImmutableMultimap;

import de.onto_med.webprotege_rest_api.resources.MetaProjectResource;
import io.dropwizard.servlets.tasks.Task;

public class ClearCacheTask extends Task {

	private MetaProjectResource metaProjectResource;

	public ClearCacheTask(MetaProjectResource metaProjectResource) {
		super("clear_cache");
		this.metaProjectResource = metaProjectResource;
	}

	@Override
	public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
		metaProjectResource.clearCache();
	}

}

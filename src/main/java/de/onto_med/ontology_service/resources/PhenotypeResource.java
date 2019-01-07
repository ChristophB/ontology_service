package de.onto_med.ontology_service.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.onto_med.ontology_service.api.Timer;
import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import de.onto_med.ontology_service.data_model.Property;
import de.onto_med.ontology_service.manager.PhenotypeManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.Entity;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypeManager.class);

	private String  phenotypePath;

	/**
	 * This is the Cache, which contains all previously loaded phenotypeManagers.
	 * Expiration time is set to 10 minutes after last access.
	 * If a non existent key is used, the cache tries to instantiate a respective PhenotypeManager.
	 */
	private LoadingCache<String, PhenotypeManager> managers = CacheBuilder.newBuilder()
		.expireAfterAccess(10, TimeUnit.MINUTES)
		.build(
			new CacheLoader<String, PhenotypeManager>() {
				@Override
				public PhenotypeManager load(@Nonnull String key) {
					Timer            timer   = new Timer();
					PhenotypeManager manager = new PhenotypeManager(phenotypePath.replace("%id%", key));

					LOGGER.info("Populated cache with phenotype ontology '" + key + "'. " + timer.getDiff());
					return manager;
				}
			}
		);

	public PhenotypeResource(String rootPath, String phenotypePath) {
		super(rootPath);
		this.phenotypePath = phenotypePath;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhenotypeSelectionView() {
		Map<String, Long> ontologies = new HashMap<>();

		getPhenotypeOntologyFiles().forEach(file -> ontologies.put(getIdFromFilename(file.getName()), file.length() / 1000));

		List<Map<String, String>> jsonData = new ArrayList<>();
		ontologies.keySet().forEach(k -> {
			Map<String, String> hash = new HashMap<>();
			hash.put("id", k);
			hash.put("size", ontologies.get(k).toString());
			jsonData.add(hash);
		});

		return Response.ok(jsonData).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeView(@PathParam("id") String id) {
		try {
			return Response.ok(managers.getUnchecked(id).getFullRdfDocument())
				.header(HttpHeaders.CONTENT_DISPOSITION,
					String.format("attachment; filename='cop_%s.owl'", id))
				.build();
		} catch (IOException e) {
			return Response.ok(e.getMessage()).status(500).build();
		}
	}

	@GET
	@Path("/{id}/{iri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhenotype(@PathParam("id") String id, @PathParam("iri") String iri) {
		PhenotypeManager manager = managers.getUnchecked(id);
		try {
			return Response.ok(manager.getEntity(iri)).build();
		} catch (Exception e) {
			return Response.ok(e.getMessage()).status(500).build();
		}
	}

	@GET
	@Path("{id}/{iri}/dependents")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Entity> getDependentPhenotypes(@PathParam("id") String id, @PathParam("iri") String iri) {
		PhenotypeManager manager    = managers.getUnchecked(id);
		List<Entity>  phenotypes = new ArrayList<>();

		if (manager.getEntity(iri).isPhenotype()) {
			phenotypes.addAll(manager.getDependentPhenotypes(iri));
		}
		phenotypes.add(manager.getEntity(iri));

		return phenotypes;
	}

	@GET
	@Path("{id}/{iri}/parts")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PhenotypeFormData> getPartsOfPhenotype(@PathParam("id") String id, @PathParam("iri") String iri) {
		return managers.getUnchecked(id).getParts(iri);
	}

	@GET
	@Path("{id}/{iri}/restrictions")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> getRestrictionsOfPhenotype(@PathParam("id") String id, @PathParam("iri") String iri) {
		return managers.getUnchecked(id).getRestrictions(iri);
	}

	@POST
	@Path("{id}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpHeaders headers, @PathParam("id") String id) {
		managers.invalidate(id);
		try {
			Files.delete(Paths.get(phenotypePath.replace("%id%", id)));
		} catch (IOException e) {
			LOGGER.warn(e.getMessage());
			return Response.seeOther(UriBuilder.fromUri(rootPath + "/phenotype").build()).build();
		}
		if (acceptsMediaType(headers, MediaType.TEXT_HTML_TYPE))
			return Response.seeOther(UriBuilder.fromUri(rootPath + "/phenotype").build()).build();
		return Response.ok("Ontology 'id' deleted.").build();
	}

	@POST
	@Path("{id}/delete-phenotypes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePhenotypes(@PathParam("id") String id, Set<String> phenotypes) {
		try {
			managers.getUnchecked(id).deletePhenotypes(phenotypes);
		} catch (Exception e) {
			return Response.serverError().build();
		}
		return Response.ok("Phenotypes deleted.").build();
	}

	@GET
	@Path("/{id}/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhenotypes(@PathParam("id") String id, @QueryParam("type") String type) {
		if ("list".equals(type)) {
			return Response.ok(managers.getUnchecked(id).getList()).build();
		} else {
			return Response.ok(managers.getUnchecked(id).getTaxonomy(true)).build();
		}
	}

	@GET
	@Path("/{id}/decision-tree")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDecisionTree(
		@PathParam("id") String id, @QueryParam("phenotype") String phenotype, @QueryParam("format") String format
	) {
		if (StringUtils.isBlank(phenotype))
			return Response.ok("Query parameter 'phenotype' missing.").status(500).build();

		PhenotypeManager manager = managers.getUnchecked(id);

		try {
			return Response.ok(manager.getPhenotypeDecisionTree(phenotype, format), MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + phenotype + "_decisiontree." + format + "'")
				.build();
		} catch (IllegalArgumentException | IOException e) {
			return Response.ok(e.getMessage()).status(500).build();
		}
	}

	@POST
	@Path("/{id}/create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEntity(@PathParam("id") String id, PhenotypeFormData formData) {
		PhenotypeManager manager = managers.getUnchecked(id);

		if (formData == null) return null;
		try {
			if (!formData.getIsPhenotype()) {
				Category category = manager.createCategory(formData);
				return Response.ok(new CrudeResponse(category.getName(), "Category '" + category.getName() + "' created.")).build();
			} else if (!formData.getIsRestricted()) {
				AbstractPhenotype phenotype = manager.createAbstractPhenotype(formData);
				return Response.ok(new CrudeResponse(phenotype.getName(), "Abstract phenotype '" + phenotype.getName() + "' created.")).build();
			} else {
				RestrictedPhenotype phenotype = manager.createRestrictedPhenotype(formData);
				return Response.ok(new CrudeResponse(phenotype.getName(), "Phenotype '" + phenotype.getName() + "' created.")).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok(e.getMessage()).status(500).build();
		}
	}

	@POST
	@Path("{id}/reason")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
	public synchronized Response classifyIndividual(
		@Context HttpHeaders headers, @PathParam("id") String id, List<Property> properties, @QueryParam("format") String format
	) {
		if (properties == null || properties.isEmpty()) {
			LOGGER.warn("Reasoning request without properties");
			return Response.ok("No properties were provided.").status(500).build();
		}

		PhenotypeManager manager = managers.getUnchecked(id);

		try {
			if ("png".equals(format)) {
				return Response.ok(Base64.encodeBase64(manager.classifyIndividualAsImage(properties)), MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='reasoner_report.png'")
					.build();
			} else {
				return Response.ok(manager.classifyIndividualAsList(properties)).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok(e.getMessage()).status(500).build();
		}
	}

	private String getIdFromFilename(String filename) {
		String namePattern = phenotypePath.replaceFirst(".*/", "");
		String prefix      = namePattern.replaceFirst("%id%.*", "");

		return filename.replaceFirst("\\..+$", "").replace(prefix, "");
	}

	private List<File> getPhenotypeOntologyFiles() {
		return Arrays.asList(
			Objects.requireNonNull(new File(phenotypePath.replaceFirst("/[^/]*$", ""))
				.listFiles(new CopFileFilter())));
	}

	@SuppressWarnings("unused")
	private List<String> getPhenotypeOntologyIris() {
		return getPhenotypeOntologyFiles().stream().map(
			file -> PhenotypeManager.buildIri(getIdFromFilename(file.getName()))).collect(Collectors.toList());
	}

	@SuppressWarnings("unused")
	private PhenotypeManager getPhenotypeManager(String id) {
		try {
			return managers.get(id);
		} catch (ExecutionException e) {
			return null;
		}
	}

	public static class CopFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith(".owl")
				   && !pathname.getName().startsWith("~") && pathname.getName().startsWith("cop_");
		}
	}

	public class CrudeResponse {
		public String id;
		public String message;

		@SuppressWarnings("WeakerAccess")
		public CrudeResponse(String id, String message) {
			this.id = id;
			this.message = message;
		}
	}
}

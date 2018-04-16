<#assign current = "Documentation">
<#assign title = "Documentation">
<#assign heading = "Documentation of the Ontology Service">
<#assign subHeading ="This page contains a list of all available RESTfull functions with respective URL and query/JSON parameters.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/Messages.ftl">

    	<div class="container">
    		<div class="row">
	    		<table class="table">
		    		<thead>
		    			<tr>
		   					<th>Path</th>
		   					<th></th>
		   					<th>Description</th>
		   				</tr>
		    		</thead>
		    		
		    		<tbody>
						<tr>
							<td colspan="2"><a href="${rootPath}/clear_cache">/clear_cache</a></td>
							<td>Clears the ProjectManager cache to speed up the cognition of changes via WebProt&#233;g&#233;.</td>
						</tr>
						
						<tr>
							<td colspan="2">/entity</td>
							<td>
								<p>Search for a single or multiple entities in multiple projects.</p>
								<ul>
									<li><b>type:</b> Entity, class or individual</li>
									<li><b>name:</b> Entity name</li>
									<li><b>match:</b> Match method for 'name' parameter: 'exact' or 'loose' (default: loose)</li>
									<li><b>property:</b> Name of a Property, the entity is annotated with</li>
									<li><b>value:</b> Value of the specified Property</li>
									<li><b>operator:</b> Logical operator to combine 'name' and 'property' (default: and)</li>
									<li><b>ontologies:</b> List of comma separated ontology ids (default: all ontologies)</li>
								</ul>
							</td>	
						</tr>
						
						<tr>
							<td colspan="2">/entity-form</td>
							<td><p>Form-based user interface to search for entities in one or multiple projects.</p></td>
						</tr>

						<tr>
							<td rowspan="2">/phenotype</td>
							<td>/</td>
							<td>List of available phenotype ontologies with download and delete option</td>
						</tr>

						<tr>
							<td>/create</td>
							<td>
								Creates an empty phenotype ontology.
								<ul><li><b>id:</b> ID of the phenotype ontology</li></ul>
							</td>
						</tr>

						<tr>
							<td rowspan="8">/phenotype/{id}</td>
						</tr>
						
						<tr>
							<td>/</td>
							<td>Download of the phenotype ontology as OWL file.</td>
						</tr>
						
						<tr>
							<td>/all</td>
							<td>A List of all phenotypes and/or phenotype categories</td>
						</tr>
						
						<tr>
							<td>/create</td>
							<td>
								Creates or update a phenotype with provided data. <i>/phenotype-form</i> sends its data to this endpoint.<br>
								All parameters in the following list, which end with '[]' are handled as lists and thus can be provided multiple times. Their order may be important to map for example language to label.
								<ul>
									<li><b>titles[]:</b> Unique identifying titles</li>
									<li><b>aliases[]:</b> Aliases</li>
									<li><b>titleLanguages[]:</b> Respective languages of the titles</li>
									<li><b>labels:</b> Alternative labels</li>
									<li><b>labelLanguages:</b> Respective languages of the alternative labels</li>
									<li><b>isPhenotype:</b> true, if this is a phenotype, false, if this is a category</li>
									<li><b>isRestricted:</b> true, if this is a restricted phenotype, else false</li>
									<li><b>superPhenotype:</b> The ID of an existing phenotype, which will be used as super phenotype.</li>
									<li><b>superCategory:</b> The ID of an existing category, which will be used as super category</li>
									<li><b>categories:</b> Category ID in which the phenotype will be placed</li>
									<li><b>descriptions[]:</b> Textual descriptions</li>
									<li><b>descriptionLanguage[]:</b> Description languages</li>
									<li><b>datatype:</b> one of 'numeric', 'date', 'string', 'formula', 'expression' (required)</li>
									<li><b>isDecimal:</b> true if the numeric phenotype is decimal, else false</li>
									<li><b>ucum:</b> Unit of a numeric/formula phenotype as UCUM</li>
									<li><b>rangeMin:</b> Minimal value for sub phenotype range</li>
									<li><b>rangeMinOperator:</b> Operator for range-min ('=', '&ge;', '>')</li>
									<li><b>rangeMax:</b> Maximum value for sub phenotype range</li>
									<li><b>rangeMaxOperator:</b> Operator for range-max ('<', '&le;', '=')</li>
									<li><b>enumValues[]:</b> Enumeration value for string phenotypes</li>
									<li><b>formula:</b> A mathematical formula which may contain other numerical phenotypes.</li>
									<li><b>expression:</b> A logical expression which may contain other phenotypes and mathematical symbols.</li>
									<li><b>relations[]:</b> IRI referencing other ontological entities.</li>
									<li><b>score:</b> The score value of this phenotype, if available</li>
								</ul>	
							</td>
						</tr>
						
						<tr>
							<td>/decision-tree</td>
							<td>
								Generates a decision tree for the specified phenotype.
								<ul><li><b>phenotype:</b> The phenotype identifier for which a decision tree will be generated.</li></ul>
							</td>
						</tr>

						<tr>
							<td>/delete</td>
							<td>Deletes the specified ontology.</td>
						</tr>

						<tr>
							<td>/delete-phenotype/{iri}</td>
							<td>Deletes the phenotype with path param iri as IRI from the specified ontology.</td>
						</tr>
						
						<tr>
							<td>/phenotype-form</td>
							<td>Interactive page to edit an ontology's phenotypes</td>
						</tr>
						
						<tr>
							<td rowspan="7">/project/{id}</td>
						</tr>
						
						<tr>
							<td>/</td>
							<td>Get full OWL document as RDF/XML.</td>
						</tr>
						
						<tr>
							<td>/classify</td>
							<td>
								<p>
									Creates an individual from JSON and returns its inferred classes. (only available via JSON request)<br>
									JSON template:
								</p>
									<pre><small>[ { "types": [ "http://onto-med.de/auxology#patient" ],
    "properties": [
      { "name":      "http://onto-med.de/auxology#bmi_sds",
        "className": "float",
        "value":     "-1.5f" }
    ] }, ... 
]</small></pre>
							</td>
						</tr>

						<tr>
							<td>/graphml</td>
							<td>Responses with an GraphML file for the specified ontology.</td>
						</tr>

						<tr>
							<td>/imports</td>
							<td>List the imported ontologies for this project.</td>
						</tr>
						
						<tr>
							<td>/overview</td>
							<td>Short overview page for the specified project with meta information and a list of imported ontologies.</td>
						</tr>

						<tr>
							<td>/taxonomy</td>
							<td>A browsable tree of concepts and individuals, where detailed information about entities can be retrieved.</td>
						</tr>


						
						<tr>
							<td colspan="2">/projects</td>
							<td>List all available projects/ontologies with a short description and id.</td>
						</tr>
						
						<tr>
		    				<td colspan="2">/reason</td>
		    				<td>
		    					<p>Search for individuals by reasoning in one or multiple projects.</p>
		    	   				<ul>
		    	   					<li><b>ce:</b> Class expression</li>
									<li><b>ontologies:</b> List of comma separated ontology ids (default: all ontologies)</li>
								</ul>
							</td>
						</tr>
						
						<tr>
							<td colspan="2">/reason-form</td>
							<td><p>Form-based user interface to reason in one or multiple projects.</p></td>
						</tr>
						
		    		</tbody>
		    	</table>
		    </div>
	    </div>
	    
	    <#include "partials/Footer.ftl">
	</body>
</html>

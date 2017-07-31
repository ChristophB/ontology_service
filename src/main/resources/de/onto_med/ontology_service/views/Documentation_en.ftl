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
							<td rowspan="4">/project/{id}</td>
						</tr>
						<tr>
							<td>/</td>
							<td>Get full OWL document as RDF/XML.</td>
						</tr>
						
						<tr>
							<td>/classify</td>
							<td>
								<p>
									Creates an individual from JSON and returns its infered classes. (only available via JSON request)<br>
									JSON template:
									<small>
										<pre>
[ { "types": [ "http://onto-med.de/auxology#patient" ],
    "properties": [
      { "iri":       "http://onto-med.de/auxology#bmi_sds",
        "className": "float",
        "values":    [ "-1.5f" ] }
    ] }, ... 
]</pre>
    								</small>
								</p>
							</td>
						</tr>
						
						<tr>
							<td>/overview</td>
							<td><p>Short overview page for the specified project.</p></td>
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

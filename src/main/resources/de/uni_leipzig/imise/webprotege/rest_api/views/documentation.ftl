<#-- @ftlvariable name="" type="de.uni_leipzig.imise.webprotege.rest_api.views.DocumentationView" -->
<html>
	<body>
		<h1>Documentation of WebProt&#233;g&#233; REST-Interface</h1>
    	<table>
    		<thead>
    			<tr>
   					<th>Path</th>
   					<th>Parameter</th>
   					<th>Description</th>
   				</tr>
    		</thead>
    		
    		<tbody>
				
				<tr>
					<td>/entity</td>
					<td></td>
					<td>Search for a single or multiple entities.</td>
				</tr>
				<tr>
					<td></td>
					<td>type</td>
					<td>Entity, class or individual</td>
				</tr>
				<tr>
					<td></td>
					<td>name</td>
					<td>Entity name</td>
				</tr>
				<tr>
					<td></td>
					<td>match</td>
					<td>Match method for 'name' parameter: 'exact' or 'loose' (default: loose)</td>
				</tr>
				<tr>
					<td></td>
					<td>property</td>
					<td>Name of a Property, the entity is annotated with</td>
				</tr>
				<tr>
					<td></td>
					<td>value</td>
					<td>Value of the specified Property</td>
				</tr>
				<tr>
					<td></td>
					<td>operator</td>
					<td>Logical operator to combine 'name' and 'property' (default: and)</td>
				</tr>
				<tr>
					<td></td>
					<td>ontologies</td>
					<td>List of comma separated ontology ids (default: all ontologies)</td>	
				</tr>
				
				<tr>
					<td>/project/{id}</td>
					<td></td>
					<td>Get full OWL document as RDF/XML.</td>
				</tr>
				
				<tr>
					<td>/project/{id}/imports</td>
					<td></td>
					<td>List all imports of the specified ontology.</td>
				</tr>
				
				<tr>
					<td>/projects</td>
					<td></td>
					<td>List all available projects/ontologies with a short description and id.</td>
				</tr>
				
				<tr>
    				<td>/reason</td>
    				<td></td>
    				<td>Search for individuals by reasoning</td>
    			</tr>
    			<tr>
    				<td></td>
    				<td>ce</td>
    				<td>Class expression (currently not working with short forms, use full IRIs instead)</td>
				</tr>
				<tr>
					<td></td>
					<td>ontologies</td>
					<td>List of comma separated ontology ids (default: all ontologies)</td>
				</tr>
				
    		</tbody>
    	</table>    
	</body>
</html>
<#assign title = "GraphML Form">
<#assign current = "Projects">
<#assign current_submenu = "graphml-form">
<#assign heading = "GraphML Generator for Project <i>${project.projectIri}</i>">
<#assign subHeading ="Use the form to customize the GraphML.">

<#macro createCheckboxes checkboxNames>
	<#list checkboxNames as name>
		<div class="checkbox">
			<label for="${name}" class="control-label">
				<input type="checkbox" name="${name}" value="true" checked>${name?replace("-", " ")?capitalize}
			</label>
		</div>
	</#list>
</#macro>>

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/project/Links.ftl">
		<#include "partials/Messages.ftl">
		
		<div class="container">
			<div class="row">
				<div id="taxonomy-tree" class="well col-sm-4">
				
				</div>
				
				<form action="${rootPath}/project/${project.projectId}/graphml" method="get" class="well col-sm-8">
					<div class="form-group row">
						<label for="start-class" class="control-label col-sm-3">Start Class</label>
						<div class="col-sm-4">
							<input type="text" class="form-control" id="start-class" name="start-class" required>
						</div>
						<small class="form-text text-muted col-sm-4">
							Select a start class on the left site.
						</small>
					</div>
					
					<div class="form-group row">
						<label for="taxonomy-direction" class="control-label col-sm-3">Taxonomy Direction</label>
						<div class="col-sm-4">
							<select class="form-control" name="taxonomy-direction">
								<option value="down">Down</option>
								<option value="up">Up</option>
							</select>
						</div>
					</div>
					
					<div class="form-group row">
						<label for="taxonomy-depth" class="control-label col-sm-3">Taxonomy Depth</label>
						<div class="col-sm-4">
							<input type="number" min="-1" class="form-control" name="taxonomy-depth" value="-1">
						</div>
						<small class="form-text text-muted col-sm-4">
							Set depth to -1 for no restrictions.
						</small>
					</div>
					
					<div class="from-group row">
						<label for="has-restriction-super-classes" class="control-label col-sm-3">Has Restriction Super Classes</label>
						<div class="col-sm-4">
							<select class="form-control" name="has-restriction-super-classes">
								<option value="with-type">With Type</option>
								<option value="without-type">Without Type</option>
								<option value="no">No</option>
							</select>
						</div>
					</div>
					
					<div class="form-group row">
						<label class="control-label col-sm-3">Additional Options</label>
						<div class="col-sm-4">
							<@createCheckboxes [ "has-grayscale", "has-taxonomy", "has-annotations", "has-property-definitions", "has-anonymous-super-classes" ] />
						</div>
						<div class="col-sm-4">
							<@createCheckboxes [ "has-equivalent-classes", "has-individuals", "has-individual-types", "has-individual-assertions" ] />
						</div>
					</div>
					<input type="submit" class="btn btn-default" value="Create GraphML">
				</form>
			</div>
		</div>
		
		<#include "partials/Footer.ftl">
		
		<script type="text/javascript">
			function convertToTree(data) {
				var tree = { children : [], a_attr : {} };
				
				data.subclasses.forEach(function(subclass) {
					tree.children.push(convertToTree(subclass));
				});
				
				tree.a_attr.iri = data.iri;
				tree.text = data.name;
				
				return tree;
			}
			
			$(document).ready(function() {
				$.getJSON('${rootPath}/project/${project.projectId}/taxonomy', function(data) {
					$('#' + 'taxonomy-tree').jstree({
						core : {
							multiple : false,
							data : convertToTree(data)
						}
					});
				});
				
				$('#taxonomy-tree').bind('select_node.jstree', function(e, selected) {
					$('#start-class').val(selected.node.a_attr.iri);
				});
			});
		</script>
	</body>
</html>
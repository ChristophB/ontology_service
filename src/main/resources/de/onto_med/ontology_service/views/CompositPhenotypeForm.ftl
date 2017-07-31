<!DOCTYPE html>

<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.EntityFormView" -->
<#assign title = "Composit Phenotype Create Form">
<#assign current = "Phenotypes">
<#assign current_submenu = "compositphenotype_form">
<#assign heading = "Composit Phenotype-Definition Form">
<#assign subHeading ="On this page you can define a new <strong>composit phenotype</strong> with all required metadata and properties.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/PhenotypeLinks.ftl">
		<#include "partials/Messages.ftl">
		
		<main class="container">	
			<!-- The single phenotype form -->
			<section name="content" class="row">
				<div class="col-md-9">
					<form id="composit-phenotype-form" class="" role="form" action="${rootPath}/phenotype/create" method="post" accept-charset="UTF-8">
						
						<#include "partials/phenotype/Id.ftl">
						<#include "partials/phenotype/Labels.ftl">
						<#include "partials/phenotype/SuperPhenotype.ftl">
						<#include "partials/phenotype/Category.ftl">
						<#include "partials/phenotype/Definitions.ftl">

						<#assign datatypes = [ 'formula', 'Numeric Formula', 'expression', 'Boolean Expression' ]>
						
						<div class="form-group">
							<#include "partials/phenotype/Datatype.ftl">
							<div class="row">
								<div class="col-sm-10 col-sm-offset-2" id="datatype-specification">
									<#include "partials/phenotype/Formula.ftl">
									<#include "partials/phenotype/Ucum.ftl">
									<#include "partials/phenotype/Ranges.ftl">
									<#include "partials/phenotype/BooleanExpression.ftl">
									<#include "partials/phenotype/Boolean.ftl">								
								</div>
							</div>
						</div>

						<#include "partials/phenotype/Relations.ftl">

						<div class="form-group">
							<input type="submit" class="btn btn-primary" value="Create Phenotype">
						</div>
					</form>
				</div>

				<!-- Form sidebar for additional information -->
				<div class="col-md-3">
					<div data-spy="affix" data-offset-top="200">
						<div id="phenotype-tree" class="well pre-scrollable "></div>
					</div>
				</div>
			</section>
		</main>

		<#include "partials/Footer.ftl">
		
		<!-- <footer>
			<div class="navbar navbar-default navbar-bottom">
					<div class="container">
						<p class="text-muted navbar-text">
							Provided by the <a href="http://www.onto-med.de">Onto-Med Research Group</a>
						</p>
						<p class="text-muted navbar-text pull-right">
							<a href="#">Help</a>
						</p>
					</div>
			</div>
		</footer>-->

		

		<script type="text/javascript">
			$(document).ready(function() {
				$('[data-toggle="tooltip"]').tooltip();
				createPhenotypeTree('phenotype-tree', '${rootPath}/phenotype/all');

				$('#expression-form-group input[type="button"]').on('click', function(button) {
					var expInput = $('#expression');
					expInput.val(expInput.val() + ' ' + this.value + ' ');
					focusInputEnd(expInput);
				});

				$('#expression').change(function() {
					$('#expression').scrollTop($('#expression')[0].scrollHeight);
				});
				$('#formula').change(function() {
					$('#formula').scrollTop($('#formula')[0].scrollHeight);
				});
			});
		</script>
	</body>
</html>
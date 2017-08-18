<!DOCTYPE html>

<#assign title = "Phenotype Create Form">
<#assign current = "Phenotypes">
<#assign current_submenu = "phenotype-form">
<#assign heading = "Phenotype-Definition Form">
<#assign subHeading = "On this page you can define a new phenotype with all required metadata and properties.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/phenotype/Links.ftl">
		<#include "partials/Messages.ftl">

		<main class="container">
			<section name="content" class="row">
			    <div class="col-md-3">
                	<div data-spy="affix" data-offset-top="300">
                		<div id="phenotype-tree" class="well pre-scrollable "></div>
                	</div>
                </div>

				<div class="col-md-9">
                    <#include "partials/phenotype/AbstractPhenotypeForm.ftl">
                    <#include "partials/phenotype/CategoryForm.ftl">
                    <#include "partials/phenotype/NumericPhenotypeForm.ftl">
                    <#include "partials/phenotype/StringPhenotypeForm.ftl">
                    <#include "partials/phenotype/BooleanPhenotypeForm.ftl">
                    <#include "partials/phenotype/CalculationPhenotypeForm.ftl">
                    <#include "partials/phenotype/DatePhenotypeForm.ftl">
				</div>
			</section>
		</main>

		<#include "partials/Footer.ftl">

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

				toggleValueDefinition();
			});
		</script>
	</body>
</html>
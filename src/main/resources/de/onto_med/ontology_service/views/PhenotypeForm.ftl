<!DOCTYPE html>

<#assign title = "Phenotype Create Form">
<#assign current = "Phenotyping">
<#assign current_submenu = "phenotype-form">
<#assign heading = "Phenotype Editing">
<#assign subHeading = "On this page you can create or update phenotypes and their metadata and properties.">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/phenotype/Links.ftl">
		<#include "partials/Messages.ftl">

		<main class="container">
			<section name="content" class="row">
			    <div class="col-md-4">
                	<div id="phenotype-tree" class="well pre-scrollable "></div>
                </div>

				<div class="col-md-8">
                    <#include "partials/phenotype/AbstractPhenotypeForm.ftl">
                    <#include "partials/phenotype/CategoryForm.ftl">
                    <#include "partials/phenotype/NumericPhenotypeForm.ftl">
                    <#include "partials/phenotype/DatePhenotypeForm.ftl">
                    <#include "partials/phenotype/StringPhenotypeForm.ftl">
                    <#include "partials/phenotype/BooleanPhenotypeForm.ftl">
                    <#include "partials/phenotype/CalculationPhenotypeForm.ftl">
                    <#include "partials/phenotype/CompositeBooleanPhenotypeForm.ftl">
				</div>
			</section>
		</main>

		<#include "partials/Footer.ftl">

		<script type="text/javascript">
			$(document).ready(function() {
				$('[data-toggle="tooltip"]').tooltip();
				createPhenotypeTree('phenotype-tree', '${rootPath}/phenotype/${id}/all', true);

				$('#expression-form-group input[type="button"].operator').on('click', function(button) {
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


				$('form #submit').on('click', function() {
                    $.ajax({
                        url: $($(this).closest('form')[0].attributes.url).val(),
                        dataType: 'text',
                        contentType: 'application/json',
                        processData: false,
                        type: 'POST',
                        data: JSON.stringify($($(this).closest('form')[0]).serializeJSON()),
                        success: function(result) {
                            $('#phenotype-tree').jstree('refresh');
                            clearPhenotypeFormData();
                            showMessage(result, 'success');
                        },
                        error: function(result) {
                            var response = JSON.parse(result.responseText);
                            showMessage(response.message, 'danger');
                        }
                    });
                });

				toggleValueDefinition();
			});
		</script>
	</body>
</html>
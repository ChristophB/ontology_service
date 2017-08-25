<!DOCTYPE html>

<#assign title = "Phenotype Reasoning Form">
<#assign current = "Phenotypes">
<#assign current_submenu = "reason-form">
<#assign heading = "Phenotype Reasoning Form">
<#assign subHeading = "Define properties based on phenotypes and get inferred phenotypes.">

<html>
	<#include "partials/Head.ftl">

	<body>
		<#include "partials/Navbar.ftl">
		<#include "partials/Heading.ftl">
		<#include "partials/phenotype/Links.ftl">
		<#include "partials/Messages.ftl">

		<main class="container">
			<section name="content" class="row">
			    <div class="col-sm-4">
                	<div id="phenotype-tree" class="well pre-scrollable "></div>
                </div>

                <form id="reason-form" class="col-sm-7" action="" method="post">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <div class="panel-title pull-left">
                                Drag phenotypes from the right side and drop them into this form.
                            </div>
                            <div class="panel-title pull-right">
                                <input type="button" id="submit" class="btn btn-primary" value="Get Phenotypes">
                            </div>
                            <div class="clearfix"></div>
                        </div>
                        <div id="reason-form-drop-area" class="panel-body drop phenotype">

                        </div>
                    </div>
                </form>
            </section>
        </main>

        <#include "partials/Footer.ftl">

        <script type="text/javascript">
            $(document).ready(function() {
            	$('[data-toggle="tooltip"]').tooltip();
            	createPhenotypeTree('phenotype-tree', '${rootPath}/phenotype/all', false);

                $('form #submit').on('click', function() {
                    $.ajax({
                        url: '${rootPath}/phenotype/reason',
                        dataType: 'text',
                        contentType: 'application/json',
                        processData: false,
                        type: 'POST',
                        data: JSON.stringify($('#reason-form').serializeArray()),
                        success: function(result) {
                            $('#phenotype-tree').jstree('refresh');
                            showMessage(result, 'success');
                        },
                        error: function(result) {
                            var response = JSON.parse(result.responseText);
                            showMessage(response.message, 'danger');
                        }
                    });
                });
            });
        </script>
    </body>
</html>
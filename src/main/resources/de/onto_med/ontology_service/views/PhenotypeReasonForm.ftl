<!DOCTYPE html>

<#assign title = "Phenotype Reasoning Form">
<#assign current = "Phenotyping">
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
                                <a id="submit-button" class="btn btn-primary" href="#">
                                	Get Phenotypes
                                </a>
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
            	createPhenotypeTree('phenotype-tree', '${rootPath}/phenotype/${id}/all', false);

                $('form #submit-button').on('click', function() {
                	$('form #submit-button').html(
                		'<i class="fa fa-refresh fa-spin fa-fw" aria-hidden="true"></i>'
                		+ '<span class="sr-only">Loading...</span>'
                	).addClass('disabled');

                    $.ajax({
                        url: '${rootPath}/phenotype/${id}/reason',
                        dataType: 'text',
                        contentType: 'application/json; charset=utf-8',
                        processData: false,
                        type: 'POST',
                        data: JSON.stringify($('#reason-form').serializeArray()),
                        success: function(result) {
                            showMessage(result, 'success');
                            $.ajax({
                                url: '${rootPath}/phenotype/${id}/reason-image',
                                dataType: 'text',
                                contentType: 'application/json; charset=utf-8',
                                processData: false,
                                type: 'POST',
                                data: JSON.stringify($('#reason-form').serializeArray()),
                                success: function(png) {
                                    download('data:image/png;base64,' + png, 'reasoner_report.png', 'image/png');
                                    $('form #submit-button').html('Get Phenotypes').removeClass('disabled');
                                },
                                error: function(result) {
                                    var response = JSON.parse(result.responseText);
                                    showMessage(response.message, 'danger');
                                    $('form #submit-button').html('Get Phenotypes').removeClass('disabled');
                                }
                            });
                        },
                        error: function(result) {
                            var response = JSON.parse(result.responseText);
                            showMessage(response.message, 'danger');
                            $('form #submit-button').html('Get Phenotypes').removeClass('disabled');
                        }
                    });
                });
            });
        </script>
    </body>
</html>
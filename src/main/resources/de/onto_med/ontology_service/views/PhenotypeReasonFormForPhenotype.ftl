<!DOCTYPE html>

<#assign title = "Phenotype Reasoning Form">
<#assign current = "Phenotyping">
<#assign current_submenu = "reason-form">
<#assign heading = "Phenotype Reasoning Form">
<#assign subHeading = "Define properties based on phenotypes and get inferred phenotypes.">

<html>
	<#include "partials/Head.ftl">

	<body>
        <#if navigationVisible><#include "partials/Navbar.ftl"></#if>
		<#include "partials/Heading.ftl">
		<#include "partials/phenotype/Links.ftl">
		<#include "partials/Messages.ftl">

		<main class="container">
			<div name="content" class="row">
                <form id="reason-form" class="col-sm-6 col-sm-offset-3" action="" method="post" onSubmit="return false">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <div class="panel-title pull-left">
                                Specify below phenotypes and click submit to evaluate your entries.
                            </div>
                            <div class="panel-title pull-right">
                                <a id="submit-button" class="btn btn-primary" href="#">Submit</a>
                            </div>
                            <div class="clearfix"></div>
                        </div>
                        <div id="reason-form-fields" class="panel-body phenotype">
                            <table>
                                <thead>
                                    <tr><th>Title</th><th>Description</th><th>Value</th></tr>
                                </thead>
                                <tbody>
                                    <#list phenotypes as phenotype>
                                        <tr>
                                            <td>${phenotype.titleText}</td>
                                            <td><!-- ... --></td>
                                            <td>
                                                <#if phenotype.datatypeText == "boolean">
                                                    <select class="form-control" name="${phenotype.name}">
                                                        <option value="true">True</option>
                                                        <option value="false">False</option>
                                                    </select>
                                                </#if>
                                                <#if phenotype.datatypeText == "double">
                                                    <input type="number" class="form-control" name="${phenotype.name}" />
                                                </#if>
                                                <#if phenotype.datatypeText == "string">
                                                    <input type="text" class="form-control" name="${phenotype.name}" />
                                                </#if>
                                            </td>
                                        </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </form>
            </section>
        </main>

        <#include "partials/Footer.ftl">

        <script type="text/javascript">
            $(document).ready(function() {
            	$('[data-toggle="tooltip"]').tooltip();

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
                                url: '${rootPath}/phenotype/${id}/reason?format=png',
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
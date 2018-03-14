<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.PhenotypeView" -->

<#assign title = "Phenotype Reasoning Form">
<#assign current = "Phenotyping">
<#assign current_submenu = "reason-form">
<#assign heading = "Phenotype Reasoning Form - " + phenotype.titleText>
<#assign subHeading = "Define properties based on phenotypes and get inferred phenotypes.">

<html>
	<#include "partials/Head.ftl">

	<body>
        <#if navigationVisible><#include "partials/Navbar.ftl"></#if>
		<#include "partials/Heading.ftl">

        <div class="row">
            <div class="col-md-12 text-center">
                <div class="list-inline list-group list-group-horizontal">
                    <#if id??>
                        <a class="list-group-item" href="${rootPath}/phenotype/${id}/phenotype-form">Edit Phenotypes</a>
                    </#if>

                    <#if id??>
                        <a class="list-group-item active" href="#">Reasoning</a>
                    </#if>
                </div>
            </div>
        </div>

		<#include "partials/Messages.ftl">

		<main class="container">
			<div name="content" class="row">
                <form id="reason-form" class="col-sm-8 col-sm-offset-2" action="" method="post" onSubmit="return false">
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
                            <table class="table">
                                <thead>
                                    <tr><th>Title</th><th>Value</th><th>Unit</th><th></th></tr>
                                </thead>
                                <tbody>
                                    <#list parts?sort_by("title") as part>
                                        <tr>
                                            <td>${part.title}</td>
                                            <td>
                                                <#if part.datatype == "boolean">
                                                    <select class="form-control" name="${part.name}">
                                                        <option value="true">True</option>
                                                        <option value="false">False</option>
                                                    </select>
                                                </#if>
                                                <#if part.datatype == "double">
                                                    <input type="number" class="form-control" name="${part.name}" placeholder="10.00">
                                                </#if>
                                                <#if part.datatype == "integer">
                                                    <input type="number" step="1" class="form-control" name="${part.name}" placeholder="10">
                                                </#if>
                                                <#if part.datatype == "string">
                                                    <#if part.selectOptions?? && (part.selectOptions?size > 0)>
                                                        <input type="hidden" name="" id="${part.name}_select">
                                                        <select class="form-control" onchange="$('#${part.name}_select').attr('name', this.value)">
                                                            <option value=""></option>
                                                            <#list part.selectOptions?keys as key>
                                                                <option value="${key}">${part.selectOptions[key]}</option>
                                                            </#list>
                                                        </select>
                                                    <#else>
                                                        <input type="text" class="form-control" name="${part.name}" placeholder="String value">
                                                    </#if>
                                                </#if>
                                                <#if part.datatype == "dateTime">
                                                    <input type="date" class="form-control" name="${part.name}">
                                                </#if>
                                            </td>
                                            <td>${(part.ucum)!""}</td>
                                            <td>
                                                <#if part.descriptionMap?? && (part.descriptionMap?size > 0)>
                                                    <i class="fa fa-info-circle text-primary" aria-hidden="true"
                                                       data-toggle="tooltip" data-placement="left" data-html="true"
                                                       title="<#list part.descriptionMap?keys?sort as key><b>${key?upper_case}:</b> ${part.descriptionMap[key]?first}<br><br></#list>"
                                                    ></i>
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
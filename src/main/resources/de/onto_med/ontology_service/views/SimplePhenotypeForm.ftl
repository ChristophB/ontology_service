<!DOCTYPE html>

<#-- @ftlvariable name="" type="de.onto_med.webprotege_rest_api.views.EntityFormView" -->
<#assign title = "Phenotype Create Form">
<#assign current = "Phenotypes">
<#assign current_submenu = "simplephenotype_form">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">

		<main class="container">
			<section id="banner" class="row jumbotron text-center">
				<h2>Simple Phenotype-Definition Form</h2>
				<p class="text-muted">On this page you can define a new <strong>simple phenotype</strong> with all required metadata and properties.</p>
			</section>
			
			<#include "partials/PhenotypeLinks.ftl">
			
			<!-- The single phenotype form -->
			<section name="content" class="row">
				<div class="col-md-9">
					<form id="composit-phenotype-form" class="" role="form" action="${rootPath}/phenotype/create" method="post" accept-charset="UTF-8">
						
						<!-- ID -->
						<div class="form-group row">
							<label for="id" class="control-label col-sm-2">ID*</label>
							<div class="col-sm-6">
								<small class="form-text text-muted">Please try to formulate the ID as clear as possible and as short as possible.</small>
								<input type="text" class="form-control" name="id" placeholder="Some_Unique_Identifier" required>
							</div>
						</div>

						<!-- Labels -->
						<div class="form-group row">
							<div class="col-sm-2">
								<label class="control-label">Labels</label>
								<a class="btn btn-primary btn-xs" onclick="addRow('#labels-div')" data-toggle="tooltip" title="Add a Label" data-placement="right">
									<i class="fa fa-plus" aria-hidden="true"></i>
								</a>
							</div>
							
							<div class="col-sm-6" id="labels-div">
								<small class="form-text text-muted">Specify some human-readable labels in different languages.</small>
							
								<div class="input-group hidden">
									<div class="input-group-addon">
										<select name="label-language[]" data-toggle="tooltip" title="Language" data-placement="right">
											<option value="en">EN</option>
											<option value="de">DE</option>
											<option value="fr">FR</option>
											<option value="es">ES</option>
										</select>
									</div>
									<input type="text" class="form-control" name="label[]" placeholder="Some Label">
								</div>
							</div>
						</div>

						<!-- Super phenotype -->
						<div class="form-group row">
							<label class="control-label col-sm-2">Super-Phenotype</label>
							<div class="checkbox col-sm-3">
								<label for="has-super-phenotype"  class="control-label">
									<input type="checkbox" id="has-super-phenotype" name="has-super-phenotype" onchange="toggleSuperPhenotype()" value="true">Has Super-Phenotype
								</label>
							</div>
							<small class="form-text text-muted col-sm-6" id="has-super-phenotype-help">
								Check this box, if this phenotype is a derivation of another phenotype.<br>
								e.g.: "Weight between 50kg and 60kg" is a derivation of phenotype "Weight".
							</small>
							
							<div id="super-phenotype-div" class="hidden">
								<div class="col-sm-4">
									<input type="text" class="form-control" id="super-phenotype" name="super-phenotype" placeholder="Some_Super_Phenotype">
								</div>
								<small class="form-text text-muted col-sm-3" id="super-phenotype-help">
									Right-Click on a phenotype on the right site and select "Set as Super-Phenotype".
								</small>
							</div>
						</div>

						<!-- Category -->
						<div class="form-group row">
							<label for="category" class="control-label col-sm-2">Category</label>
							<div class="col-sm-3">
								<select id="category" name="category" class="form-control" onchange="toggleNewCategoryField()">
									<option />
									<option value="new_category">Create new Category</option>
									<option value="Category_1">Category 1</option>
									<option value="Category_2">Category 2</option>
								</select>
							</div>
							<div id="new-category-div" class="col-sm-6 hidden">
								<input type="text" id="new-category" name="new-category" class="form-control" placeholder="New Category Name">
							</div>
						</div>

						<!-- Definition -->
						<div class="form-group row">
							<div class="col-sm-2">
								<label class="control-label">Definitions</label>
								<a class="btn btn-primary btn-xs" onclick="addRow('#definition-div')" data-toggle="tooltip" title="Add a Definition" data-placement="right">
									<i class="fa fa-plus" aria-hidden="true"></i>
								</a>
							</div>
							
							<div class="col-sm-10" id="definition-div">
								<div class="input-group hidden">
									<div class="input-group-addon">
										<select name="definition-language[]" data-toggle="tooltip" title="Language" data-placement="right">
											<option value="en">EN</option>
											<option value="de">DE</option>
											<option value="fr">FR</option>
											<option value="es">ES</option>
										</select>
									</div>
									<textarea class="form-control" name="definition[]" placeholder="Some Definition"></textarea>
								</div>
							</div>
						</div>

						<!-- Specifications -->
						<div class="form-group row">
							<!-- Datatype -->
							<label for="datatype" class="control-label col-sm-2">Datatype*</label>
							<div class="col-sm-10">
								<div class="row">
									<div class="col-sm-3">
										<select id="datatype" name="datatype" class="form-control" onchange="toggleValueDefinition()" required>
											<option />
											<option value="integer">Integer</option>
											<option value="double">Floating Number</option>
											<option value="string">String</option>
										</select>
									</div>
									<small class="form-text text-muted col-sm-5">Some description about the datatypes</small>
								</div>

								<div id="datatype-specification">
									<!-- Value definition: Enum -->
									<div id="enum-form-group" class="hidden">
										<label class="control-label">Possible Values</label>
										<a class="btn btn-primary btn-xs" onclick="addRow('#enum-form-group')">
											<i class="fa fa-plus" aria-hidden="true"></i>
										</a><br>
										<small class="form-text text-muted">
											Define specific values and add appropriate labels.
										</small>
										<div class="row hidden">
											<div class="col-sm-5">
												<input type="text" class="form-control" name="enum-value[]" placeholder="Value">
											</div>
											<div class="col-sm-1" style="margin-top:5px">=></div>
											<div class="col-sm-6">
												<input type="text" class="form-control" name="enum-label[]" placeholder="Label">
											</div>
										</div>
									</div>
	
									<!-- Value definition: UCUM -->
									<div id="ucum-form-group" class="hidden">
										<label for="ucum" class="control-label">UCUM</label>
										<input type="text" class="form-control" id="ucum" name="ucum" aria-describedby="ucum-help">
										<small id="ucum-help" class="form-text text-muted">If the numeric values do have a unit, specify it as <a href="http://unitsofmeasure.org/trac">UCUM</a>.</small>
									</div>
	
									<!-- Range specifications -->
									<div id="range-form-group" class="hidden">
										<label class="control-label">Ranges</label> 
										<a class="btn btn-primary btn-xs" onclick="addRow('#range-form-group')">
											<i class="fa fa-plus" aria-hidden="true"></i>
										</a><br>
										<small class="form-text text-muted">
											Specify min and/or max values and give the resulting intervals a label.<br>
											e.g.: >20, &le;34.5 => normal | &le;20 => too low | &gt;34.5 => too high
										</small>
	
										<div class="row hidden">
											<div class="col-sm-2" style="padding-right:0">
												<select class="form-control" name="range-min-operator[]">
													<option />
													<option value="=">=</option>
													<option value="&ge;">&ge;</option>
													<option value="&gt;">&gt;</option>
												</select>
											</div>
											<div class="col-sm-2" style="padding-left:0">
												<input type="number" step="any" class="form-control" name="range-min[]" placeholder="Min">
											</div>
											
											<div class="col-sm-2" style="padding-right:0">
												<select class="form-control" name="range-max-operator[]">
													<option />
													<option value="&lt;">&lt;</option>
													<option value="&le;">&le;</option>
													<option value="=">=</option>
												</select>
											</div>
											<div class="col-sm-2" style="padding-left:0">
												<input type="number" step="any" class="form-control" name="range-max[]" placeholder="Max">
											</div>
	
											<div class="col-sm-4">
												<input type="text" class="form-control" name="enum" placeholder="Label">
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<!-- Relations -->
						<div class="form-group row">
							<div class="col-sm-2">
								<label for="relation" class="control-label">Relations</label>
								<a class="btn btn-primary btn-xs" onclick="addRow('#relation-div')"
									data-toggle="tooltip" title="Add a relation" data-placement="right">
									<i class="fa fa-plus" aria-hidden="true"></i>
								</a>
							</div>
							<div class="col-sm-8" id="relation-div">
								<input type="text" class="form-control hidden" id="relation[]" name="relation[]" placeholder="https://example.com/foo#bar">
							</div>
						</div>

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
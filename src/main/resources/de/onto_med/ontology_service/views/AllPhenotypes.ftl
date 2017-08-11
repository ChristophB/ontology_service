<#assign title = "All Phenotypes">
<#assign current = "Phenotypes">
<#assign current_submenu = "all">
<#assign heading = "Show All Phenotypes">
<#assign subHeading ="A List of All Defined Phenotypes">

<html>
	<#include "partials/Head.ftl">
	
	<body>
		<#include "partials/Navbar.ftl">
    	<#include "partials/Heading.ftl">
		<#include "partials/phenotype/Links.ftl">
		<#include "partials/Messages.ftl">
		
    	<div class="container">
    		<div class="row">
	    		<div id="phenotype-tree" class="well col-sm-6"></div>
				
				<pre class="well col-sm-6" id="description"></pre>
			</div>
    	</div>
	    
	    <#include "partials/Footer.ftl">
	    
	    <script type="text/javascript">
	    	function customMenu(node) {
				var items = {
					getDecisionTree : {
						label  : 'Get Decision Tree',
						action : function() { window.open('${rootPath}/phenotype/decision-tree?phenotype=' + encodeURIComponent(node.a_attr.iri), '_self'); }
					},
				};
				if (node.a_attr.type != 'expression') {
					delete items.getDecisionTree;
				}
				return items;
			}

			$(document).ready(function() {
				$.getJSON('${rootPath}/phenotype/all', function(data) {
					$('#' + 'phenotype-tree').jstree({
						core : {
							multiple : false,
							data : transformToPhenotypeTree(data)
						},
						plugins : [ 'contextmenu' ],
						contextmenu : { items : customMenu }
					});
				});
				
				$('#phenotype-tree').bind('select_node.jstree', function(e, selected) {
					var id = selected.node.a_attr.id;
					
					$.getJSON('${rootPath}/phenotype/' + encodeURIComponent(id), function(json) {
						$('#description').html(JSON.stringify(json, null, 2));
					}, 'application/json');
				});
			});
		</script>
	</body>
</html>
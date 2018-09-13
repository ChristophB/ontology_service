var awesomplete;

function toggleValueDefinition() {
	$('#ucum-form-group, #is-decimal-form-group, #formula-form-group').addClass('hidden');

	if ($('#datatype').val() == 'numeric' || $('#datatype').val() == 'calculation')
		$('#ucum-form-group, #is-decimal-form-group').removeClass('hidden');
	if ($('#datatype').val() == 'calculation')
		$('#formula-form-group').removeClass('hidden');
}

function addRow(id) {
	var row = $('form:not(.hidden) ' + id + ' .hidden').clone();
	row.removeClass('hidden').addClass('generated');
	$('form:not(.hidden) ' + id).append(row);
}

function showMessage(text, state) {
	$('#message').remove();
	$('body').append(
		'<div id="message" class="alert alert-' + state + ' fade in">'
    	+ '<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>'
        	+ $('<div>').text(text).html()
        + '</div>'
    );
}

function createPhenotypeTree(id, url, withContext) {
	$('#' + id).jstree({
		core: {
			multiple: false,
			data: {
				url: url
			}
		},
		plugins: [ 'contextmenu', 'dnd' ],
		contextmenu: { items: withContext ? customMenu : null }
	});



	$(document).on('dnd_move.vakata', function (e, data) {
		var target     = $(data.event.target);
		var attributes = data.element.attributes;
		var drop       = target.closest('.drop');
		var jstreeIcon = data.helper.find('.jstree-icon');

        jstreeIcon.removeClass('jstree-ok').addClass('jstree-er');

		if (target.closest('.jstree').length || !drop.length) return; // field with class "drop" outside of jstree

		if ((attributes.type.value === "null" && drop.hasClass('category'))
		    || (attributes.type.value !== "null" && drop.hasClass('phenotype')
		        && ((drop[0].id === 'reason-form-drop-area' && attributes.isSinglePhenotype.value == "true")
			        || (drop[0].id === 'formula' && attributes.isRestricted.value == "false"
			            && ['numeric', 'calculation', 'composite-boolean'].indexOf(attributes.type.value) != -1)
			        || (drop[0].id === 'expression')
			    )
			)
		) jstreeIcon.removeClass('jstree-er').addClass('jstree-ok');
	}).on('dnd_stop.vakata', function (e, data) {
		var target     = $(data.event.target);
		var attributes = data.element.attributes;
		var drop       = target.closest('.drop');

		if (target.closest('.jstree').length || !drop.length) return; // field with class "drop" outside of jstree

		if (attributes.type.value === 'null' && drop.hasClass('category')) {
			drop.val(drop.val() + ' ' + data.element.text + ';');
			focusInputEnd(drop);
		} else if (attributes.type.value !== 'null' && drop.hasClass('phenotype')) {
			if (drop[0].id === 'reason-form-drop-area' && attributes.isSinglePhenotype.value == 'true') {
			    var pathname = window.location.pathname;

			    if (attributes.type.value === 'string' && attributes.isRestricted.value == 'false')
			        $.ajax({
			            url: pathname.replace(/\/phenotype.*/i, '') + '/phenotype/' + pathname.replace(/.*phenotype\/(.*)\/.*/i, '$1') + '/' + attributes.id.value + '/restrictions',
                        dataType: 'text',
                        contentType: 'application/json; charset=utf-8',
                        processData: false,
                        type: 'GET',
                        success: function(options) { appendFormField(data.element, drop[0], JSON.parse(options)); },
                        error: function(result) {
                            var response = JSON.parse(result.responseText);
                            showMessage(response.message, 'danger');
                        }
			        });
			    else appendFormField(data.element, drop[0]);
			} else if (drop[0].id === 'expression'
			    || (drop[0].id === 'formula' && attributes.isRestricted.value == "false"
                    && ['numeric', 'calculation', 'composite-boolean'].indexOf(attributes.type.value) != -1)
            ) {
				drop.val(drop.val() + ' ' + attributes.id.value + ' ');
				focusInputEnd(drop);
			}
		}
	});
}

function appendFormField(element, target, options = null) {
	var id         = element.attributes.id.value;
	var type       = element.attributes.type.value;
    var inputField = '';

    if (type === "string" && options != null) {
        inputField
            = '<input type="hidden" name="" id="'+ id + '_select">'
            + '<select class="form-control" onchange="$(\'#' + id + '_select\').attr(\'name\', this.value)">'
                + '<option value=""></option>';
        for (var name in options)
            inputField +=  '<option value="' + name + '">' + options[name] + '</option>';
        inputField += '</select>';
    } else {
        if (type === "numeric") type = "number";
        if (type === "string") type = "text";

        if (element.attributes.isRestricted.value === "true") {
            inputField = '<input type="hidden" name="' + id + '">';
        } else if (['boolean', 'composite-boolean'].indexOf(type) !== -1) {
            inputField
                = '<select class="form-control" name="' + id + '">'
                    + '<option value="true">True</option>'
                    + '<option value="false">False</option>'
                + '</select>';
        } else {
            inputField = '<input type="' + type + '" class="form-control" name="' + id + '">';
        }
    }

	var html
		= '<div class="form-group row generated">'
			+ '<label for="' + id + '" class="control-label col-sm-4">' + element.text + '</label>'
			+ '<div class="col-sm-6">'
				+ inputField
			+ '</div>'
			+ '<a class="btn btn-danger" href="#" onclick="$(this).parent().remove()">'
				+ '<i class="fa fa-times fa-lg"></i>'
			+ '</a>'
		+ '</div>';

	$(target).append(html);
	$('.form-control:last').focus();
}

function showPhenotypeForm(id, clear = false) {
	$('#abstract-phenotype-form, #phenotype-category-form, #numeric-phenotype-form, #string-phenotype-form, '
	    + '#date-phenotype-form, #boolean-phenotype-form, #calculation-phenotype-form, '
	    + '#composite-boolean-phenotype-form, #reason-form').addClass('hidden');

	$(id).removeClass('hidden');
	if (clear === true) clearPhenotypeFormData();

	if (id == '#reason-form') {
	    $('#edit-link').removeClass('active');
	    $('#reason-link').addClass('active');
	} else {
	    $('#reason-link').removeClass('active');
        $('#edit-link').addClass('active');
	}
}

function clearPhenotypeFormData() {
	$('#message, .generated').remove();
	$('form:not(.hidden) input[type!=checkbox].form-control, form:not(.hidden) textarea.form-control, form:not(.hidden) select').val(null);
	$('form:not(.hidden) input[type=checkbox]').removeAttr('checked');
	$('.hidden-language, form:not(.hidden) #title-languages').val('en');
	toggleValueDefinition();

    if (document.querySelector('form:not(.hidden) input.awesomplete#identifier')) {
        $.getJSON('all?type=list', function(data) {
            var input = document.querySelector('form:not(.hidden) input.awesomplete#identifier');
            if (awesomplete != undefined) awesomplete.destroy();
            awesomplete = new Awesomplete(input, { list: data });
        });
    }
}

function customMenu(node) {
	var items = {
	    inspect: {
        	label: 'Inspect',
        	icon: 'fa fa-search',
        	action: function() {
        		$.ajax({
        		    url: node.a_attr.id,
        		    datatype: 'text',
        		    type: 'GET',
        		    success: function(data) { inspectPhenotype(data); },
        		    error: function(response) { showMessage(response.responseJSON.message, 'danger'); }
        		});
        	}
        },
		showCategoryForm: {
			label: 'Create Category',
			icon: 'fa fa-plus text-secondary',
			action: function() {
				showPhenotypeForm('#phenotype-category-form', true);
				$('#super-category').val(node.a_attr.id);
			}
		},
		showAbstractPhenotypeForm: {
			label: 'Create Phenotype',
			icon: 'fa fa-plus text-primary',
			action: function() {
				showPhenotypeForm('#abstract-phenotype-form', true);
				$('form:not(.hidden) #categories').val(node.a_attr.id);
			}
		},
		showRestrictedPhenotypeForm: {
			label: 'Add Restriction',
			icon: 'fa fa-plus text-warning',
			action: function() {
				switch (node.a_attr.type) {
					case 'date': showPhenotypeForm('#date-phenotype-form', true); break;
					case 'string': showPhenotypeForm('#string-phenotype-form', true); break;
					case 'numeric': showPhenotypeForm('#numeric-phenotype-form', true); break;
					case 'boolean': showPhenotypeForm('#boolean-phenotype-form', true); break;
					case 'composite-boolean': showPhenotypeForm('#composite-boolean-phenotype-form', true); break;
					case 'calculation': showPhenotypeForm('#calculation-phenotype-form', true); break;
					default: return;
				}
				$('form:not(.hidden) #super-phenotype').val(node.a_attr.id);
			}
		},
		getDecisionTreePng: {
			label: 'Get Decision Tree As PNG',
			icon: 'fa fa-file-image-o',
			action: function() {
				window.open('decision-tree?phenotype=' + node.a_attr.id + '&format=png', '_blank').focus();
			}
		},
		getDecisionTreeGraphml: {
			label: 'Get Decision Tree As GraphML',
			icon: 'fa fa-file-text-o',
			action: function() {
				window.open('decision-tree?phenotype=' + node.a_attr.id + '&format=graphml', '_blank').focus()
			}
		},
		showReasonForm: {
		    label: 'Show Reason Form',
		    icon: 'fa fa-comment-o',
		    action: function() {
		        window.open('reason-form/' + node.a_attr.id, '_self').focus();
		    }
		},
		delete: {
			label: 'Delete',
			icon: 'fa fa-trash-o text-danger',
			action: function() {
			    $.ajax({
			        url: node.a_attr.id + "/dependents",
			        dataType: 'json',
			        success: function(data) {
                        $('#deletePhenotypeTable').bootstrapTable('load', data);
                        $('#deletePhenotypeTable').bootstrapTable('checkAll', true);
                        $('#deletePhenotypeModal').modal('show');
                    },
                    error: function(data) { showMessage(data.responseJSON.message, 'danger'); }
                });
			}
		}
	};

	if (!node.a_attr.isPhenotype) {
		delete items.showRestrictedPhenotypeForm;
		delete items.showReasonForm;
		delete items.getDecisionTreeGraphml;
		delete items.getDecisionTreePng;
	} else {
		delete items.showCategoryForm;
		delete items.showAbstractPhenotypeForm;
	}
	if (node.a_attr.isRestricted) {
		delete items.getDecisionTreePng;
		delete items.getDecisionTreeGraphml;
	}
	if (node.a_attr.id === 'Phenotype_Category') {
		delete items.delete;
		delete items.inspect;
	}

	return items;
}

function deletePhenotypes() {
    var deletions = [];
    $('#deletePhenotypeTable').bootstrapTable('getSelections').forEach(function(phenotype) {
        deletions.push(phenotype.name);
    });

    $.ajax({
        url: 'delete-phenotypes',
        dataType: 'text',
        contentType: 'application/json',
        processData: false,
        type: 'POST',
        data: JSON.stringify(deletions),
        success: function(result) {
            $('#phenotype-tree').jstree('refresh');
            $('#deletePhenotypeModal').modal('hide');
            showMessage(result, 'success');
        },
        error: function(result) {
            $('#deletePhenotypeModal').modal('hide');
            showMessage(result.responseText, 'danger');
        }
    });
}

function focusInputEnd(input) {
	var length = input.val().length * 2;
	input.focus();
	input[0].setSelectionRange(length, length);
}

function inspectIfExists(id) {
	$.getJSON(id, function(data) { if (data != undefined) inspectPhenotype(data); });
}

function inspectPhenotype(data) {
	clearPhenotypeFormData();

	var form;
	if (data.abstractPhenotype === true) {
		form = '#abstract-phenotype-form';
		$(form + ' #ucum').val(data.unit);
		$(form + ' #datatype').val(getDatatype(data));
		$(form + ' #is-decimal')[0].checked = (data.datatype == 'XSD_DOUBLE');
		$(form + ' #formula')[0].value = data.formula;

		toggleValueDefinition();
	} else if (data.restrictedPhenotype === true) {
		switch (getDatatype(data)) {
			case 'date':    form = '#date-phenotype-form';    break;
			case 'string':  form = '#string-phenotype-form';  break;
			case 'numeric': form = '#numeric-phenotype-form'; break;
			case 'boolean': form = '#boolean-phenotype-form'; break;
			case 'composite-boolean':
				form = '#composite-boolean-phenotype-form';
				$(form + ' #expression').val(data.manchesterSyntaxExpression); // TODO: print original string
				$(form + ' #score').val(data.score);
				break;
			case 'calculation': form = '#calculation-phenotype-form'; break;
		}
		$(form + ' #super-phenotype').val(data.abstractPhenotypeName);
	} else {
    	form = '#phenotype-category-form';
    }

	showPhenotypeForm(form);

	var counter = 1;
	for (var lang in data.titles) {
		var title = data.titles[lang];

		if (counter == 1) {
			$(form + ' #title-div #title-languages').val(lang);
            $(form + ' #title-div .input-group:not(.hidden):first input[type=text]#titles').val(title.titleText);
            if (title.alias != null) $(form + ' #title-div .input-group:not(.hidden):first input[type=text]#aliases').val(title.alias);
		} else {
			addRow('#title-div');
			$(form + ' #title-div .generated:last select').val(lang);
			$(form + ' #title-div .generated:last input[type=text]#titles').val(title.titleText);
			if (title.alias != null) $(form + ' #title-div .generated:last input[type=text]#aliases').val(title.alias);
		}
		counter++;
    }

	$(form + ' #categories').val(data.phenotypeCategories !== undefined ? data.phenotypeCategories.join('; ') : null);

	for (var lang in data.labels) {
		data.labels[lang].forEach(function(label) {
			addRow('#label-div');
			$(form + ' #label-div .generated:last select').val(lang);
			$(form + ' #label-div .generated:last input[type=text]').val(label);
		});
	}
	for (var lang in data.descriptions) {
		data.descriptions[lang].forEach(function(description) {
			addRow('#description-div');
            $(form + ' #description-div .generated:last select').val(lang);
            $(form + ' #description-div .generated:last textarea').val(description);
		});
    }
	data.relatedConcepts.forEach(function(relation) {
    	addRow('#relation-div');
        $(form + ' #relation-div input[type=text].generated:last').val(relation);
    });
    addRange(form, data.phenotypeRange);

    if (data.score != undefined) $(form + ' #score').val(data.score);
}

function addRange(form, range) {
	if (!range) return;

	var asDate = range.dateValue || range.dateValues || range.dateRange;

    var value       = range.stringValue || range.dateValue || range.integerValue || range.doubleValue;
    var values      = range.stringValues || range.dateValues || range.integerValues || range.doubleValues;
    var rangeValues = range.dateRange || range.integerRange || range.doubleRange;

	if (value) {
		addEnumFieldWithValue(form, convertValue(value, asDate));
	} else if (values) {
		values.forEach(function(value) {
			addEnumFieldWithValue(form, convertValue(value, asDate));
		});
	} else if (rangeValues) {
		if (rangeValues.MIN_INCLUSIVE) {
			$(form + ' #range-min-operator').val('>=');
			$(form + ' #range-min').val(convertValue(rangeValues.MIN_INCLUSIVE, asDate));
		} else if (rangeValues.MIN_EXCLUSIVE) {
			$(form + ' #range-min-operator').val('>');
            $(form + ' #range-min').val(convertValue(rangeValues.MIN_EXCLUSIVE, asDate));
		}
		if (rangeValues.MAX_INCLUSIVE) {
           	$(form + ' #range-max-operator').val('<=');
           	$(form + ' #range-max').val(convertValue(rangeValues.MAX_INCLUSIVE, asDate));
        } else if (rangeValues.MAX_EXCLUSIVE) {
            $(form + ' #range-max-operator').val('<');
           	$(form + ' #range-max').val(convertValue(rangeValues.MAX_EXCLUSIVE, asDate));
        }
	}
}

function convertValue(value, asDate) {
	return asDate ? new Date(value).toISOString().substring(0, 10) : value;
}

function addEnumFieldWithValue(form, value) {
	addRow('#enum-form-group');
    $(form + ' #enum-form-group .generated:last input[type=text]').val(value);
}

function getDatatype(data) {
	if (data.abstractBooleanPhenotype === true || data.restrictedBooleanPhenotype === true) {
		return "composite-boolean";
    } else if (data.abstractCalculationPhenotype === true || data.restrictedCalculationPhenotype === true) {
    	return "calculation";
    } else if (data.datatype == 'XSD_STRING') {
    	return "string";
    } else if (data.datatype == 'XSD_DATE_TIME' || data.datatype == 'XSD_LONG') {
    	return "date";
    } else if (data.datatype == 'XSD_INTEGER' || data.datatype == 'XSD_DOUBLE') {
    	return "numeric";
    } else if (data.datatype == 'XSD_BOOLEAN') {
    	return "boolean";
    }
}
<form id="reason-form" class="hidden" action="" method="post" onSubmit="return false">
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
		<div id="reason-form-drop-area" class="panel-body drop phenotype"></div>
	</div>
</form>

<script type="text/javascript">
$(document).ready(function() {
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

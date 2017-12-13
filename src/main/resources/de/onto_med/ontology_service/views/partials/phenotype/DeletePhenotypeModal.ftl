<div id="deletePhenotypeModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Delete Phenotypes</h4>
            </div>
            <div class="modal-body">
                <p>
                    There are some phenotypes which are connected to the phenotype you want to delete.<br>
                    Please de-select all phenotypes from the list below to exclude them from deletion.
                </p>
                <form onsubmit="return false;">
                    <table id="deletePhenotypeTable" data-toggle="table" data-search="true" data-click-to-select="true">
                        <thead>
                            <tr>
                                <th data-field="delete" data-checkbox="true"></th>
                                <th data-visible="false" data-field="name">Phenotype</th>
                                <th data-sortable="true" data-field="titleText">Phenotype</th>
                                <th data-sortable="true" data-field="datatypeText">Data Type</th>
                            </tr>
                        </thead>
                    </table>
                    <br>
                    <button class="btn btn-warning" onclick="deletePhenotypes()">Delete</button>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>
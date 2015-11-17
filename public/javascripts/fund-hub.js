var hub = (function(){
    var $createFundForm = $('#createFundForm');
    var $newFundName = $($createFundForm.find('input')[1]);
    var $newFundUrl = $($createFundForm.find('input')[2]);

    function createFund(){
        if($newFundName.val() == '' || $newFundUrl.val() == ''){
            alert('Completar todos los campos');
        } else {
            $createFundForm.submit();
        }
    }

    return {
        createFund : createFund
    }
})();

$(document).ready(updateFunds);

function updateFunds(){
    $.get("/api/fund/list").done(renderFunds);
}

function renderFunds(funds){
    var template = $('#template').html();
    Mustache.parse(template);   // optional, speeds up future uses
    var rendered = Mustache.render(template, {funds: funds});
    $("#fund-table").find("tbody").html(rendered);
}

function search(){
    var rows = $("#fund-table").find("tbody tr");
    rows.hide();
    var searchText = $("#search").val().toLowerCase();
    rows.each(function(){
        var $this = $(this);
        var name = $this.find("td:first").text().toLowerCase();
        var url = $this.find("td:nth-child(2)").text().toLowerCase();
        if(name.indexOf(searchText) != -1 || url.indexOf(searchText) != -1){
            $this.show();
        }
    });
}

function updateLavca(){
    $.get("/api/fund/lavca").done(renderFunds);
}

function updateEmpea(){
    $.get("/api/fund/empea").done(renderFunds);
}

$("#search").keyup(search);

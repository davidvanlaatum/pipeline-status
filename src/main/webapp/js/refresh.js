jQuery(document).ready(function () {

    function updateStatusVars() {
        jQuery.ajax({
            url: "pipeline-status/ajax",
            cache: false,
            dataType: "html"
        }).done(function (html) {
            jQuery("#pipeline_status_vars").empty().append(html);
        });
    }

    window.setInterval(function () {
        updateStatusVars();
    }, 5000);
});

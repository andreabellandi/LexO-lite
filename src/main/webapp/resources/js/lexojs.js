/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function setHeight() {

    var height = $(document.getElementById('content')).height();

    $(document.getElementById('editViewTab:editTabScroll')).css('height', height - 250);
    $(document.getElementById('editViewTab:dictViewTabScroll')).css('height', height - 250);
    $(document.getElementById('editViewTab:scrollPaneldetailViewTab')).css('height', height - 250);
    $(document.getElementById('lexiconTabViewForm:tabView:scrollPanelLemmaTree')).css('height', height - 270);
    $(document.getElementById('lexiconTabViewForm:tabView:scrollPanelFormTree')).css('height', height - 270);
    $(document.getElementById('lexiconTabViewForm:tabView:scrollPanelOntologyTree')).css('height', height - 300);

}
;

//window.addEventListener('resize', setHeight);
window.onresize = setHeight;
window.onload = setHeight;


$(document).ready(function () {});

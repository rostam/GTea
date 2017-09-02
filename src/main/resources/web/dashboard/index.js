var serverAddr = "http://localhost:2342/";
var nodeId = -1;
var edgeId = -1;
var cy; //cytoscape object
var selectedNode;

initCytoscape();
$( document ).ready(function() {
    //initCytoscape();
});


var original_data = {};
$.get(serverAddr + 'graphs/')
    .done(function (data) {
        original_data = data;
        var categoriesSelect = $('#categories');
        data.graphs.forEach(function (d) {
            categoriesSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = d.properties;
        });
        categoriesSelect.on('change', function () {
            var category = getSelectedCategory();
            var keys = "", vals = "";
            original_data[category].forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys += propNamesTypes[0] + ", ";
                vals += propNamesTypes[1] + ", ";
            });
            keys = keys.substr(0, keys.length - 2);
            vals = vals.substr(0, vals.length - 2);
            $('#props_keys').html(keys);
            $('#props_vals').val(vals);
        });

        var reportsSelect = $('#reports');
        data.reports.forEach(function (d) {
            reportsSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = d.properties;
        });
        reportsSelect.on('change', function () {
            var report = getSelectedReport();
            var props = $('#reportProps');
            props.empty();
            var keys="",vals="";
            original_data[report].forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys+=propNamesTypes[0]+", ";vals+=propNamesTypes[1]+", ";
            });
            keys=keys.substr(0,keys.length-2);
            vals=vals.substr(0,vals.length-2);
            if(keys=="") props.append("No parameters<br/>");
            else props.append('<span id="reportPropsKeys">' +keys + '</span>: ' +
                '<input id="reportPropsVals"' + 'name="' + keys + '"' + ' value="' + vals + '">');
        });
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

function Report() {
    var reportProps = "";
    $('#reportProps').children('input').each(function (i, item) {
        reportProps += item.name + ":" + item.value + "-"
    });
    if (reportProps == "") {
        reportProps = "no";
    }
    $.get(serverAddr + 'report/'
        + $('#categories').find('option:selected').text() + "--"
        + $('#reports').find('option:selected').text() + "--"
        + ($('#props_keys').html() + ":" + $('#props_vals').val()) + "--"
        + ($('#reportPropsKeys').html() + ":" + $('#reportPropsVals').val()))
        .done(function (data) {
            $('#reportResults').html(JSON.stringify(data));
//                $('#results-body').html(JSON.stringify(data));
            if (data.titles != undefined) {
                var titles = data.titles.substr(1, data.titles.indexOf("]") - 1);
                var tts = titles.split(",");
                var builtHTML = "<table><tr>";
                tts.forEach(function (t) {
                    builtHTML += "<th>" + t + "</th>";
                });
                var results = JSON.parse(data.results);
                builtHTML += "</tr>";
                results.forEach(function (row) {
                    builtHTML += "<tr>";
                    row.forEach(function (col) {
                        builtHTML += "<td>" + col + "</td>";
                    })
                    builtHTML += "</tr>";
                });
                builtHTML += "</tr></table>";
                $('#results-body').html(builtHTML);
            }

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}


function initCytoscape() {
     cy = cytoscape({
        container: document.getElementById('canvas'),
        style: [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': 'lightgray',
                    'label': 'data(id)',
                    'text-valign': 'center'
                }
            }]
    });
}

function addSingleVertex() {
    var offset = $('#canvas').offset();
    var xPos = event.pageX - offset.left;
    var yPos = event.pageY - offset.top;
    $.get(serverAddr + 'addVertex/'
    + nodeId++ + "--" + xPos + "--" + yPos)
    .done(function (data) {
        cy.add({
            data: {id: nodeId},
            renderedPosition: {x: xPos, y: yPos}
        });
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

}

function removeSingleVertex() {
    console.log("drawing single vertex");
}

function addSingleEdge(source, target) {
    $.get(serverAddr + 'addEdge/'
        + source + "--" + target)
        .done(function (data) {
            var edges = data.edges;
            var nodes = data.nodes;
            cy.elements().remove();
            cy.add(nodes);
            cy.add(edges);
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
    console.log("drawing single edge");
}

function deleteSingleEdge() {
    console.log("drawing single vertex");
}

function Draw() {
    var lay = $('#layouts').find('option:selected').text();
    if (lay == "Botanical Tree") {
        drawBotanical();
        return;
    }
    $.get(serverAddr + 'draw/'
        + $('#categories').find('option:selected').text() + "--"
        + $('#reports').find('option:selected').text() + "--" +
        ($('#props_keys').html() + ":" + $('#props_vals').val()))
        .done(function (data) {
            nodeId = 0; //resets counter for freehand vertices
            var nodes = data.nodes;
            var edges = data.edges;
            cy.elements().remove();
            cy.add(nodes);
            cy.add(edges);
            nodeId += nodes.length-1; //adds the current amount of nodes, so the next freehand item will be max(ids)+1
            if (lay == "Preset") {
                cy.layout({name: 'preset'}).run();
            } else if (lay == "Force Directed") {
                cy.layout({name: 'cose'}).run();
            }
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

cy.on('tap', function(event) {
    var evtTarget = event.target;

    if (evtTarget === cy) {
        addSingleVertex(event);
    }
    else if (evtTarget.isNode()) {
        if (selectedNode == null) {
            selectedNode = evtTarget;
        }
        else {
            console.log(selectedNode.data('id'), evtTarget.data('id'));
            addSingleEdge(selectedNode.data('id'), evtTarget.data('id'));
            selectedNode = null;
        }
        console.log("Clicked a node");
    }
    else if (evtTarget.isEdge()) {

    }


});

/*$('#canvas').on('mousedown', function(event) {
    $('#canvas').on('mouseup mousemove', function handler(event){
        if (event.type == 'mouseup') {
            // click
            addSingleVertex(event);
        } else {
            // drag
        }
        $("#canvas").off('mouseup mousemove', handler);
    });
});*/

function getSelectedCategory() {
    return $('#categories').find('option:selected').text();
}

function getSelectedReport() {
    return $('#reports').find('option:selected').text();
}

/**
 * runs a BFS on graph, starting the given vertex as the root
 */
function BFSrun(treeRoot, f) {
    var q = [], ret = [];
    q.push(treeRoot);
    ret.push(treeRoot);
    treeRoot.setMark = true;
    treeRoot.obj = 0;
    while (q.length != 0) {
        var v = q.splice(0, 1)[0];
        v.neighborhood().forEach(function (vertex) {
            if (vertex.setMark == undefined || vertex.setMark == false) {
                vertex.setMark = true;
                vertex.obj = v.obj + 1;
                q.push(vertex);
                ret.push(vertex);
                f(vertex, v);
            }
        });
    }
    return ret;
}

function getAngle(p1, p2) {
    var angle = Math.atan2(p1.y - p2.y,
        p1.x - p2.x);
    if (angle < 0) {
        // atan2 returns getAngle in phase -pi to pi, which means
        // we have to convert the answer into 0 to 2pi range.
        angle += 2 * Math.PI;
    }
    return angle;
}

function getMiddlePoint(p1, p2) {
    return {x: (p1.x + p2.x) / 2, y: (p1.y + p2.y) / 2};
}

function sizeOfIntersectionOfArrays(arr1, arr2) {
    var cnt = 0;
    if (arr1.indexOf(arr2[0]) == -1) cnt++;
    if (arr1.indexOf(arr2[1]) == -1) cnt++;
    if (arr1.indexOf(arr2[2]) == -1) cnt++;
    return cnt;
}

$('#generators').show();
$('#g6format').hide();
function selectLoader() {
    var loader = $('#loaders ').find('option:selected').text();
    switch (loader) {
        case "Generators":
            $('#generators').show();
            $('#g6format').hide();
            break;
        case "Edge list":
        case "Adjacency matrix":
        case "G6 format":
            $('#generators').hide();
            $('#g6format').show();
            break;
    }
}

function loadG6() {
    $.get(serverAddr + 'g6/'+$('#g6string').val())
        .done(function (data) {

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}
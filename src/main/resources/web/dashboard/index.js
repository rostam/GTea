var serverAddr = "http://127.0.0.1:2342/"; //"http://0.0.0.0:2342/";
var nodeId = 0;
var cy; //cytoscape object
var selectedNode;
var uuid = guid();
var directed = 'triangle', undirected = 'none';
//var parallels = [];
var report_results;

initCytoscape(undirected, serverAddr, uuid);

var original_data = {};
$.get(serverAddr + 'graphs/')
    .done(function (data) {
        original_data = data;
        var categoriesSelect = $('#categories');
        data.graphs.forEach(function (d) {
            categoriesSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = {desc: d.desc, props: d.properties};
        });
        categoriesSelect.on('change', function () {
            var category = getSelectedCategory();
            var keys = "", vals = "";
            original_data[category].props.forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys += propNamesTypes[0] + ", ";
                vals += propNamesTypes[1] + ", ";
            });
            keys = keys.substr(0, keys.length - 2);
            vals = vals.substr(0, vals.length - 2);
            $('#props_keys').html(keys);
            $('#props_vals').val(vals);

            var desc = original_data[category].desc;
            $('#tooltiptext').html(desc);
            var linkName = category;
            var ind = category.indexOf("Generator");
            if(ind != -1) linkName = category.substr(0,ind);
            ind = category.indexOf("Graph");
            if(ind == -1) linkName += "Graph";
            linkName += ".html";
            $('#gen_link').attr('href','http://mathworld.wolfram.com/'+linkName);
        });

        var reportsSelect = $('#reports');
        data.reports.forEach(function (d) {
            reportsSelect.append('<option>' + d.name + '</option>');
            original_data[d.name] = {desc: d.desc, props: d.properties};
        });
        reportsSelect.on('change', function () {
            var report = getSelectedReport();
            var props = $('#reportProps');
            props.empty();
            var keys="",vals="";
            original_data[report].props.forEach(function (d) {
                var propNamesTypes = d.split(":");
                keys+=propNamesTypes[0]+", ";vals+=propNamesTypes[1]+", ";
            });
            keys=keys.substr(0,keys.length-2);
            vals=vals.substr(0,vals.length-2);
            if(keys=="") props.append("No parameters<br/>");
            else props.append('<span id="reportPropsKeys">' +keys + '</span>: ' +
                '<input id="reportPropsVals"' + 'name="' + keys + '"' + ' value="' + vals + '">');

            var desc = original_data[report].desc;
            $('#tooltiptextReport').html(desc);
        });
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

function Report() {
    $('#reportResults').html('computing...');
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
        + ($('#reportPropsKeys').html() + ":" + $('#reportPropsVals').val())
        + "--" + uuid)
        .done(function (data) {
            report_results = data;
            if (data.titles != undefined) {
                $('#reportResults').html(JSON.stringify(data));
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
            } else {
                var res = "";
                Object.keys(data).forEach(function (t) {
                    res+= t + ":" + JSON.stringify(data[t]) + ",";
                });
                res = res.substr(0,res.length-1);
                $('#reportResults').html(res);
                $('#results-body').html(res);
            }

        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            alert(errorThrown);
        });
}

function load_generator(isDraw) {
    var lay = $('#layouts').find('option:selected').text();
    var type = $('#graphType').find('option:selected').text();
    if (lay == "Botanical Tree") {
        drawBotanical();
        return;
    }
    server(serverAddr + 'draw/'
        + $('#categories').find('option:selected').text() + "--"
        + $('#reports').find('option:selected').text() + "--" +
        ($('#props_keys').html() + ":" + $('#props_vals').val())
        + "--" + type
        + "--" + uuid,function (data) {
            if (isDraw) {
                nodeId = 0; //resets counter for freehand vertices
                var nodes = data.nodes;
                var edges = data.edges;
                cy.elements().remove();
                cy.add(nodes);
                cy.add(edges);
                nodeId += nodes.length; //adds the current amount of nodes, so the next freehand item will be max(ids)+1
                setVertexIds();
                applyLayout();
            }
        })
}

cy.on('tap', function(event) {
    var evtTarget = event.target;

    if(evtTarget === selectedNode) {
        // If node is already selected, deselect the node
        cy.$('#'+selectedNode.data('id')).classes('node');
        selectedNode = null;
        return;
    }

    var loader = $('#loaders').find('option:selected').text();
    if(loader == "Freehand drawing") {
        if (evtTarget === cy) {
            addSingleVertex(event);
        }
        else if (evtTarget.isNode()) {
            if (selectedNode == null) {
                // Update the selectedNode and change the color of it
                selectedNode = evtTarget;
                cy.$('#' + selectedNode.data('id')).classes('selected');

            }
            else {
                // Adds an edge between the selected node and the newly selected node.
                // Resets the color.
                addSingleEdge(selectedNode.data('label'), evtTarget.data('label'));
                cy.$('#' + selectedNode.data('label')).classes('node');

                selectedNode = null;
            }
        }
    }
});

cy.on('cxttapend', 'node', function(event) {
    var evtTarget = event.target;
    removeSingleVertex(evtTarget);

});

cy.on('cxttapend', 'edge', function(event) {
    var evtTarget = event.target;
    removeSingleEdge(evtTarget);
});

cy.on('layoutstop', function() {
    cy.maxZoom(2.5);
    cy.fit();
    cy.maxZoom(100);
});

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

$('.loaders').hide();
$('#generators').show();

function selectLoader() {
    var loader = $('#loaders').find('option:selected').text();
    $('.loaders').hide();
    switch (loader) {
        case "Generators":
            $('#generators').show();
            break;
        case "Edge list":
            $('#elformat').show();
            break;
        case "Adjacency matrix":
            $('#adjMatformat').show();
            break;
        case "G6 format":
            $('#g6format').show();
            break;
        case "File":
            $('#fileformat').show();
            break;
        case "Freehand drawing":
            $('#freehandformat').show();
    }
}

function load_graph(type,isDraw) {
    var str = $('#' + type + 'string').val().replace(/\n/g, "-");
    var isDirected = $('#graphType').find('option:selected').text();
    if (type == "g6") {
        var str2 = "";
        for (var i = 0; i < str.length; i++)
            if (str[i] == "?") str2 += "qqq";
            else str2 += str[i];
        str = str2;
    }
    server(serverAddr + 'loadGraph' + '/' + type + "--"
        + str + "--" + isDirected + "--" + uuid, function (data) {
        if (isDraw) {
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
            var nodes = data.nodes;
            var edges = data.edges;
            cy.elements().remove();
            cy.add(nodes);
            cy.add(edges);
            cy.layout({name: 'cose'}).run();
        }
    });
}

function showOnGraph() {
    if (report_results.colors != undefined) {
        var colors = report_results.colors;
        cy.nodes().forEach(function (n) {
            var color = colors[parseInt(n.id())];
            var actualColor = distinctColors[Object.keys(distinctColors)[color]];
            n.style('background-color', actualColor);
            n.style('background-opacity', 0.6);
        });
        cy.nodes().style('background-opacity',0.6);
    }
}


// function selectGenerator() {
//     var desc = original_data[$('#categories').find('option:selected').text()].desc;
//     $('#tooltiptext').html(desc);
// }









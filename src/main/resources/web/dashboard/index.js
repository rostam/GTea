var serverAddr = "http://localhost:2342/";

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
            original_data[report].forEach(function (d) {
                var propNamesTypes = d.split(":");
                props.append(propNamesTypes[0]
                    + ': <input ' + 'name="' + propNamesTypes[0] + '"' + ' value="' + propNamesTypes[1] + '"><br/>');
            })
        });
    })
    .fail(function (jqXHR, textStatus, errorThrown) {
        alert(errorThrown);
    });

function Report() {
    var properties = "";
    $('#props').children('input').each(function (i, item) {
        properties += item.name + ":" + item.value + "-"
    });
    var reportProps = "";
    $('#reportProps').children('input').each(function (i, item) {
        reportProps += item.name + ":" + item.value + "-"
    });
    if (reportProps == "") {
        reportProps = "no";
    }
    properties = properties.substr(0, properties.length - 1);
    $.get(serverAddr + 'report/'
        + $('#categories').find('option:selected').text() + ","
        + $('#reports').find('option:selected').text() + "," + properties + "," + reportProps)
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

var cy;

function DoActions() {
    var lay = $('#layouts').find('option:selected').text();
    if (lay == "Botanical Tree") {
        var maxBetweennessIndex = 0;
        var res = $('#reportResults').html();
        if (res != "") {
            var arrBetweenness = JSON.parse(res);
            var max = Math.max(...arrBetweenness
        )
            ;
            maxBetweennessIndex = arrBetweenness.indexOf(max);
        } else {
            maxBetweennessIndex = 10;
        }
//            cy.fit();
        cy.nodes().forEach(function (n) {
            n.style('width', '20');
            n.style('height', '20');
            n.style('opacity', '0.4');
        });
        cy.edges().forEach(function (e) {
            e.style('opacity', '0.4');
        });
        //toundirected();
        var parent = {};
        selectedNode = cy.$('#' + maxBetweennessIndex);//cy.$(':selected');
        BFSrun(selectedNode, function (v, p) {
            parent[v.data('id')] = p;
        });
        var layer = cy.cyCanvas({
            zIndex: 1,
            pixelRatio: "auto"
        });
        var canvas = layer.getCanvas();
        var ctx = canvas.getContext('2d');
        cy.on("render cyCanvas.resize", function (evt) {
            layer.resetTransform(ctx);
            layer.clear(ctx);

            // Draw fixed elements
            // ctx.fillRect(0, 0, 100, 100); // Top left corner
            layer.setTransform(ctx);

            paper.setup(document.getElementById("paperCanvas"));
//                var path = new paper.Path();
//                console.log(path);
//                path.strokeColor = 'black';
//                var start = new paper.Point(100, 100);
//                path.moveTo(start);
//                path.lineTo(start.add([ 200, -50 ]));
//                paper.view.draw();
//                return;
            var paths = [];
            var startEnds = {};
            cy.nodes().forEach(function (v) {
                v1 = parent[v.data('id')];
                if (v1 == undefined) return;
                v2 = parent[v1.data('id')];
                if (v2 == undefined) return;
                p1 = v.position();
                p2 = v1.position();
                p3 = v2.position();

                m1 = getMiddlePoint(p1, p2);
                m2 = getMiddlePoint(p2, p3);
                var cp = p2;

                teta1 = getAngle(p1, p2);
                teta2 = getAngle(p1, p3);
                teta3 = getAngle(p2, p3);

                startWidth = 1;
                middleWidth = 1;
                endWidth = 1;

                pp1 = {x: m1.x - startWidth * Math.sin(teta1), y: m1.y + startWidth * Math.cos(teta1)};
                pp2 = {x: cp.x - middleWidth * Math.sin(teta2), y: cp.y + middleWidth * Math.cos(teta2)};
                pp3 = {x: m2.x - endWidth * Math.sin(teta3), y: m2.y + endWidth * Math.cos(teta3)};

                var path = new paper.Path();
                var start = new paper.Point(pp1.x, pp1.y);
                var handle = new paper.Point(pp2.x, pp2.y);
                var to = new paper.Point(pp3.x, pp3.y);
                path.moveTo(start);
                path.quadraticCurveTo(handle, to);
                var vec = [];
                vec[0] = v.data('id');
                vec[1] = v1.data('id');
                vec[2] = v2.data('id');
                vec.sort();
                paths.push({path: path, vers: vec});
                startEnds[pp1.x + "," + pp1.y] = 0;
                startEnds[pp3.x + "," + pp3.y] = 0;
//                    path.strokeColor = "green";
//                    path.view.draw();

                ctx.beginPath();
                ctx.moveTo(pp1.x, pp1.y);
                ctx.quadraticCurveTo(pp2.x, pp2.y, pp3.x, pp3.y);
                ctx.strokeStyle = "green";
                ctx.lineWidth = 1;
                ctx.stroke();
            });
//                paper.zoom(0.2);
            var cnt = 0;
            var uniqueIntersections = {};
            for (var i = 0; i < paths.length; i++) {
                for (var j = i + 1; j < paths.length; j++) {
//                        paths[i].getTangentAt(paths[i].getOffsetOf())
//                        console.log(intersectionOfArrays(paths[i].vers,paths[j].vers));
//                        if(sizeOfIntersectionOfArrays(paths[i].vers,paths[j].vers) < 2) {
                    var intersections = paths[i].path.getIntersections(paths[j].path);
                    cnt += intersections.length;
                    for (var k = 0; k < intersections.length; k++) {
                        var pp = intersections[k].point;
                        if (pp != undefined) {
                            if (uniqueIntersections[pp.x + "," + pp.y] == undefined)
                                uniqueIntersections[pp.x + "," + pp.y] = 0;
                            uniqueIntersections[pp.x + "," + pp.y]++;
                            //ctx.strokeStyle = "red";
                            //ctx.stroke();
                        }
                    }
//                        }
                }
            }
            console.log(cnt);
            var cnt2 = 0;
            Object.keys(uniqueIntersections).forEach(function (t) {
                if (uniqueIntersections[t] == 1) {
                    var xy = t.split(",");
                    var isStartEnd = false;
                    Object.keys(startEnds).forEach(function (t2) {
                        var xy2 = t2.split(",");
                        var x2 = parseFloat(xy2[0]);
                        var y2 = parseFloat(xy2[1]);
                        var x1 = parseFloat(xy[0]);
                        var y1 = parseFloat(xy[1]);
                        var dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                        if (dist < 1) isStartEnd = true;
                    });
                    if (!isStartEnd) {
                        ctx.beginPath();
                        ctx.arc(parseFloat(xy[0]), parseFloat(xy[1]), 2, 0, 2 * Math.PI);
                        ctx.fill();
                        cnt2++;
                    }
                }
            });
            console.log(cnt2);
            first = false;
        });
        return;
    }
    var properties = "";
    $('#props').children('input').each(function (i, item) {
        properties += item.name + ":" + item.value + "-"
    });
    properties = properties.substr(0, properties.length - 1);
    $.get(serverAddr + 'draw/'
        + $('#categories').find('option:selected').text() + ","
        + $('#reports').find('option:selected').text() + "," + properties)
        .done(function (data) {
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

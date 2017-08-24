/**
 * Created by rostam on 11.07.17.
 */

var preset = {
    name: 'preset',

    // called on `layoutready`
    ready: function () {
        cy.edges().forEach(function (e) {
            e.style("opacity", getEdgeOpacity());
        });
    },

    // called on `layoutstop`
    stop: function () {
    },

    // whether to animate while running the layout
    animate: false,

    // number of iterations between consecutive screen positions update (0 ->
    // only updated on the end)
    refresh: 4,

    // whether to fit the network view after when done
    fit: true,

    // padding on fit
    padding: 30,

    // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
    boundingBox: undefined,

    // whether to randomize node positions on the beginning
    randomize: false,

    // whether to use the JS console to print debug messages
    debug: false,

    // node repulsion (non overlapping) multiplier
    nodeRepulsion: 8000000,

    // node repulsion (overlapping) multiplier
    nodeOverlap: 10,

    // ideal edge (non nested) length
    idealEdgeLength: 1,

    // divisor to compute edge forces
    edgeElasticity: 100,

    // nesting factor (multiplier) to compute ideal edge length for nested edges
    nestingFactor: 5,

    // gravity force (constant)
    gravity: 250,

    // maximum number of iterations to perform
    numIter: 100,

    // initial temperature (maximum node displacement)
    initialTemp: 200,

    // cooling factor (how the temperature is reduced between consecutive iterations
    coolingFactor: 0.95,

    // lower temperature threshold (below this point the layout will end)
    minTemp: 1.0
};

var cose = {
    name: 'cose',

    // called on `layoutready`
    ready: function () {
    },

    // called on `layoutstop`
    stop: function () {
    },

    // whether to animate while running the layout
    animate: false,

    // number of iterations between consecutive screen positions update (0 ->
    // only updated on the end)
    refresh: 4,

    // whether to fit the network view after when done
    fit: true,

    // padding on fit
    padding: 30,

    // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
    boundingBox: undefined,

    // whether to randomize node positions on the beginning
    randomize: true,

    // whether to use the JS console to print debug messages
    debug: false,

    // node repulsion (non overlapping) multiplier
    nodeRepulsion: 8000000,

    // node repulsion (overlapping) multiplier
    nodeOverlap: 10,

    // ideal edge (non nested) length
    idealEdgeLength: 1,

    // divisor to compute edge forces
    edgeElasticity: 100,

    // nesting factor (multiplier) to compute ideal edge length for nested edges
    nestingFactor: 5,

    // gravity force (constant)
    gravity: 10,

    // maximum number of iterations to perform
    numIter: parseInt(getIterationLayout()),

    // initial temperature (maximum node displacement)
    initialTemp: 200,

    // cooling factor (how the temperature is reduced between consecutive iterations
    coolingFactor: 0.95,

    // lower temperature threshold (below this point the layout will end)
    minTemp: 1.0
};

var random = {
    name: 'random',
    fit: false, // whether to fit to viewport
    padding: 30, // fit padding
    boundingBox: {x1: 0, y1: 0, w: 5000, h: 5000}, // constrain layout bounds; { x1, y1, x2, y2 }
    // or { x1, y1, w, h }
    animate: false, // whether to transition the node positions
    animationDuration: 0, // duration of animation in ms if enabled
    animationEasing: undefined, // easing of animation if enabled
    ready: undefined, // callback on layoutready
    stop: undefined // callback on layoutstop
};


function buildCytoscape() {
    return cytoscape({
        container: document.getElementById('canvas'),
        // these options hide parts of the graph during interaction
        //hideEdgesOnViewport: true,
        //hideLabelsOnViewport: true,

        // this is an alternative that uses a bitmap during interaction
        //textureOnViewport: true,

        // interpolate on high density displays instead of increasing resolution
        //pixelRatio: 1,

        // a motion blur effect that increases perceived performance for little or no cost
        // motionBlur: true,
        style: cytoscape.stylesheet()
            .selector('node')
            .css({
                // define label content and font
                'content': function (node) {
                    if ($('#showVertexLabels').is(':checked')) {
                        return node.data('label');
                    }

                    if(node.data('properties')['vis_label'] != null) {
                        return node.data('properties')['vis_label'];
                    }

                    // var labelString = getLabel(node, vertexLabelKey, useDefaultLabel);
                    //
                    // var properties = node.data('properties');
                    //
                    // if (properties['count'] != null) {
                    //     labelString += ' (' + properties['count'] + ')';
                    // }
                    return "";
                },
                // if the count shall effect the vertex size, set font size accordingly
                'font-size': function (node) {
                    if ($('#showCountAsSize').is(':checked')) {
                        var count = node.data('properties')['count'];
                        if (count != null) {
                            count = count / maxVertexCount;
                            // surface of vertices is proportional to count
                            return Math.max(2, Math.sqrt(count * 10000 / Math.PI));
                        }
                    }
                    return 14;
                },
                'text-valign': 'center',
                'color': 'black',
                // this function changes the text color according to the background color
                // unnecessary atm because only light colors can be generated
                /* function (vertices) {
                 var label = getLabel(vertices, vertexLabelKey, useDefaultLabel);
                 var bgColor = colorMap[label];
                 if (bgColor[0] + bgColor[1] + (bgColor[2] * 0.7) < 300) {
                 return 'white';
                 }
                 return 'black';
                 },*/
                //set background color according to color map
                'background-color': function (node) {
                    //if (node.data('properties')['IsCenter'] != null) {
                        if (node.data('properties')['IsMissing'] == "true") {
                            return "black";
                        } else {
                            if (node.data('properties')['color'] != null) {
                                var col = node.data('properties')['color'];
                                if (col.indexOf(",") == -1) return col;
                                else {
                                    return col.substring(0, col.indexOf(","));
                                }

                            }
                        }
                    //}
                    var label = getLabel(node, getColorKeyForVertices(), useDefaultLabel);
                    var color = colorMap[label + ""];
                    var result = '#';
                    result += ('0' + color[0].toString(16)).substr(-2);
                    result += ('0' + color[1].toString(16)).substr(-2);
                    result += ('0' + color[2].toString(16)).substr(-2);
                    return result;
                },
                'border-width' : function (node) {
                    if(node.data('properties')['IsCenter'] != null &&
                        node.data('properties')['IsCenter'] == "true") return 3;
                    return 0;
                },

                /* size of vertices can be determined by property count
                 count specifies that the vertex stands for
                 1 or more other vertices */
                'width': function (node) {
                    if ($('#showCountAsSize').is(':checked')) {
                        var count = node.data('properties')['count'];
                        if (count != null) {
                            count = count / maxVertexCount;
                            // surface of vertex is proportional to count
                            return Math.sqrt(count * 1000000 / Math.PI) + 'px';
                        }
                    }
                    return getVertexSize() + 'px';
                },
                'height': function (node) {
                    if ($('#showCountAsSize ').is(':checked')) {
                        var count = node.data('properties')['count'];
                        if (count != null) {
                            count = count / maxVertexCount;
                            // surface of vertex is proportional to count
                            return Math.sqrt(count * 1000000 / Math.PI) + 'px';
                        }
                    }
                    return getVertexSize() + 'px';
                },
                'text-wrap': 'wrap'
            })
            .selector('edge')
            .css({
                'opacity' : 0.9,
                //"edge-text-rotation": "autorotate",
                'curve-style': 'bezier',
                'text-opacity': 1.0,
                'font-weight' : 'normal',
                // layout of edge and edge label
                'content': function (edge) {
                    if ($('#showEdgeLabels').is(':checked')) {
                        return edge.data('label');
                    }

                    if(edge.data('properties')['vis_label'] != null) {
                        return edge.data('properties')['vis_label'];
                    }

                    // var labelString = getLabel(edge, edgeLabelKey, useDefaultLabel);
                    //
                    // if(labelString == 'undefined') {
                    //     return '';
                    // }

                    // var properties = edge.data('properties');
                    //
                    // if (properties['count'] != null) {
                    //     labelString += ' (' + properties['count'] + ')';
                    // }

                    return "";
                },
                // if the count shall effect the vertex size, set font size accordingly
                'font-size': function (node) {
                    if ($('#showCountAsSize').is(':checked')) {
                        var count = node.data('properties')['count'];
                        if (count != null) {
                            count = count / maxVertexCount;
                            // surface of vertices is proportional to count
                            return Math.max(2, Math.sqrt(count * 10000 / Math.PI));
                        }
                    }
                    return 14;
                },
                'line-color': function (edge) {
                    if (edge.data('properties')['IsCorrect'] != null) {
                        if (edge.data('properties')['IsMissing'] == "true") {
                            return 'black';
                        } else if (edge.data('properties')['IsCorrect'] == "true") {
                            return 'forestgreen';
                        } else {
                            return 'red';
                        }
                    }
                    return '#999';
                },
                "text-outline-width": 0.3,
                "text-outline-color": "gray",
                //'#999',
                // width of edges can be determined by property count
                // count specifies that the edge represents 1 or more other edges
                'width': function (edge) {
                    if ($('#showCountAsSize').is(':checked')) {
                        var count = edge.data('properties')['count'];
                        if (count != null) {
                            count = count / maxEdgeCount;
                            return Math.sqrt(count * 1000);
                        }
                    }
                    if(edge.data('properties')['Style']=='bold') {
                        return 6;
                    }

                    return 2;
                },
                'line-style' : function(edge) {
                  if(edge.data('properties')['Style']==undefined) {
                      return 'solid';
                  } else {
                      if(edge.data('properties')['Style']=="bold")
                          return 'solid';
                      else return edge.data('properties')['Style'];
                  }
                },
                // 'target-arrow-shape': 'none',
                'target-arrow-color': '#000'
            })
            // properties of edges and vertices in special states, e.g. invisible or faded
            .selector('.faded')
            .css({
                'opacity': 0.25,
                'text-opacity': 0
            })
            .selector('.invisible')
            .css({
                'opacity': 0,
                'text-opacity': 0
            })
            .selector(':parent')
            .css({
                'background-opacity': 0.333
            })
        ,
        ready: function () {
            window.cy = this;
            cy.on('tap', 'node', function (e) {
                var node = e.cyTarget;
                selectedNode = node;
            });
        }
    });
}

var fade = false;
var selectedNode = null;
function fadeUnselected() {
    if (fade == false) fade = true;
    else fade = false;
    if (fade) {
        cy.elements().unselectify();
        /* if a vertex is selected, fade all edges and vertices
         that are not in direct neighborhood of the vertex */
        cy.on('tap', 'node', function (e) {
            var node = e.cyTarget;
            selectedNode = node;
            var neighborhood = node.neighborhood().add(node);

            cy.elements().addClass('faded');
            neighborhood.removeClass('faded');
        });
        // remove fading by clicking somewhere else
        cy.on('tap', function (e) {

            if (e.cyTarget === cy) {
                cy.elements().removeClass('faded');
            }
        });
    } else {
        cy.elements().unselectify();
        cy.on('tap', 'node', function (e) {
        });
        // remove fading by clicking somewhere else
        cy.on('tap', function (e) {
        });
    }
}

function HierarchichalClustering() {
    var minDis= 100000, maxDis=0;//var avgDist = 0.0;
    var nodes = cy.nodes();
    nodes.forEach(function(n1){
        nodes.forEach(function(n2) {
            if(n1.data('id') == n2.data('id')) return;
            var xy1 = n1.position();
            var xy2 = n2.position();
            var x1 = xy1.x;var x2 = xy2.x;
            var y1 = xy1.y;var y2 = xy2.y;
            var a = x1 - x2;
            var b = y1 - y2;

            var c = Math.sqrt( a*a + b*b );
            //avgDist+=c;

            if(c < minDis) minDis = c;
            if(c > maxDis) maxDis = c;
        });
    });
    //avgDist = avgDist/cy.nodes().length;
    var options = {
        distance: "euclidean",
        linkage: "complete",
        attributes: [
            function(node) {
                return node.n('x');
            },
            function(node) {
                return node.position('y');
            }
        ],

        mode: 'regular',
        threshold: minDis*50
    };
    // Run hierarchical on graph nodes
    var clusters = cy.elements().hierarchical( options );

    // Assign random colors to each cluster!
    for (var c = 0; c < clusters.length; c++) {
       clusters[c].style( 'background-color', '#' + Math.floor(Math.random()*16777215).toString(16) );
    }
}

function removeNode() {
    if (selectedNode != null) {
        cy.elements().unselectify();
        cy.remove(selectedNode);
    }
}


/**
 * function called when the server returns the data
 * @param data graph data
 */
function drawGraph(data, finish) {

    // buffer the data to speed up redrawing
    bufferedData = data;
    console.log(data);

    // lists of vertices and edges
    var nodes = data.nodes;
    var edges = data.edges;
    console.log(nodes);

    if(nodes.length>2000) {
        var options = {
            name: 'preset',
            positions: undefined, // map of (node id) => (position obj); or function(node){ return somPos; }
            zoom: undefined, // the zoom level to set (prob want fit = false if set)
            pan: undefined, // the pan level to set (prob want fit = false if set)
            fit: true, // whether to fit to viewport
            padding: 30, // padding on fit
            animate: false, // whether to transition the node positions
            animationDuration: 500, // duration of animation in ms if enabled
            animationEasing: undefined, // easing of animation if enabled
            ready: undefined, // callback on layoutready
            stop: undefined // callback on layoutstop
        };
        cy.layout(options).run();
        return''
    }

    // set conaining all distinct labels (property key specified by vertexLabelKey)
    var labels = new Set();

    // compute maximum count of all vertices, used for scaling the vertex sizes
    maxVertexCount = 0;
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var vertexCount = Number(node['data']['properties']['count']);
        if ((vertexCount != null) && (vertexCount > maxVertexCount)) {
            maxVertexCount = vertexCount;
        }
        if (getColorKeyForVertices() != 'label') {
                labels.add(node['data']['properties'][getColorKeyForVertices()]+"")
        } else {
            labels.add(node['data']['label']);
        }
    }

    // generate random colors for the vertex labels
    generateRandomColors(labels);

    // compute maximum count of all edges, used for scaling the edge sizes
    maxEdgeCount = 0;
    for (var j = 0; j < edges.length; j++) {
        var edge = edges[j];
        var edgeCount = Number(edge['data']['properties']['count']);
        if ((edgeCount != null) && (edgeCount > maxEdgeCount)) {
            maxEdgeCount = edgeCount;
        }
    }

    // update vertex and edge count after sampling
    // document.getElementById("svcount").innerHTML = nodes.length;
    // document.getElementById("secount").innerHTML = edges.length;

    cy.elements().remove();
    cy.add(nodes);
    cy.add(edges);

    if ($('#hideNullGroups').is(':checked')) {
        hideNullGroups();
    }

    if ($('#hideDisconnected').is(':checked')) {
        hideDisconnected();
    }

    if ($('#hideEdges').is(':checked')) {
        hideEdges();
    }
    addQtip();
    cy.layout(chooseLayout());

    if(cy.nodes().length != 0) {
        if (cy.nodes()[0].data("properties")["ClusterId"] != null) {
            var clusteridsselect = $('#clusterids');
            var uniqueClusterIds = {};
            cy.nodes().forEach(function (n) {
                var colors = n.data("properties")["color"];
                if (colors.indexOf(",") != -1) {
                    var styles = {};
                    var colorsSplitted = colors.split(",");
                    var size = 100 / colorsSplitted.length;
                    for (var i = 0; i < colorsSplitted.length; i++) {
                        styles["pie-" + (i+1) + "-background-color"] = colorsSplitted[i];
                        styles["pie-" + (i+1) + "-background-size"] = "" + size;
                    }
                    n.style(styles);
                }
                uniqueClusterIds[n.data('properties').ClusterId] = 0;
            });
            Object.keys(uniqueClusterIds).forEach(function (t) {
                clusteridsselect.append("<option>"+t+"</option>");
            });
        }
    }
    changed = false;
    // hide the loading gif
    finish();
}

/**
 * Function for initializing the database propertyKeys menu. Adds on-click listener to the elements.
 * @param databases list of all available databases
 */
function initializeDatabaseMenu(databases) {
    var categoriesSelect = $('#categories');
    Object.keys(databases).forEach(function (d) {
        categoriesSelect.append('<option>' + d + '</option>');
    });
    categoriesSelect.on('change', function () {
        var category = getSelectedCategory();
        var databaseSelect = $('#databases');
        databaseSelect.empty();
        databases[category].forEach(function (d) {
            databaseSelect.append('<option>' + d + '</option>');
        })
    });
}

/**
 * Function for initializing the samplings menu.
 * @param samplings list of all available sampling methods
 */
function initializeSamplingsMenu(samplings) {
    var samplingsSelect = $('#samplings');
    for (var i = 0; i < samplings.length; i++) {
        var name = samplings[i];
        samplingsSelect.append('<option>' + name + '</option>');
    }
}

/**
 * Initialize the property key menus, the filter menus, the aggregate function selects and the
 * checkboxes.
 * @param keys vertex and edge property keys
 */
function initializeOtherMenus(keys) {
    document.getElementById("loading").style.visibility = 'hidden';
    $("#collapseComponents").empty();
    $("#collapseLayouts").empty();
    $("#collapseMulti").empty();
    $("#edgeKeys").empty();
    keys.vertexLabels.forEach(function (vl) {
        $("#collapseComponents").append("<div class='checkbox'>" +
            "<label><input type='checkbox' value=''>" + vl + "</label></div>");
    });

    keys.edgeLabels.forEach(function (vl) {
        $("#collapseLayouts").append("<div class='checkbox'>" +
            "<label><input type='checkbox' value=''>" + vl + "</label></div>");
    });

    keys.vertexKeys.forEach(function (vl) {
        $("#collapseMulti").append("<div class='checkbox'>" +
            "<label><input type='checkbox' value=''>" + vl.name + "</label></div>");
    });

    keys.edgeKeys.forEach(function (vl) {
        $("#edgeKeys").append("<div class='checkbox'>" +
            "<label><input type='checkbox' value=''>" + vl.name + "</label></div>");

    });

    // $('#exec').show();
    // $('#wholeGraph').show();
    // $('#sampleGraph').show();
    // $('.show').show();
    // $('.redraw').on('click', redrawIfNotChanged);
    //
    // $('.label').show();
    //
    // initializeFilterKeyMenus(keys);
    // initializePropertyKeyMenus(keys);
    // initializeAggregateFunctionMenus(keys);
}

/**
 * Check if the selected property keys, filters and aggregation functions have changed. If not, redraw the graph.
 * This is implemented to ensure that no accidental redraws happen.
 */
function redrawIfNotChanged() {
    if (!changed) {
        useDefaultLabel = false;
        useForceLayout = true;
        drawGraph(bufferedData);
    }
}

/**
 * initialize the filter menus with the labels
 * @param keys labels of the input vertices
 */
function initializeFilterKeyMenus(keys) {

    var vertexFilters = $('#vertexFilters');
    var edgeFilters = $('#edgeFilters');

    // show the instructions
    vertexFilters.find('.instruction').show();
    edgeFilters.find('.instruction').show();

    // clear previous entries
    vertexFilters.find('.multiSel').children().remove();
    edgeFilters.find('.multiSel').children().remove();

    var vertexFilterSelect = vertexFilters.find('dd .multiSelect ul').empty();
    var edgeFilterSelect = edgeFilters.find('dd .multiSelect ul').empty();

    // add one entry per vertex label
    for (var i = 0; i < keys.vertexLabels.length; i++) {
        var vertexLabel = keys.vertexLabels[i];

        vertexFilterSelect.append(
            '<li><input type="checkbox" value="' + vertexLabel + '"' +
            ' class="checkbox"/>' + vertexLabel + '</li>');
    }

    // add one entry per vertex label
    for (var j = 0; j < keys.edgeLabels.length; j++) {
        var edgeLabel = keys.edgeLabels[j];

        edgeFilterSelect.append(
            '<li><input type="checkbox" value="' + edgeLabel + '"' +
            ' class="checkbox"/>' + edgeLabel + '</li>');
    }


    // on click, select filters
    vertexFilters.find('.checkbox').on('click', elementSelected);
    vertexFilters.find('.checkbox').on('click', vertexFilterSelected);
    edgeFilters.find('.checkbox').on('click', elementSelected);
    edgeFilters.find('.checkbox').on('click', edgeFilterSelected);

    edgeFilterSelect.append('<li><input type="checkbox" value="NONE"' +
        ' class="checkbox"/>NONE</li>');

    edgeFilterSelect.find('.checkbox[value=NONE]').on('click', NONEFilterSelected);

    vertexFilters.show();
    edgeFilters.show();
}

/**
 * Initialize the key propertyKeys menus.
 * @param keys array of vertex and edge keys
 */
function initializePropertyKeyMenus(keys) {

    // get the propertyKeys menus in their current form
    var vertexPropertyKeys = $('#vertexPropertyKeys');
    var edgePropertyKeys = $('#edgePropertyKeys');

    // remove the current keys from the property key menus
    var vertexSelect = vertexPropertyKeys.find('dd .multiSelect ul').empty();
    var edgeSelect = edgePropertyKeys.find('dd .multiSelect ul').empty();

    // add the label option, which is always possible
    var vertexLabelHtml = '' +
        '<li><input type ="checkbox" value ="label" class="checkbox"/> label</li>';

    vertexSelect.append(vertexLabelHtml);

    // add the other options
    for (var i1 = 0; i1 < keys.vertexKeys.length; i1++) {
        var vertexKey = keys.vertexKeys[i1];
        var vertexPropertyLabel = '&lt;';
        // insert an entry into the vertex filter map
        createVertexFilterMapEntry(vertexKey);
        for (var j1 = 0; j1 < vertexKey.labels.length; j1++) {
            vertexPropertyLabel += vertexKey.labels[j1];
            if (j1 < vertexKey.labels.length - 1) {
                vertexPropertyLabel += ', ';
            }
        }
        vertexPropertyLabel += '&gt;.' + vertexKey.name;
        var vertexHtml =
            '<li><input type="checkbox" value="' + vertexKey.name + '" ' +
            ' class="checkbox"/>' + vertexPropertyLabel + '</li>';
        vertexSelect.append(vertexHtml);
    }

    // add the label option, which is always possible
    var edgeLabelHtml = '' +
        '<li><input type ="checkbox" value ="label" class="checkbox"/> label</li>';

    edgeSelect.append(edgeLabelHtml);

    // add the other options
    for (var i = 0; i < keys.edgeKeys.length; i++) {
        var edgeKey = keys.edgeKeys[i];
        var edgePropertyLabel = '&lt;';
        // insert an entry into the edge filter map
        createEdgeFilterMapEntry(edgeKey);
        for (var j = 0; j < edgeKey.labels.length; j++) {
            edgePropertyLabel += edgeKey.labels[j];
            if (j < edgeKey.labels.length - 1) {
                edgePropertyLabel += ', ';
            }
        }
        edgePropertyLabel += '&gt;.' + edgeKey.name;
        var edgeHtml =
            '<li><input type="checkbox" value="' + edgeKey.name + '" ' +
            ' class="checkbox"/>' + edgePropertyLabel + '</li>';
        edgeSelect.append(edgeHtml);
    }

    // show the propertyKeys menus
    vertexPropertyKeys.show();
    edgePropertyKeys.show();

    // remove the currently selected keys, they are saved in <span> elements
    vertexPropertyKeys.find('.multiSel').children().remove();
    edgePropertyKeys.find('.multiSel').children().remove();

    // show instructions
    vertexPropertyKeys.find('.instruction').show();
    edgePropertyKeys.find('.instruction').show();

    // add click listener
    vertexPropertyKeys.find('.checkbox').on('click', elementSelected);
    vertexPropertyKeys.find('.checkbox').on('click', setVertexLabel);
    edgePropertyKeys.find('.checkbox').on('click', elementSelected);
    edgePropertyKeys.find('.checkbox').on('click', setEdgeLabel);

}

/**
 * function that is executed if a key is selected from any of the 2 propertyKeys boxes
 */

function elementSelected() {
    changed = true;
    var title = $(this).val();
    var propertyKeys = $(this).closest('.dropDown');

    // if a key is selected, add it as a span to the title of the property keys box
    // else make the instruction visible
    if ($(this).is(':checked')) {
        var html = '<span title="' + title + '">' + title + '</span>';
        propertyKeys.find('.multiSel').append(html);
        propertyKeys.find('.instruction').hide();
    } else {
        var multiSel = propertyKeys.find('.multiSel');
        multiSel.find('span[title="' + title + '"]').remove();
        if (multiSel.children().length == 0) propertyKeys.find('.instruction').show();
    }
}

/**
 * create a new entry in the vertex filter map via a vertex key
 * @param vertexKey key that shall be inserted into the map
 */
function createVertexFilterMapEntry(vertexKey) {

    var vertexKeyObject = {};

    vertexKeyObject.name = vertexKey.name;
    vertexKeyObject.support = 0;

    for (var i = 0; i < vertexKey.labels.length; i++) {
        var label = vertexKey.labels[i];
        vertexKeyObject.support++;
        if (vertexFilterMap[label] == undefined) {
            var array = [];
            array.push(vertexKeyObject);
            vertexFilterMap[label] = array;
        } else {
            vertexFilterMap[label].push(vertexKeyObject);
        }
    }
}

/**
 * create a new entry in the edge filter map via an edge key
 * @param edgeKey key that shall be inserted into the map
 */
function createEdgeFilterMapEntry(edgeKey) {

    var edgeKeyObject = {};

    edgeKeyObject.name = edgeKey.name;
    edgeKeyObject.support = 0;

    for (var i = 0; i < edgeKey.labels.length; i++) {
        var label = edgeKey.labels[i];
        edgeKeyObject.support++;
        if (edgeFilterMap[label] == null) {
            var array = [];
            array.push(edgeKeyObject);
            edgeFilterMap[label] = array;
        } else {
            edgeFilterMap[label].push(edgeKeyObject);
        }
    }
}

/**
 * set the first selected property as vertex label
 * also make it bold in the selection
 */
function setVertexLabel() {
    var selectedLabels = $(this).closest('.dropDown').find('.multiSel span');
    selectedLabels.each(function () {
        $(this).css('font-weight', 'normal');
    });
    selectedLabels.first().css('font-weight', 'bold');
    vertexLabelKey = selectedLabels.first().text();
}

/**
 * set the first selected property as edge label
 * also make it bold in the selection
 */
function setEdgeLabel() {
    var selectedLabels = $(this).closest('.dropDown').find('.multiSel span');
    selectedLabels.each(function () {
        $(this).css('font-weight', 'normal');
    });
    selectedLabels.first().css('font-weight', 'bold');
    edgeLabelKey = selectedLabels.first().text();
}

/**
 * function to hide properties of filtered elements
 */

function vertexFilterSelected() {
    var label, keyObject, propertyKeys, aggrFuncs;
    if ($(this).is(':checked')) {
        label = $(this).val();
        for (var i = 0; i < vertexFilterMap[label].length; i++) {

            keyObject = vertexFilterMap[label][i];
            keyObject.support++;

            propertyKeys = '#vertexPropertyKeys';
            enablePropertyKey(propertyKeys, keyObject);


            aggrFuncs = '#vertexAggrFuncs';
            enableAggrFunc(aggrFuncs, keyObject);
        }
    } else {
        label = $(this).val();
        for (var j = 0; j < vertexFilterMap[label].length; j++) {

            keyObject = vertexFilterMap[label][j];
            keyObject.support--;

            if (keyObject.support == 0) {
                propertyKeys = '#vertexPropertyKeys';
                disablePropertyKey(propertyKeys, keyObject);


                aggrFuncs = '#vertexAggrFuncs';
                disableAggrFunc(aggrFuncs, keyObject);
            }
        }
    }
}

/**
 * function called when a edge filter was selected
 */
function edgeFilterSelected() {
    var label, keyObject, propertyKeys, aggrFuncs;
    if ($(this).is(':checked')) {
        label = $(this).val();
        for (var i = 0; i < edgeFilterMap[label].length; i++) {

            keyObject = edgeFilterMap[label][i];
            keyObject.support++;

            propertyKeys = '#edgePropertyKeys';
            enablePropertyKey(propertyKeys, keyObject);


            aggrFuncs = '#edgeAggrFuncs';
            enableAggrFunc(aggrFuncs, keyObject);
        }
    } else {
        label = $(this).val();
        for (var j = 0; j < edgeFilterMap[label].length; j++) {

            keyObject = edgeFilterMap[label][j];
            keyObject.support--;

            if (keyObject.support == 0) {
                propertyKeys = '#edgePropertyKeys';
                disablePropertyKey(propertyKeys, keyObject);

                aggrFuncs = '#edgeAggrFuncs';
                disableAggrFunc(aggrFuncs, keyObject);
            }
        }
    }
}

/**
 * Enable a property key in the dropdown menu
 * @param dropdown the dropdown menu
 * @param keyObject the property key object
 */
function enablePropertyKey(dropdown, keyObject) {
    var name = keyObject.name;
    var checkbox = $(dropdown)
        .find('dd .multiSelect ul li input[value = "' + name + '"]')
        .attr('disabled', false);
    checkbox.parent().css('color', 'black');

}

/**
 * Disable a property key in the dropdown menu
 * @param dropdown the dropdown menu
 * @param keyObject the property key object
 */
function disablePropertyKey(dropdown, keyObject) {

    var propertyDropdown = $(dropdown);

    var name = keyObject.name;
    var checkbox = propertyDropdown
        .find('dd .multiSelect ul li input[value = "' + name + '"]')
        .attr('disabled', true);
    checkbox.attr('checked', false);
    checkbox.parent().css('color', 'grey');

    propertyDropdown.find('span[title="' + name + '"]').remove();

    if (propertyDropdown.find('.multiSel').children().length == 0)
        propertyDropdown.find('.instruction').show();
}

/**
 * Enable a aggregation functions of a property key in the dropdown menu
 * @param dropdown the dropdown menu
 * @param keyObject the property key
 */
function enableAggrFunc(dropdown, keyObject) {
    var name = keyObject.name;
    for (var i = 0; i < aggrPrefixes.length; i++) {
        var aggrName = aggrPrefixes[i] + name;
        var checkbox = $(dropdown)
            .find('dd .multiSelect ul li input[value = "' + aggrName + '"]')
            .attr('disabled', false);
        checkbox.parent().css('color', 'black');
    }
}

/**
 * Disable the aggregation functions of a property key in the dropdown menu
 * @param dropdown the dropdown menu
 * @param keyObject the property key
 */
function disableAggrFunc(dropdown, keyObject) {

    var propertyDropdown = $(dropdown);

    var name = keyObject.name;

    for (var i = 0; i < aggrPrefixes.length; i++) {

        var aggrName = aggrPrefixes[i] + name;

        var checkbox = propertyDropdown
            .find('dd .multiSelect ul li input[value = "' + aggrName + '"]')
            .attr('disabled', true);
        checkbox.attr('checked', false);
        checkbox.parent().css('color', 'grey');

        propertyDropdown.find('span[title="' + aggrName + '"]').remove();

        if (propertyDropdown.find('.multiSel').children().length == 0)
            propertyDropdown.find('.instruction').show();
    }

}

/**
 * initialize the aggregate function propertyKeys menu
 */
function initializeAggregateFunctionMenus(keys) {
    var vertexAggrFuncs = $('#vertexAggrFuncs');
    var edgeAggrFuncs = $('#edgeAggrFuncs');

    // remove the current keys from the property key menus
    var vertexSelect = vertexAggrFuncs.find('dd .multiSelect ul').empty();
    var edgeSelect = edgeAggrFuncs.find('dd .multiSelect ul').empty();

    // add count as default aggregation function
    var vertexLabelHtml = '' +
        '<li><input type ="checkbox" value ="count" class="checkbox"/> count</li>';

    vertexSelect.append(vertexLabelHtml);

    // add aggregation functions for each numerical property
    for (var i1 = 0; i1 < keys.vertexKeys.length; i1++) {
        var vertexKey = keys.vertexKeys[i1];
        if (vertexKey.numerical == true) {
            for (var j1 = 0; j1 < aggrPrefixes.length; j1++) {
                var vertexAggrFunc = aggrPrefixes[j1] + vertexKey.name;
                var vertexHtml = '<li><input type="checkbox" value="' + vertexAggrFunc +
                    '" class="checkbox"/>' + vertexAggrFunc + '</li>';
                vertexSelect.append(vertexHtml);
            }
        }
    }

    // add count as default aggregation function
    var edgeLabelHtml = '<li><input type ="checkbox" value ="count" class="checkbox"/>count</li>';

    edgeSelect.append(edgeLabelHtml);

    // add aggregation functions for each numerical property
    for (var i2 = 0; i2 < keys.edgeKeys.length; i2++) {
        var edgeKey = keys.edgeKeys[i2];
        if (edgeKey.numerical == true) {
            for (var j2 = 0; j2 < aggrPrefixes.length; j2++) {
                var edgeAggrFunc = aggrPrefixes[j2] + edgeKey.name;
                var edgeHtml = '<li><input type="checkbox" value="' + edgeAggrFunc +
                    '" class="checkbox"/>' + edgeAggrFunc + '</li>';
                edgeSelect.append(edgeHtml);
            }
        }
    }

    // show the propertyKeys menus
    vertexAggrFuncs.show();
    edgeAggrFuncs.show();

    // remove the currently selected keys, they are saved in <span> elements
    vertexAggrFuncs.find('.multiSel').children().remove();
    edgeAggrFuncs.find('.multiSel').children().remove();

    // show instructions
    vertexAggrFuncs.find('.instruction').show();
    edgeAggrFuncs.find('.instruction').show();

    vertexAggrFuncs.find('.checkbox').on('click', elementSelected);
    edgeAggrFuncs.find('.checkbox').on('click', elementSelected);
}

/**
 * hide elements when the page is loaded for the first time
 */
function hideElements() {
    $('#loading').hide();
    $('.label').hide();
    $('.show').hide();
    $('.dropDown').hide();
    $('.aggrFuncs').hide();
}

/**
 * get the selected database
 * @returns selected database name
 */
function getSelectedCategory() {
    return $('#categories').find('option:selected').text();
}

function selectAClusterId() {
    $('#clusterids').empty();
    var sel = $('#clusterids').find('option:selected').text();
    var ns = [];
    cy.nodes().forEach(function(n){
        if(n.data('properties').ClusterId == sel) {
            ns.push(n);
        }
    });
    cy.fit(ns);
    cy.zoom({
        level:1.2,
        position:ns[0].position()
    });
}

/**
 * get the selected database
 * @returns selected database name
 */
function getSelectedDatabase() {
    return $('#databases').find('option:selected').text();
}

/**
 * get the selected database
 * @returns selected database name
 */
function getSelectedSampling() {
    return $('#samplings').find('option:selected').text();
}

/**
 * get the selected layout
 * @returns selected layout name
 */
function getSelectedLayout() {
    return $('#layouts').find('option:selected').text();
}


/**
 * get the selected sampling
 * @returns selected sampling name
 */
function getSamplingThreshold() {
    return document.getElementById('sampling_threshold').value;
}

/**
 * get the selected sampling
 * @returns selected sampling name
 */
function getIterationLayout() {
    return document.getElementById('iterations_layout').value;
}

/**
 * get the selected sampling
 * @returns selected sampling name
 */
function getClusterSizes() {
    return document.getElementById('cluster_size').value;
}

/**
 * get the given cluster id
 * @returns given cluster id
 */
function getClusterId() {
    return document.getElementById('cluster_id').value;
}

/**
 * get the global vertex size
 * @returns given vertex size
 */
function getVertexSize() {
    return document.getElementById('vertex_size').value;
}

/**
 * get the edge opacity
 * @returns given edge opacity
 */
function getEdgeOpacity() {
    return document.getElementById('edge_opacity').value;
}

/**
 * get the color key for vertices
 * @returns the color ley for the vertices
 */
function getColorKeyForVertices() {
    var tmp = document.getElementById('color_keys').value;
    tmp = tmp.substr(0,tmp.indexOf(","));
    return tmp;
}

/**
 * get the color key for vertices
 * @returns the color ley for the vertices
 */
function getColorKeyForEdges() {
    var tmp = document.getElementById('color_keys').value;
    tmp = tmp.substr(tmp.indexOf(",")+1);
    return tmp;
}

/**
 *  * get the selected vertex keys
 * @returns selected vertex keys
 */
function getSelectedVertexKeys() {
    return $.map(
        $('#vertexPropertyKeys').find('dt a .multiSel span'),
        function (item) {
            return $(item).text();
        });
}

/**
 * get the selected edge keys
 * @returns selected edge keys
 */
function getSelectedEdgeKeys() {
    return $.map(
        $('#edgePropertyKeys').find('dt a .multiSel span'),
        function (item) {
            return $(item).text();
        });
}

/**
 * get the selected vertex aggregate function
 * @returns  name of the selected vertex aggregate function
 */
function getSelectedVertexAggregateFunctions() {
    return $.map(
        $('#vertexAggrFuncs').find('dt a .multiSel span'),
        function (item) {
            return $(item).text();
        });
}

/**
 * get the selected edge aggregate function
 * @returns name of the selected edge aggregate function
 */
function getSelectedEdgeAggregateFunctions() {
    return $.map(
        $('#edgeAggrFuncs').find('dt a .multiSel span'),
        function (item) {
            return $(item).text();
        });
}

/**
 * get the selected vertex filters
 * @returns {Array} selected edge filters
 */
function getSelectedVertexFilters() {
    return $.map(
        $('#vertexFilters').find('dt a .multiSel span'),
        function (item) {
            return $(item).text();
        });
}

/**
 * get the selected edge filters
 * @returns {Array} selected edge filters
 */
function getSelectedEdgeFilters() {
    return $.map(
        $('#edgeFilters').find('dt a .multiSel span'),
        function (item) {
            return $(item).text();
        });
}

function isNONEFilterSelected() {
    var filters = $('#edgeFilters').find('dt a .multiSel span[title="NONE"]');
    return filters.length != 0;

}

/**
 * Checks if a grouping request is valid (all fields are set).
 * @param request
 * @returns {boolean} true, if the request was valid
 */
function isValidRequest(request) {
    return ((request.dbName != 'Select a database') &&
    (request.vertexKeys.length > 0));
}

/**
 * Reset the page on reload
 */
// function resetPage() {
//     $('#showEdgeLabels').prop('checked', false);
//     $('#showCountAsSize').prop('checked', false);
//     $('#hideNullGroups').prop('checked', false);
//     $('#hideDisconnected').prop('checked', false);
//     $('#hideEdges').prop('checked', false);
//     $('#databases').val('default');
// }

/**
 * Generate a random color for each label
 * @param labels array of labels
 */
function generateRandomColors(labels) {
    colorMap = {};
    labels.forEach(function (label) {
        var r = 0;
        var g = 0;
        var b = 0;
        while (r + g + b < 382) {
            r = Math.floor((Math.random() * 255));
            g = Math.floor((Math.random() * 255));
            b = Math.floor((Math.random() * 255));
        }
        colorMap[label] = [r, g, b];
    });
}

/**
 * Get the label of the given element, either the default label ('label') or the value of the
 * given property key
 * @param element the element whose label is needed
 * @param key key of the non-default label
 * @param useDefaultLabel boolean specifying if the default label shall be used
 * @returns {string} the label of the element
 */
function getLabel(element, key, useDefaultLabel) {
    var label = '';
    if (key != 'label') {
        label += element.data('properties')[key]+"";
    } else {
        label += element.data('label');
    }
    return label;
}

function chooseLayout() {
// options for the force layout
    var radialRandom = {
        name: 'preset',
        positions: function (node) {
            var r = Math.random() * 1000001;
            var theta = Math.random() * 2 * (Math.PI);
            return {
                x: Math.sqrt(r) * Math.sin(theta),
                y: Math.sqrt(r) * Math.cos(theta)
            };
        },
        zoom: undefined,
        pan: undefined,
        fit: true,
        padding: 30,
        animate: false,
        animationDuration: 500,
        animationEasing: undefined,
        ready: undefined,
        stop: undefined
    };

    if (getSelectedLayout().toString().indexOf("on server") != -1) {
        return preset;
    } else if (getSelectedLayout().toString() == "FR layout") {
        return cose;
    } else {
        return radialRandom;
    }
}

/**
 * Add a custom Qtip to the vertices and edges of the graph.
 */
function addQtip() {
    cy.elements().qtip({
        content: function () {
            var qtipText = '';
            for (var key in this.data()) {
                if (key != 'properties' && key != 'pie_parameters') {
                    qtipText += key + ' : ' + this.data(key) + '<br>';

                }
            }
            var properties = this.data('properties');
            for (var property in properties) {
                if (properties.hasOwnProperty(property)) {
                    qtipText += property + ' : ' + properties[property] + '<br>';
                }
            }
            return qtipText;
        },
        position: {
            my: 'top center',
            at: 'bottom center'
        },
        style: {
            classes: 'MyQtip'
        }
    });
}

/**
 * Hide all vertices and edges, that have a NULL property.
 */
function hideNullGroups() {
    var vertexKeys = getSelectedVertexKeys();
    var edgeKeys = getSelectedEdgeKeys();

    var nodes = cy.nodes();
    var edges = cy.edges();


    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        for (var j = 0; j < vertexKeys.length; j++) {
            if (node.data().properties[vertexKeys[j]] == 'NULL') {
                node.remove();
                break;
            }
        }
    }

    for (var k = 0; k < edges.length; k++) {
        var edge = edges[k];
        for (var l = 0; l < edgeKeys.length; l++) {
            if (edge.data().properties[edgeKeys[l]] == 'NULL') {
                edge.remove();
                break;
            }
        }
    }
}

/**
 * Function to hide all disconnected vertices (vertices without edges).
 */
function hideDisconnected() {
    var nodes = cy.nodes();

    for (var i = 0; i < nodes.length; i++) {

        var node = nodes[i];
        if (cy.edges('[source="' + node.id() + '"]').length == 0) {
            if (cy.edges('[target="' + node.id() + '"]').length == 0) {
                node.remove();
            }
        }
    }
}

/**
 * Function that is called when the NONE filter is selected
 */
function NONEFilterSelected() {

    var edgeFilters = $('#edgeFilters');
    var edgePropertyKeys = $('#edgePropertyKeys');
    var edgeAggrFuncs = $('#edgeAggrFuncs');
    var multiSel = edgeFilters.find('dt a .multiSel');

    // check if the NONE filters check box is checked
    if ($(this).is(':checked')) {
        // disable all other edge filters
        edgeFilters.find('.checkbox[value!="NONE"]')
            .attr('disabled', true)
            .attr('checked', false);

        // disable all edge property keys and edge aggregation functions
        edgePropertyKeys.find('.checkbox')
            .attr('disabled', true);

        edgeAggrFuncs.find('.checkbox')
            .attr('disabled', true);

        // remove all currently selected edge filters, hide instructions and show NONE filter insted
        multiSel.find('span:not(.instruction)').remove();
        edgeFilters.find('.instruction').hide();
        multiSel.append('<span title="NONE">NONE</span>');

    } else {
        // enable all edge filters, edge property keys and edge aggregation functions
        edgeFilters.find('.checkbox')
            .attr('disabled', false);

        edgePropertyKeys.find('.checkbox')
            .attr('disabled', false);

        edgeAggrFuncs.find('.checkbox')
            .attr('disabled', false);

        // hide the NONE filter span and show the instructions
        multiSel.find('span[title="NONE"]').remove();
        edgeFilters.find('.instruction').show();
    }
}

/**
 * hide all edges, at the moment this is never called
 */
function hideEdges() {
    cy.edges().remove();
}

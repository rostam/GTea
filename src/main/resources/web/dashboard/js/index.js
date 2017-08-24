/**
 * Prefixes of the aggregation functions
 */
var aggrPrefixes = ['min ', 'max ', 'sum '];

/**
 * Maps of (label) to (property key, number of supporting labels)
 * used to make unsupported property keys diabled
 * @type {Object<String, {String, Integer}>}
 */
var vertexFilterMap = {};
var edgeFilterMap = {};

/**
 * Property keys that are used to specify the vertex and edge labels.
 */
var vertexLabelKey;
var edgeLabelKey;

/**
 * Map of all possible values for the vertexLabelKey to a color in RGB format.
 * @type {{}}
 */
var colorMap = {};

var bufferedData;

/**
 * Boolean specifying if the graph was changed, used to dodge unnecessary redraws.
 * @type {boolean}
 */
var changed;

/**
 * The cytoscape graph object
 */
var cy;

/**
 * True, if the graph layout should be force based
 * @type {boolean}
 */
var useForceLayout = true;

/**
 * True, if the default label should be used
 * @type {boolean}
 */
var useDefaultLabel = true;

/**
 * Maximum value for the count attribute of vertices
 * @type {number}
 */
var maxVertexCount = 0;

/**
 * Maximum value for the count attribute of edges
 * @type {number}
 */
var maxEdgeCount = 0;


/**
 * Runs when the DOM is ready
 */

//var serverAddr = "http://139.18.13.41:2342/";
var serverAddr = "http://localhost:2342/";
// var serverAddr = "http://139.18.17.183:2342/";

$(document).ready(function () {
    // $.get(serverAddr + 'databases/')
    //     .done(initializeDatabaseMenu)
    //     .fail(function (jqXHR, textStatus, errorThrown) {
    //         alert(errorThrown);
    //     });
    //
    // $.get(serverAddr + 'samplings/')
    //     .done(initializeSamplingsMenu)
    //     .fail(function (jqXHR, textStatus, errorThrown) {
    //         alert(errorThrown);
    //     });
});
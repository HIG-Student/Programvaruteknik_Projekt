/**
 *
 */
function calculate(xyValues) {
    var xValues = [xyValues.length];
    var yValues = [xyValues.length];
    for (var i = 0; i < xyValues.length; i++) {
        xValues[i] = xyValues[i][0];
        yValues[i] = xyValues[i][1];
    }
    var nbrOfElements = xyValues.length;
    var xSum = getSumOf(xValues);
    var ySum = getSumOf(yValues);
    var xySum = getSumOfArrayNodeProducts(xValues, yValues);
    var xSquaredSum = getSumOfArrayNodeProducts(xValues, xValues);
    var ySquaredSum = getSumOfArrayNodeProducts(yValues, yValues);
    function getSumOfArrayNodeProducts(aValues, bValues) {
        var sum = 0;
        for (var i = 0; i < nbrOfElements; i++) {
            sum += aValues[i] * bValues[i];
        }
        return sum;
    }
    function getSumOf(values) {
        var sum = 0;
        for (var i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }
    function getKValue() {
        return ((nbrOfElements * xySum) - (xSum * ySum)) /
            ((nbrOfElements * xSquaredSum) - (xSum * xSum));
    }
    function getMValue() {
        return ((xSquaredSum * ySum) - (xSum * xySum)) /
            ((nbrOfElements * xSquaredSum) - (xSum * xSum));
    }
    function getRValue() {
        return (Math.pow(((nbrOfElements * xySum) - (xSum * ySum)), 2)) /
            (((nbrOfElements * xSquaredSum) - (xSum * xSum)) * ((nbrOfElements * ySquaredSum) - (ySum * ySum)));
    }
    return {
        "m": getMValue(),
        "k": getKValue(),
        "r": getRValue(),
    };
}
//# sourceMappingURL=regressionCalculator.js.map
var expectedMValue;
var expectedKValue;
var expectedRValue;
var xyValues;
describe("testRegressionWithSomeRisingValues", function () {
    xyValues = [[0, 3], [1, 4], [3, 5], [4, 7], [5, 8], [7, 9], [9, 11], [10, 13], [15, 14], [18, 15], [19, 17], [20, 19]];
    expectedMValue = 3.7650273224043715;
    expectedKValue = 0.7190961453256535;
    expectedRValue = 0.9632129660496855;
    runTests();
});
describe("testRegressionWithAConstantValues", function () {
    xyValues = [[0, 0], [1, 1], [2, 2], [3, 3], [4, 4], [5, 5], [6, 6], [7, 7], [8, 8], [9, 9], [10, 10]];
    expectedMValue = 0;
    expectedKValue = 1;
    expectedRValue = 1;
    runTests();
});
function runTests() {
    it("returns the m value of the linear function.", function () {
        expect(calculate(xyValues).m).toBe(expectedMValue);
    });
    it("returns the k value of the linear function.", function () {
        expect(calculate(xyValues).k).toBe(expectedKValue);
    });
    it("returns the r value of the linear function.", function () {
        expect(calculate(xyValues).R).toBe(expectedRValue);
    });
}
//# sourceMappingURL=testRegression.js.map
/*
 * This tests the regression calculator with different scenarios.
 */

describe("testWithSomeRisingValues", function() 
{
    var xyValues = [[0, 3], [1, 4], [3, 5], [4, 7], [5, 8], [7, 9], [9, 11], [10, 13], [15, 14], [18, 15], [19, 17], [20, 19]];

    var m = 3.7650273224043715;
    var k = 0.7190961453256535;
    var r = 0.9632129660496855;

    runTests(xyValues, m, k, r);
})

describe("testWithAConstantValues", function() 
{
    var xyValues = [[0, 0], [1, 1], [2, 2], [3, 3], [4, 4], [5, 5], [6, 6], [7, 7], [8, 8], [9, 9], [10, 10]];

    var m = 0;
    var k = 1;
    var r = 1;

    runTests(xyValues, m, k, r);
})

describe("testWithNullGaps", function() 
{
    var xyValues = [[0, 0], [null, null], [2, 2], [3, 3], [4, 4], [null, null], [6, 6], [7, 7], [null, null], [9, 9], [10, 10]];

    var m = 0;
    var k = 1;
    var r = 1;

    runTests(xyValues, m, k, r);
})

describe("testWithSingleXaxisNullGaps", function() 
{
    var xyValues = [[0, 0], [null, 1], [2, 2], [null, 3], [4, 4], [5, 5], [6, 6], [7, 7], [8, 8], [9, 9], [10, 10]];

    var m = 0;
    var k = 1;
    var r = 1;

    runTests(xyValues, m, k, r);
})

describe("testWithSingleYaxisNullGaps", function() 
{
    var xyValues = [[0, 0], [1, null], [2, 2], [3, null], [4, 4], [5, 5], [6, 6], [7, 7], [8, 8], [9, 9], [10, 10]];

    var m = 0;
    var k = 1;
    var r = 1;

    runTests(xyValues, m, k, r);
})

describe("testWithSingleNullinDifferentXandYslots", function() 
{
    var xyValues = [[0, 0], [1, null], [2, 2], [3, null], [4, 4], [null, 5], [6, 6], [null, 7], [8, 8], [9, 9], [10, 10]];

    var m = 0;
    var k = 1;
    var r = 1;

    runTests(xyValues, m, k, r);
})

describe("testOnlyNullValuesInArray", function() 
{
    var xyValues = [[null, null], [null, null], [null, null], [null, null]];

    var m = 0;
    var k = 1;
    var r = 1;

    runTests(xyValues, m, k, r);
})

describe("testMValueOrientation", function() 
{
    var xyValues = [[0, 2], [1, 4], [2, 6], [3, 8]];
    var m = 2;
    testM(xyValues, m);
    
    var xyValues = [[0, 5], [1, 5]];
    var m = 5;
    var xyValues = [[0, 0.00004], [1, 0.00004]];
    var m = 0.00004;
    testM(xyValues, m);
    
    var xyValues = [[0, -7], [1, -7]];
    var m = -7;
    testM(xyValues, m);
    
    var xyValues = [[3, 25], [7, 25]];
    var m = 25;
    testM(xyValues, m);
    
    var xyValues = [[0, 7.5], [1, 7.5]];
    var m = 7.5;
})

function testM(xyValues, m)
{
    it("Tests that the calculated m value is the expected m: " + m, function() 
    {
        expect(calculate(xyValues).m).toBe(m);
    });
}

function testK(xyValues, k) 
{
    it("Tests that the calculated k value is the expected k: " + k, function() 
    {
        expect(calculate(xyValues).k).toBe(k);
    });
}

function testR(xyValues, r) 
{
    it("Tests that the calculated r value is the expected r: " + r, function() 
    {
        expect(calculate(xyValues).r).toBe(r);
    });
}

function runTests(xyValues, m, k, r)
{
    testM(xyValues, m);
    testK(xyValues, k);
    testR(xyValues, r);
}


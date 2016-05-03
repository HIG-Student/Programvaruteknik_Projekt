module.exports = function(config) {
    config.set({
        basePath: '../../../../',
        frameworks: 
		[
			"jasmine"
		],
        files: 
		[
			"WebContent/node_modules/jasmine-core/lib/jasmine-core/jasmine.js",
			"WebContent/node_modules/karma-jasmine/lib/boot.js",
			"WebContent/node_modules/karma-jasmine/lib/adapter.js",
			
			"WebContent/WEB-INF/karma/*.js"
        ],
        exclude: 
		[
			"WebContent/WEB-INF/karma/karma.conf*.js"
		],
        reporters: 
		[
			"progress"
		],
        port: 9876,
        logLevel: config.LOG_INFO,
        browsers: 
		[
			"PhantomJS"
		],
        singleRun: false,
        autoWatch: true,
        plugins: 
		[
            "karma-jasmine",
            "karma-phantomjs-launcher"
        ]
    });
};
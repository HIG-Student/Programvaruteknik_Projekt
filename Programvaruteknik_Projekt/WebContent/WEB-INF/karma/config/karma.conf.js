module.exports = function(config) {
    config.set({
        basePath: '../../..',
        frameworks: 
		[
			"jasmine"
		],
        files: 
		[
            "WebContent/app/*.js",
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
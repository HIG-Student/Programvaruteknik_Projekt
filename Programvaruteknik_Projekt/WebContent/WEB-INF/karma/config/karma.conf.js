module.exports = function(config) {
    config.set({
        basePath: '../../../../',
        frameworks: 
		[
			"systemjs",
			"jasmine"
		],
        files: 
		[
			"WebContent/node_modules/systemjs/dist/system-polyfills.js",
			
			"WebContent/node_modules/jasmine-core/lib/jasmine-core/jasmine.js",
			"WebContent/node_modules/karma-jasmine/lib/boot.js",
			"WebContent/node_modules/karma-jasmine/lib/adapter.js",
			
			"WebContent/app/**.js",
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
        logLevel: config.LOG_DEBUG,
        browsers: 
		[
			"PhantomJS"
		],
        singleRun: false,
        autoWatch: true,
        plugins: 
		[
		 	"karma-systemjs",
            "karma-jasmine",
            "karma-phantomjs-launcher"
        ],
        
        systemjs: 
        {
        	// Path to your SystemJS configuration file 
        	configFile: 'WebContent/WEB-INF/karma/config/system.conf.js',
        	config: 
        	{
        		paths: 
                {
        			'WebContent': 'WebContent',
        			'app': 'WebContent/app'
                }
        	}
        }
    
    });
};